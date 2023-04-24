package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.entities.PhotoEntity
import de.heikozelt.wegefrei.model.VehicleColor
import de.heikozelt.wegefrei.model.VehicleMakesComboBoxModel
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * tests DatabaseRepo.findNextNoticeToSend()
 */
internal class DatabaseRepoSendFailedTest {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var databaseRepo: DatabaseRepo? = null
    val filename = "dhl1.jpg"
    val dir = Paths.get("src/test/resources").toAbsolutePath().toString()

    /**
     *
     */
    @Test
    fun updateSendFailedTest() {

        // Insert a notice (containing a photo)
        val dateTime = ZonedDateTime.of(2021, 12, 31, 12, 58, 59, 0, ZoneId.of("Europe/Berlin"))
        val photo1 = PhotoEntity(
            Paths.get(dir, filename).toString(),
            null,
            50.1,
            8.1,
            dateTime
        )
        val finalTime = ZonedDateTime.of(2022, 1, 1, 12, 58, 0, 0, ZoneId.of("Europe/Berlin"))
        val notice1 = NoticeEntity()
        notice1.apply {
            observationTime = photo1.dateTime
            licensePlate = "SE NT 0001"
            vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1].toString()
            color = VehicleColor.COLORS[1].colorName
            latitude = 50.1
            longitude = 8.1
            photoEntities.add(photo1)
            deliveryType = 'E'
            finalizedTime = finalTime
        }
        databaseRepo?.insertNotice(notice1)

        // now there is a notice waiting to be sent
        val something = databaseRepo?.findNextNoticeToSend()
        assertEquals(1, something?.id)
        assertEquals(0, something?.sendFailures)
        something?.id?.let {
            databaseRepo?.updateNoticeSendFailed(it)
        }
        val updated = databaseRepo?.findNextNoticeToSend()
        assertEquals(1, something?.id)
        assertEquals(1, updated?.sendFailures)
    }

    @BeforeEach
    fun init_db() {
        databaseRepo = DatabaseRepo.fromMemory()
    }

    @AfterEach
    fun close_db() {
        databaseRepo?.close()
    }

}