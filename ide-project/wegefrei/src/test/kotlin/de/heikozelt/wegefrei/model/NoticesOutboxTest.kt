package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.DatabaseRepo
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
        print(content)
        assertTrue(content.contains("<html>"))
        assertTrue(content.contains("Sehr geehrte Damen und Herren"))
        // todo: weitere asserts
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
            vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1]
            color = VehicleColor.COLORS[1].colorName
            latitude = 50.1
            longitude = 8.1
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
        // todo more assertions

        message1?.let {
            it.sentTime = sentTime
            it.messageID = mID
            outbox.sendCallback(it, true)
        }
        val message2 = outbox.next()
        assertNull(message2)
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
            vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1]
            color = VehicleColor.COLORS[1].colorName
            latitude = 50.1
            longitude = 8.1
            offense = "Parken auf Motorradparkplatz. Ich konnte mein Krad dort nicht abstellen."
            recipientEmailAddress = "hz@heikozelt.de"
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
    }

    @Test
    fun one_next_failed() {
        // todo: Test schreiben
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