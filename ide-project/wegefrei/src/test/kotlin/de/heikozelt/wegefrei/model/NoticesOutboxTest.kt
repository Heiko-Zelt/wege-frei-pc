package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.delivery.email.NoticesOutbox
import de.heikozelt.wegefrei.email.EmailAddressEntity
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.json.Witness
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.ZonedDateTime

class NoticesOutboxTest {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    // todo Prio 1: running one_next_success() & one_next_send_failed() together fails. One test has side effects on other test.

    @Test
    fun buildMailContent_ok() {
        val noticeEntity = NoticeEntity()
        noticeEntity.apply {
            id = 123;
            vehicleMake = "AMG"
            vehicleAbandoned = true
            licensePlate = "K OT 1234"
            color = VehicleColor.COLORS[1].colorName
            locationDescription = "Köln Hauptbahnhof"
            endangering = true
            offense = "Parken am Taxenstand"
            recipientEmailAddress = "hz@heikozelt.de"
            observationTime = ZonedDateTime.of(2022, 1, 1, 12, 1, 0, 0, ZoneId.of("Europe/Berlin"))
            finalizedTime = ZonedDateTime.of(2022, 1, 1, 23, 58, 0, 0, ZoneId.of("Europe/Berlin"))
        }
        val witness = Witness()
        witness.apply {
            emailAddress = "hz@heikozelt.de"
            givenName = "Heiko"
            surname = "Zelt"
            street = "Bahnhofstraße 1"
            zipCode = "12345"
            town = "Köln"
        }
        val content = NoticesOutbox.buildMailContent(noticeEntity, witness)
        log.debug(content)
        val expected = """
        <html>
          <head></head>
          <body>
            <p>Sehr geehrte Damen und Herren,</p>
            <p>hiermit zeige ich, mit der Bitte um Weiterverfolgung, folgende Verkehrsordnungswidrigkeit an:</p>
            <h1>Falldaten</h1>
            <table>
              <tbody>
                <tr>
                  <td>Kennzeichen:</td>
                  <td>K OT 1234</td>
                </tr>
                <tr>
                  <td>Marke:</td>
                  <td>AMG</td>
                </tr>
                <tr>
                  <td>Farbe:</td>
                  <td>Weiß</td>
                </tr>
                <tr>
                  <td>Tatortbeschreibung:</td>
                  <td>Köln Hauptbahnhof</td>
                </tr>
                <tr>
                  <td>Verstoß:</td>
                  <td>Parken am Taxenstand</td>
                </tr>
                <tr>
                  <td>Umstände:</td>
                  <td>Das Fahrzeug war verlassen<br>
                    mit Gefährdung</td>
                </tr>
                <tr>
                  <td>Beobachtungszeit:</td>
                  <td>01.01.2022, 12:01 MEZ</td>
                </tr>
                <tr>
                  <td>Beobachtungsdauer:</td>
                  <td>weniger als 1 Minute</td>
                </tr>
              </tbody>
            </table>
            <h1>Zeuge</h1>
            <table>
              <tbody>
                <tr>
                  <td>Name:</td>
                  <td>Heiko Zelt</td>
                </tr>
                <tr>
                  <td>Adresse:</td>
                  <td>Bahnhofstraße 1, 12345 Köln</td>
                </tr>
                <tr>
                  <td>E-Mail:</td>
                  <td>hz@heikozelt.de</td>
                </tr>
              </tbody>
            </table>
            <h1>Erklärung</h1>
            <p>Hiermit bestätige ich, dass ich die Datenschutzerklärung zur Kenntnis genommen habe und ihr zustimme. Meine oben gemachten Angaben einschließlich meiner Personalien sind zutreffend und vollständig. Als Zeuge bin ich zur wahrheitsgemäßen Aussage und auch zu einem möglichen Erscheinen vor Gericht verpflichtet. Vorsätzlich falsche Angaben zu angeblichen Ordnungswidrigkeiten können eine Straftat darstellen. Ich weiß, dass mir die Kosten des Verfahrens und die Auslagen des Betroffenen auferlegt werden, wenn ich vorsätzlich oder leichtfertig eine unwahre Anzeige erstatte.</p>
            <p>Beweisfotos, aus denen Kennzeichen und Tatvorwurf erkennbar hervorgehen, befinden sich im Anhang. Bitte prüfen Sie den Sachverhalt auch auf etwaige andere Verstöße, die aus den Beweisfotos zu ersehen sind.</p>
            <p>Bitte bestätigen Sie Ihre Zuständigkeit und den Erhalt dieser Anzeige mit der Zusendung des Aktenzeichens an hz@heikozelt.de. Falls Sie nicht zuständig sein sollten, leiten Sie bitte meine Anzeige weiter und informieren Sie mich darüber. Sie dürfen meine persönlichen Daten auch weiterleiten und diese für die Dauer des Verfahrens speichern.</p>
            <p>Diese E-Mail wurde mit <a href="https://heikozelt.de/wegefrei/">Wege frei!</a> erstellt.</p>
            <p>Mit freundlichen Grüßen</p>
            <p>Heiko Zelt</p>
          </body>
        </html>""".trimIndent()
        assertEquals(expected, content)
    }

    @Test
    fun buildMailContent_witness_givenName_missing() {
        val noticeEntity = NoticeEntity()
        noticeEntity.apply {
            id = 123;
            vehicleMake = "AMG"
            vehicleAbandoned = true
            licensePlate = "K OT 1234"
            color = VehicleColor.COLORS[1].colorName
            locationDescription = "Köln Hauptbahnhof"
            endangering = true
            offense = "Parken am Taxenstand"
            observationTime = ZonedDateTime.of(2022, 1, 1, 12, 1, 0, 0, ZoneId.of("Europe/Berlin"))
            finalizedTime = ZonedDateTime.of(2022, 1, 1, 23, 58, 0, 0, ZoneId.of("Europe/Berlin"))
            recipientEmailAddress = "hz@heikozelt.de"
        }
        val witness = Witness()
        witness.apply {
            emailAddress = "hz@heikozelt.de"
            surname = "Zelt"
            street = "Bahnhofstraße 1"
            zipCode = "12345"
            town = "Köln"
        }

        val exception =
            assertThrows(ValidationException::class.java) { NoticesOutbox.buildMailContent(noticeEntity, witness) }
        exception.validationErrors.forEach {
            log.debug("validation error: $it")
        }
        assertEquals(1, exception.validationErrors.size)
        assertEquals("Dein Vorname als Zeug_in fehlt.", exception.validationErrors[0])
    }

    @Test
    fun buildMailContent_notice_offense_missing() {
        val noticeEntity = NoticeEntity()
        noticeEntity.apply {
            id = 123;
            vehicleMake = "AMG"
            vehicleAbandoned = true
            licensePlate = "K OT 1234"
            color = VehicleColor.COLORS[1].colorName
            locationDescription = "Köln Hauptbahnhof"
            endangering = true
            observationTime = ZonedDateTime.of(2022, 1, 1, 12, 1, 0, 0, ZoneId.of("Europe/Berlin"))
            finalizedTime = ZonedDateTime.of(2022, 1, 1, 23, 58, 0, 0, ZoneId.of("Europe/Berlin"))
            recipientEmailAddress = "hz@heikozelt.de"
        }
        val witness = Witness()
        witness.apply {
            emailAddress = "hz@heikozelt.de"
            givenName = "Heiko"
            surname = "Zelt"
            street = "Bahnhofstraße 1"
            zipCode = "12345"
            town = "Köln"
        }

        val exception =
            assertThrows(ValidationException::class.java) { NoticesOutbox.buildMailContent(noticeEntity, witness) }
        exception.validationErrors.forEach {
            log.debug("validation error: $it")
        }
        assertEquals(1, exception.validationErrors.size)
        assertEquals("Ein Verstoß muss angeben sein.", exception.validationErrors[0])
    }

    @Test
    fun zero_next() {
        val dbRepo = DatabaseRepo.fromMemory()
        val outbox = NoticesOutbox()
        outbox.setDatabaseRepo(dbRepo)
        val message = outbox.next()
        assertNull(message)
        dbRepo.close()
    }

    @Test
    fun zero_next_dbRepo_missing() {
        val outbox = NoticesOutbox()
        val message = outbox.next()
        assertNull(message)
    }

    @Test
    fun one_next_success() {
        val dbRepo = DatabaseRepo.fromMemory()
        val settings = Settings()
        settings.witness = Witness("Heiko", "Zelt", "hz@heikozelt.de", "Bahnhofstraße 1", "12345", "Köln")
        val outbox = NoticesOutbox()
        outbox.setDatabaseRepo(dbRepo)
        outbox.setSettings(settings)

        val obsTime = ZonedDateTime.of(2022, 1, 1, 2, 58, 0, 0, ZoneId.of("Europe/Berlin"))
        val createTime = ZonedDateTime.of(2022, 1, 1, 23, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val finalTime = ZonedDateTime.of(2022, 1, 1, 23, 59, 34, 0, ZoneId.of("Europe/Berlin"))
        val sentTime = ZonedDateTime.of(2022, 1, 2, 1, 2, 59, 0, ZoneId.of("Europe/Berlin"))
        val mID = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)

        val notice1 = NoticeEntity()
        notice1.apply {
            observationTime = obsTime
            createdTime = createTime
            licensePlate = "SE NT 0001"
            vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1].toString()
            color = VehicleColor.COLORS[1].colorName
            latitude = 50.1
            longitude = 8.1
            offense = "Parken im Rhein"
            recipientEmailAddress = "ordnungsamt@junit-test-gemeinde.de"
            finalizedTime = finalTime
        }
        dbRepo.insertNotice(notice1)

        val ids = dbRepo.findAllNoticesIdsDesc()
        log.debug("number of ids: ${ids.size}")
        log.debug("first id: ${ids[0]}")

        val notices = dbRepo.findAllNoticesDesc()
        log.debug("number of notices: ${notices.size}")
        val n = notices[0];
        log.debug("id: ${n.id}")
        log.debug("observation time: ${n.observationTime}")
        log.debug("created time: ${n.createdTime}")
        log.debug("finalized time: ${n.finalizedTime}")

        val message1 = outbox.next()
        assertNotNull(message1)
        assertEquals(1, message1?.externalID)
        assertEquals(1, message1?.tos?.size)
        assertEquals(EmailAddressEntity("ordnungsamt@junit-test-gemeinde.de"), message1?.tos?.first())
        // todo more assertions

        message1?.let {
            outbox.sentSuccessfulCallback(message1.externalID, sentTime, mID)
        }
        // todo create EMailMessage interface with 2 implementations MimeEmailMessage & EmailMessageMock:

        val message2 = outbox.next()
        assertNull(message2)
        dbRepo.close()
    }

    @Test
    fun one_next_witness_missing() {
        val dbRepo = DatabaseRepo.fromMemory()
        val settings = Settings()
        val outbox = NoticesOutbox()
        outbox.setDatabaseRepo(dbRepo)
        outbox.setSettings(settings)

        val obsTime = ZonedDateTime.of(2022, 1, 1, 2, 58, 0, 0, ZoneId.of("Europe/Berlin"))
        val createTime = ZonedDateTime.of(2022, 1, 1, 23, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val finalTime = ZonedDateTime.of(2022, 1, 1, 23, 59, 34, 0, ZoneId.of("Europe/Berlin"))

        val notice1 = NoticeEntity()
        notice1.apply {
            observationTime = obsTime
            createdTime = createTime
            licensePlate = "SE NT 0001"
            vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1].toString()
            color = VehicleColor.COLORS[1].colorName
            latitude = 50.1
            longitude = 8.1
            offense = "Parken auf Motorradparkplatz. Ich konnte mein Krad dort nicht abstellen."
            recipientEmailAddress = "ordnungsamt@junit-test-gemeinde.de"
            finalizedTime = finalTime
        }
        dbRepo.insertNotice(notice1)

        val ids = dbRepo.findAllNoticesIdsDesc()
        log.debug("number of ids: ${ids.size}")
        log.debug("first id: ${ids[0]}")

        val notices = dbRepo.findAllNoticesDesc()
        log.debug("number of notices: ${notices.size}")
        val n = notices[0];
        log.debug("id: ${n.id}")
        log.debug("observation time: ${n.observationTime}")
        log.debug("created time: ${n.createdTime}")
        log.debug("finalized time: ${n.finalizedTime}")

        val exception = assertThrows(ValidationException::class.java) { outbox.next() }
        exception.validationErrors.forEach {
            log.debug("validation error: $it")
        }
        assertEquals(1, exception.validationErrors.size)
        assertEquals(exception.validationErrors[0], "Bitte gib Deine Zeugen-Daten an.")
        dbRepo.close()
    }

    @Test
    fun one_next_send_failed() {
        val dbRepo = DatabaseRepo.fromMemory()
        val settings = Settings()
        settings.witness = Witness("Heiko", "Zelt", "hz@heikozelt.de", "Bahnhofstraße 1", "12345", "Köln")
        val outbox = NoticesOutbox()
        outbox.setDatabaseRepo(dbRepo)
        outbox.setSettings(settings)

        val obsTime = ZonedDateTime.of(2022, 1, 1, 2, 58, 0, 0, ZoneId.of("Europe/Berlin"))
        val createTime = ZonedDateTime.of(2022, 1, 1, 23, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val finalTime = ZonedDateTime.of(2022, 1, 1, 23, 59, 34, 0, ZoneId.of("Europe/Berlin"))

        val notice1 = NoticeEntity()
        notice1.apply {
            observationTime = obsTime
            createdTime = createTime
            licensePlate = "SE NT 0001"
            vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1].toString()
            color = VehicleColor.COLORS[1].colorName
            latitude = 50.1
            longitude = 8.1
            offense = "Parken auf Grünfläche"
            recipientEmailAddress = "ordnungsamt@junit-test-gemeinde.de"
            finalizedTime = finalTime
        }
        dbRepo.insertNotice(notice1)

        val ids = dbRepo.findAllNoticesIdsDesc()
        log.debug("number of ids: ${ids.size}")
        log.debug("first id: ${ids[0]}")

        val message1 = outbox.next()
        assertNotNull(message1)
        assertEquals(1, message1?.externalID)
        assertEquals(1, message1?.tos?.size)
        assertEquals(EmailAddressEntity("ordnungsamt@junit-test-gemeinde.de"), message1?.tos?.first())
        // todo more assertions

        message1?.let {
            outbox.sendFailedCallback(it.externalID, Exception("what ever went wrong"))
        }

        val notices = dbRepo.findAllNoticesDesc()
        log.debug("number of notices: ${notices.size}")
        val n = notices[0];
        log.debug("id: ${n.id}")
        log.debug("observation time: ${n.observationTime}")
        log.debug("created time: ${n.createdTime}")
        log.debug("finalized time: ${n.finalizedTime}")

        assertNull(n.sentTime)
        assertEquals(1, n.sendFailures)

        val message2 = outbox.next()
        assertNotNull(message2)
        assertEquals(1, message2?.externalID)
        dbRepo.close()
    }

    @Test
    fun two_next_success_success() {
        // todo: Test schreiben
    }

    @Test
    fun two_next_failed() {
        // todo: Test schreiben
    }

}