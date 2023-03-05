package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.entities.PhotoEntity
import de.heikozelt.wegefrei.model.VehicleColor
import de.heikozelt.wegefrei.model.VehicleMakesComboBoxModel
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
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

    /**
     *
     */
    @Test
    fun updateSendFailedTest() {

        // Insert a notice (containing a photo)
        val dateTime = ZonedDateTime.of(2021, 12, 31, 12, 58, 59, 0, ZoneId.of("Europe/Berlin"))
        val photo1 = PhotoEntity(
            Paths.get(DatabaseRepoUpdateNoticeSentTest.dir, DatabaseRepoUpdateNoticeSentTest.filename).toString(),
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
            vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1]
            color = VehicleColor.COLORS[1].colorName
            latitude = 50.1
            longitude = 8.1
            photoEntities.add(photo1)
            finalizedTime = finalTime
        }
        databaseRepo.insertNotice(notice1)

        // now there is a notice waiting to be sent
        val something = databaseRepo.findNextNoticeToSend()
        assertEquals(1, something?.id)
        assertEquals(0, something?.sendFailures)
        something?.id?.let {
            databaseRepo.updateNoticeSendFailed(it)
        }
        val updated = databaseRepo.findNextNoticeToSend()
        assertEquals(1, something?.id)
        assertEquals(1, updated?.sendFailures)
    }

    companion object {
        private val databaseRepo = DatabaseRepo.fromMemory()

        @AfterAll @JvmStatic
        fun close_db() {
            databaseRepo.close()
        }
    }
}