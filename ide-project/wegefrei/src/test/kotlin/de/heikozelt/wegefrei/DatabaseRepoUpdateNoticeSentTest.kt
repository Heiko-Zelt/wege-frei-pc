package de.heikozelt.wegefrei

import com.beust.klaxon.Klaxon
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.entities.PhotoEntity
import de.heikozelt.wegefrei.model.NoticeState
import de.heikozelt.wegefrei.model.VehicleColor
import de.heikozelt.wegefrei.model.VehicleMakesComboBoxModel
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.time.ZoneId
import java.time.ZonedDateTime

internal class DatabaseRepoUpdateNoticeSentTest {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    @Test
    fun updateNotice_after_it_was_sent() {
        val noticeID = 1
        val sentTime = ZonedDateTime.of(2022, 1, 1, 12, 58, 59, 0, ZoneId.of("Europe/Berlin"))
        val messageID = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
        val notice = databaseRepo.findNoticeById(noticeID)
        assertNotNull(notice)
        databaseRepo.updateNoticeSent(noticeID, sentTime, messageID)

        val sentNotice = databaseRepo.findNoticeById(noticeID)
        assertNotNull(sentNotice)
        sentNotice?.let {
            it.dump(log)
            assertEquals(sentTime, it.sentTime);
            assertTrue(it.isSent());
            assertEquals(NoticeState.SENT, it.getState())
            assertArrayEquals(messageID, it.messageId)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        private val databaseRepo = DatabaseRepo.fromMemory()

        val filename = "dhl1.jpg"
        val dir = Paths.get("src/test/resources").toAbsolutePath().toString()

        @BeforeAll
        @JvmStatic
        fun inserts() {
            LOG.debug("BeforeAll()")

            val dateTime = ZonedDateTime.of(2021, 12, 31, 12, 58, 59, 0, ZoneId.of("Europe/Berlin"))
            val finalTime = ZonedDateTime.of(2022, 1, 1, 12, 58, 0, 0, ZoneId.of("Europe/Berlin"))

            val photo1 = PhotoEntity(
                Paths.get(dir, filename).toString(),
                null,
                50.1,
                8.1,
                dateTime
            )

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
        }

        @AfterAll
        @JvmStatic
        fun close_db() {
            databaseRepo.close()
        }
    }
}