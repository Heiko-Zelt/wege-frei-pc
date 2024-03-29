package de.heikozelt.wegefrei.delivery.email

import de.heikozelt.wegefrei.db.DatabaseRepo
import de.heikozelt.wegefrei.email.EmailAddressEntity
import de.heikozelt.wegefrei.email.useragent.EmailAttachment
import de.heikozelt.wegefrei.email.useragent.EmailMessage
import de.heikozelt.wegefrei.email.useragent.Outbox
import de.heikozelt.wegefrei.db.entities.NoticeEntity
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.json.Witness
import de.heikozelt.wegefrei.model.ValidationException
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.nio.file.Paths
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.JOptionPane

/**
 * Instead of using a dedicated outbox table/collection,
 * the notices table is filtered for notices, which are ready to be sent.
 * Each message/notice has a counter, which stores the number of failed sent attempts.
 * Notices with lower sent failures are sent first.
 * @param app reference is needed to restart thread and to update notices table
 * (and maybe update notice frame, but notice can't be edited anyway if it is finalized/in outbox or sent)
 * todo: Prio 1 for testability change callbacks to app: WegeFrei
 * replace class app with interface or add listeners
 */
class NoticesOutbox(/*private val app: WegeFrei*/) : Outbox<Int> {
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
     * private var current: NoticeEntity? = null
     */

    /**
     * callbacks
     */
    private var successfulSentListener: ((Int, ZonedDateTime) -> Unit)? = null

    private var restartListener: (() -> Unit)? = null

    fun setSuccessfulSentListener(listener: (Int, ZonedDateTime) -> Unit) {
        successfulSentListener = listener
    }

    fun setRestartListener(listener: () -> Unit) {
        restartListener = listener
    }


    fun setDatabaseRepo(dbRepo: DatabaseRepo) {
        this.dbRepo = dbRepo
    }

    fun setSettings(settings: Settings) {
        this.settings = settings
    }

    override fun next(): EmailMessage<Int>? {
        log.debug("next()")
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
     *
     * Problem: Zuordnung von EmailMessage zu NoticeEntity
     * <ul>
     *   <li>gewählte Lösung: EmailMessage wird um externe (geheime/private) ID/Notice-ID erweitert.</li>
     *   <li>alternative Lösung 1: Vor jedem Sendeversuch wird die Message-ID in der Datenbank eingetragen (performt schlecht).
     *     MessageID kann nicht Sendezeitpunkt enthalten. :-(</li>
     *   <li>alternative Lösung 2: Outbox merkt sich, welche Meldung (Email-Nachricht) dran war. auch gut.</li>
     * </ul>
     */
    override fun sentSuccessfulCallback(externalID: Int, sentTime: ZonedDateTime, messageID: ByteArray) {
        log.debug("sentSucessfulCallback(externalID=${externalID}, ...)")
        dbRepo?.updateNoticeSent(externalID, sentTime, messageID)
        // todo Prio 1: In NoticesFrame die Meldung im Cache invalidieren/in der Tabelle aktualisieren!!!
        // todo Prio 1: replace with listener pattern
        //app.updateNoticeSend(externalID, sentTime)
        successfulSentListener?.let { it(externalID, sentTime) }
    }

    override fun sendFailedCallback(externalID: Int, exception: Throwable) {
        log.debug("sendFailedCallback(externalID=${externalID}, ...)")
        dbRepo?.updateNoticeSendFailed(externalID)
        EventQueue.invokeLater {
            val options = arrayOf("Abbrechen", "Fortfahren/Erneut versuchen")
            val result = JOptionPane.showOptionDialog(
                null, exception.message, "Fehler beim E-Mail senden",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]
            );
            when (result) {
                // Problem: restart Thread. Has the Thread finished yet?
                // usually the background thread should be faster than the GUI showing a window, user reading an error message and clicking a button.
                1 -> {
                    log.debug("user pressed continue")
                    // todo Prio 1: replace with listener pattern
                    //app.startSendingEmails()
                    restartListener?.let { it() }
                }

                else -> log.debug("user pressed cancel")
            }
        }

        /*
        val msg = exception.message ?: "unbekannter Fehler"
        val optionFrame = OptionFrame("Fehler beim E-Mail senden", msg)
        optionFrame.addOption("Abbrechen") { log.debug("user canceled") }
        optionFrame.addOption("Fortfahren/erneut versuchen") {
            log.debug("user wants to try again")
        }
        optionFrame.isVisible = true
        externalID?.let {
            dbRepo?.updateNoticeSendFailed(it)
        }
        */
    }

    fun buildEmailMessage(noticeEntity: NoticeEntity): EmailMessage<Int>? {
        log.debug("buildEmailMessage()")
        settings?.let { setti ->
            val nID = noticeEntity.id ?: 0 // neue oder bestehende Meldung
            val from = EmailAddressEntity(setti.witness.emailAddress, setti.witness.getFullName())
            // todo Prio 3: mehrere Empfänger erlauben
            val to = noticeEntity.getRecipient()
            val tos = TreeSet<EmailAddressEntity>()
            tos.add(to)
            val subject = buildSubject(noticeEntity)
            val content = buildMailContent(noticeEntity, setti.witness)
            val message = EmailMessage<Int>(nID, from, tos, subject, content)
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
            LOG.debug("buildSubject()")
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
            LOG.debug("buildMailContent()")

            fun htmlEncode(str: String?): String {
                str?.let {
                    return it
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                }
                return ""
            }

            fun appendTableRow(tableRows: MutableList<String>, label: String, value: String?) {
                if (!value.isNullOrBlank()) {
                    tableRows.add("<tr><td>$label:</td><td>${htmlEncode(value)}</td></tr>")
                }
            }

            fun appendTableRowHtmlValue(tableRows: MutableList<String>, label: String, value: String?) {
                if (!value.isNullOrBlank()) {
                    tableRows.add("<tr><td>$label:</td><td>$value</td></tr>")
                }
            }

            val validationErrors = w.validate()
            validationErrors.addAll(n.isComplete())
            if (validationErrors.isNotEmpty()) throw ValidationException(validationErrors)

            val caseRows = mutableListOf<String>()
            appendTableRow(caseRows, "Landeskennzeichen", n.getCountryFormatted())
            appendTableRow(caseRows, "Kennzeichen", n.licensePlate)
            appendTableRow(caseRows, "Fahrzeugart", n.vehicleType)
            appendTableRow(caseRows, "Marke", n.vehicleMake)
            appendTableRow(caseRows, "Farbe", n.color)
            appendTableRow(caseRows, "Tatortadresse", n.getAddress())
            appendTableRow(caseRows, "Tatortbeschreibung", n.locationDescription)
            appendTableRow(caseRows, "Stadtteil", n.quarter)
            appendTableRow(caseRows, "Geoposition", n.getGeoPositionFormatted())
            appendTableRow(caseRows, "Verstoß", n.offense)
            appendTableRowHtmlValue(caseRows, "Umstände", n.getCircumstancesHtml())
            appendTableRow(caseRows, "HU-Fälligkeit", n.getInspectionMonthYear())
            // todo Prio 3: Wochentag einfügen, wegen Werktags-Beschränkungen
            appendTableRow(caseRows, "Tatzeit", n.getObservationTimeFormatted())
            appendTableRow(caseRows, "Tatende", n.getEndTimeFormatted())
            appendTableRow(caseRows, "Dauer", n.getDurationFormatted())
            appendTableRow(caseRows, "Hinweis", n.note)
            val caseTableRows = caseRows.joinToString("\n")

            val witnessRows = mutableListOf<String>()
            appendTableRow(witnessRows, "Name", w.getFullName())
            appendTableRow(witnessRows, "Adresse", w.getAddress())
            appendTableRow(witnessRows, "E-Mail", w.emailAddress)
            appendTableRow(witnessRows, "Telefon", w.telephoneNumber)
            val witnessTableRows = witnessRows.joinToString("\n")

            val attachmentsSection = if (n.photoEntities.isEmpty()) {
                ""
            } else {
                val sb = StringBuilder()
                sb.append("<h1>Anlagen</h1>")
                sb.append("<ol>")
                n.getPhotoEntitiesSorted()
                    .forEach { sb.append("<li>${it.getFilename()} (SHA1: ${it.getHashHex()})</li>") }
                sb.append("</ol>")
            }

            val content = """
              <html>
                <p>Sehr geehrte Damen und Herren,</p>
                <p>hiermit zeige ich, mit der Bitte um Weiterverfolgung, folgende Verkehrsordnungswidrigkeit an:</p>
                <h1>Falldaten</h1>
                <table>
                $caseTableRows
                </table>
                <h1>Zeuge</h1>
                <table>
                $witnessTableRows
                </table>
                $attachmentsSection
                <h1>Erklärung</h1>
                <p>Hiermit bestätige ich, dass ich die Datenschutzerklärung zur Kenntnis genommen habe und ihr zustimme.
                  Meine oben gemachten Angaben einschließlich meiner Personalien sind zutreffend und vollständig.
                  Als Zeuge bin ich zur wahrheitsgemäßen Aussage und auch zu einem möglichen Erscheinen vor Gericht verpflichtet.
                  Vorsätzlich falsche Angaben zu angeblichen Ordnungswidrigkeiten können eine Straftat darstellen.
                  Ich weiß, dass mir die Kosten des Verfahrens und die Auslagen des Betroffenen auferlegt werden,
                  wenn ich vorsätzlich oder leichtfertig eine unwahre Anzeige erstatte.</p>
                <p>Beweisfotos, aus denen Kennzeichen und Tatvorwurf erkennbar hervorgehen, befinden sich im Anhang.
                  Bitte prüfen Sie den Sachverhalt auch auf etwaige andere Verstöße, die aus den Beweisfotos zu ersehen sind.</p>
                <p>Bitte bestätigen Sie Ihre Zuständigkeit und den Erhalt dieser Anzeige mit der Zusendung des Aktenzeichens
                  an ${w.emailAddress}.
                  Falls Sie nicht zuständig sein sollten, leiten Sie bitte meine Anzeige weiter und informieren Sie mich darüber.
                  Sie dürfen meine persönlichen Daten auch weiterleiten und diese für die Dauer des Verfahrens speichern.</p>
                <p>Diese E-Mail wurde mit <a href="https://heikozelt.de/wegefrei/">Wege frei!</a> erstellt.</p>
                <p>Mit freundlichen Grüßen</p>
                <p>${w.getFullName()}</p>
              </html>""".trimIndent()
            val doc = Jsoup.parse(content)
            doc.outputSettings().indentAmount(2)
            val pretty = doc.toString()
            LOG.debug("html content:\n$pretty")
            return pretty
        }
    }
}