package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.email.EmailAddressEntity
import de.heikozelt.wegefrei.email.useragent.EmailAttachment
import de.heikozelt.wegefrei.email.useragent.EmailMessage
import de.heikozelt.wegefrei.email.useragent.Outbox
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.json.Witness
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Instead of using a dedicated outbox table/collection,
 * the notices table is filtered for notices, which are ready to be sent.
 * Each message/notice has a counter, which stores the number of failed sent attempts.
 * Notices with lower sent failures are sent first.
 */
class NoticesOutbox : Outbox {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * stop sending
     * (because sending failed, user was asked how to continue and user canceled)
     */
    private var cancel = false

    private var dbRepo: DatabaseRepo? = null

    private var settings: Settings? = null

    /**
     * remember, which notice is currently being sent.
     * to be able to update status in database as sent or increment failure counter
     */
    private var current: NoticeEntity? = null

    fun setDatabaseRepo(dbRepo: DatabaseRepo) {
        this.dbRepo = dbRepo
    }

    fun setSettings(settings: Settings) {
        this.settings = settings
    }

    override fun next(): EmailMessage? {
        return if (cancel) {
            null
        } else {
            val notice = dbRepo?.findNextNoticeToSend()
            if (notice == null) {
                null
            } else {
                buildEmailMessage(notice)
            }
        }
    }

    /**
     * is called by EmailSender after a message was sent or an error occurred
     */
    override fun sendCallback(sendSuccess: Boolean) {
        if (sendSuccess) {
           /*
            * todo: Problem: Zuordnung von EmailMessage zu NoticeEntity
            * gewählte Lösung: Outbox merkt sich, welche Meldung (Email-Nachricht) dran war.
            * alternative Lösung 2: Vor jedem Sendeversuch wird die Message-ID in der Datenbank eingetragen (perfomt schlecht).
            * alternative Lösung 3: EmailMessage wird um externe (geheime/private) ID/Notice-ID erweitert.
            * updateNotice statt dessen verwenden
            */
            dbRepo?.updateNoticeSent(message)
            // todo Email als gesendet in der Datenbank markieren
        } else { // wenn Senden nicht erfolgreich:
            // Abbruchkriterium? Ask user: retry/continue Erneut versuchen/weiter senden? or Cancel/Abbrechen?
        }
    }

    fun buildEmailMessage(noticeEntity: NoticeEntity): EmailMessage? {
        log.debug("sendEmail()")
        settings?.let { setti ->
            val from = EmailAddressEntity(setti.witness.emailAddress, setti.witness.getFullName())
            // todo Prio 3: mehrere Empfänger erlauben
            val to = noticeEntity.getRecipient()
            val tos = TreeSet<EmailAddressEntity>()
            tos.add(to)
            val subject = buildSubject(noticeEntity)
            val content = buildMailContent(noticeEntity, setti.witness)
            val message = EmailMessage(from, tos, subject, content)
            if (from.address != to.address) {
                message.ccs.add(from)
            }

            /*
            selectedPhotosListModel.getSelectedPhotos()
            */
            noticeEntity.photoEntities.forEach { pe ->
                pe.path?.let { pa ->
                    val attachment = EmailAttachment(Paths.get(pa))
                    message.attachments.add(attachment)
                }
            }
            return message
        }
        return null
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        fun buildSubject(n: NoticeEntity): String {
            var subject = "Anzeige"
            n.observationTime?.let {
                val formatter = DateTimeFormatter.ofPattern("d. MMM, HH:mm").withLocale(Locale.GERMAN)
                val formatted = it.format(formatter)
                subject += " $formatted"
            }
            n.licensePlate?.let { lic ->
                subject += ", $lic"
            }
            return subject
        }


        fun buildMailContent(n: NoticeEntity, w: Witness): String {
            fun htmlEncode(str: String?): String {
                str?.let {
                    return it
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                }
                return ""
            }

            fun tableRow(label: String, value: String?): String {
                return if (value.isNullOrBlank()) {
                    ""
                } else {
                    "|    <tr><td>$label:</td><td>${htmlEncode(value)}</td></tr>\n"
                }
            }

            fun tableRowHtmlValue(label: String, value: String?): String {
                return if (value.isNullOrBlank()) {
                    ""
                } else {
                    "|    <tr><td>$label:</td><td>$value</td></tr>\n"
                }
            }

            val countryRow = tableRow("Landeskennzeichen", n.getCountryFormatted())
            val licensePlateRow = tableRow("Kennzeichen", n.licensePlate)
            val makeRow = tableRow("Marke", n.vehicleMake)
            val colorRow = tableRow("Farbe", n.color)
            val offenseAddressRow = tableRow("Tatortadresse", n.getAddress())
            val locationDescriptionRow = tableRow("Tatortbeschreibung", n.locationDescription)
            val positionRow = tableRow("Geoposition", n.getGeoPositionFormatted())
            val offenseRow = tableRow("Verstoß", n.offense)
            val circumstancesRow = tableRowHtmlValue("Umstände", n.getCircumstancesHtml())
            val inspectionDateRow = tableRow("HU-Fälligkeit", n.getInspectionMonthYear())
            // todo: Wochentag einfügen, wegen Werktags-Beschränkungen
            val observationTimeRow = tableRow("Beobachtungszeit", n.getObservationTimeFormatted())
            val observationDurationRow = tableRow("Beobachtungsdauer", n.getDurationFormatted())
            val noteRow = tableRow("Hinweis", n.note)

            val nameRow = tableRow("Name", w.getFullName())
            val witnessAddressRow = tableRow("Adresse", w.getAddress())
            val witnessEmailRow = tableRow("E-Mail", w.emailAddress)
            val telephoneRow = tableRow("Telefon", w.telephoneNumber)

            val attachmentsSection = if (n.photoEntities.isEmpty()) {
                ""
            } else {
                val sb = StringBuilder()
                sb.append("|  <h1>Anlagen</h1>\n")
                sb.append("|  <ol>\n")
                n.getPhotoEntitiesSorted()
                    .forEach { sb.append("|    <li>${it.getFilename()} (SHA1: ${it.getHashHex()})</li>\n") }
                sb.append("|  </ol>\n")
            }

            val content = """
              |<html>
              |  <p>Sehr geehrte Damen und Herren,</p>
              |  <p>hiermit zeige ich, mit der Bitte um Weiterverfolgung, folgende Verkehrsordnungswidrigkeit an:</p>
              |  <h1>Falldaten</h1>
              |  <table>
              $countryRow$licensePlateRow$makeRow$colorRow$offenseAddressRow$locationDescriptionRow$positionRow$offenseRow$circumstancesRow$inspectionDateRow$observationTimeRow$observationDurationRow$noteRow
              |  </table>  
              |  <h1>Zeuge</h1>
              |  <table>
              $nameRow$witnessAddressRow$witnessEmailRow$telephoneRow  
              |  </table>
              $attachmentsSection
              |  <h1>Erklärung</h1>
              |  <p>Hiermit bestätige ich, dass ich die Datenschutzerklärung zur Kenntnis genommen habe und ihr zustimme.
              |    Meine oben gemachten Angaben einschließlich meiner Personalien sind zutreffend und vollständig.
              |    Als Zeuge bin ich zur wahrheitsgemäßen Aussage und auch zu einem möglichen Erscheinen vor Gericht verpflichtet.
              |    Vorsätzlich falsche Angaben zu angeblichen Ordnungswidrigkeiten können eine Straftat darstellen.
              |    Ich weiß, dass mir die Kosten des Verfahrens und die Auslagen des Betroffenen auferlegt werden,
              |    wenn ich vorsätzlich oder leichtfertig eine unwahre Anzeige erstatte.</p>
              |  <p>Beweisfotos, aus denen Kennzeichen und Tatvorwurf erkennbar hervorgehen, befinden sich im Anhang.
              |    Bitte prüfen Sie den Sachverhalt auch auf etwaige andere Verstöße, die aus den Beweisfotos zu ersehen sind.</p>
              |  <p>Bitte bestätigen Sie Ihre Zuständigkeit und den Erhalt dieser Anzeige mit der Zusendung des Aktenzeichens an hz@heikozelt.de.
              |    Falls Sie nicht zuständig sein sollten, leiten Sie bitte meine Anzeige weiter und informieren Sie mich darüber.
              |    Sie dürfen meine persönlichen Daten auch weiterleiten und diese für die Dauer des Verfahrens speichern.</p>                                  
              |  <p>Mit freundlichen Grüßen</p>
              |  <p>${w.getFullName()}</p>
              |</html>""".trimMargin()
            LOG.debug("html content:\n$content")
            return content
        }
    }
}