package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.entities.PhotoEntity
import de.heikozelt.wegefrei.model.VehicleColor
import de.heikozelt.wegefrei.model.VehicleMakesComboBoxModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.ZonedDateTime

internal class DatabaseRepoTest {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * gleicher Datensatz, aber 2 unterschiedliche Transaktionen liefern 2 unterschiedliche Entities
     */
    @Test
    fun getPhotoByFilename() {
        val photo1 = databaseRepo.findPhotoByPath("/tmp/2021-12-31_12-00-00.jpg")
        val photo2 = databaseRepo.findPhotoByPath("/tmp/2021-12-31_12-00-00.jpg")
        log.debug("photo1: $photo1")
        log.debug("photo2: $photo2")
        assertNotNull(photo1)
        assertNotNull(photo2)
        assertNotEquals(photo1, photo2)
        assertEquals(photo1?.path, photo2?.path)
    }

    /**
     * The notice with id 1 may be deleted by deleteNotice() test.
     */
    @Test
    fun findAllNoticesIds() {
        val noticeIds = databaseRepo.findAllNoticesIdsDesc()
        assertNotNull(noticeIds)
        noticeIds?.let {
            assertTrue(2 in it)
            assertTrue(3 in it)
            assertTrue(13 in it)
            assertTrue(14 in it)
            assertFalse(15 in it)
        }
    }

    @Test
    fun updateNotice_after_one_photo_was_added() {
        val notice = databaseRepo.findNoticeById(3)
        assertNotNull(notice)
        notice?.let { n ->
            n.photoEntities.add(photos[4])
            databaseRepo.updateNotice(n)
        }

        val notice2 = databaseRepo.findNoticeById(3)
        assertNotNull(notice2)
        notice2?.let{ n ->
            n.photoEntities.forEach {
                log.debug("path=${it.path}")
            }
            assertEquals(2, n.photoEntities.size)
            val paths = n.photoEntities.map { it.path }
            assertTrue("/tmp/2021-12-31_12-03-00.jpg" in paths)
            assertTrue("/tmp/2021-12-31_12-04-00.jpg" in paths)
        }
    }

    @Test
    fun updateNotice_after_one_photo_was_removed() {
        val notice = databaseRepo.findNoticeById(4)
        assertNotNull(notice)
        notice?.let { n ->
            n.photoEntities.clear()
            databaseRepo.updateNotice(n)
        }

        val notice2 = databaseRepo.findNoticeById(4)
        assertNotNull(notice2)
        notice2?.let{ n ->
            n.photoEntities.forEach {
                log.debug("path=${it.path}")
            }
            assertEquals(0, n.photoEntities.size)
        }
        val orphan = databaseRepo.findPhotoByPath("/tmp/2021-12-31_12-05-00.jpg")
        assertNull(orphan)
    }

    @Test
    fun deleteNotice() {
        databaseRepo.deleteNotice(1)

        val notice = databaseRepo.findNoticeById(1)
        assertNull(notice)
        val photo0 = databaseRepo.findPhotoByPath("/tmp/2021-12-31_12-00-00.jpg")
        assertNotNull(photo0)
        val orphan = databaseRepo.findPhotoByPath("/tmp/2021-12-31_12-01-00.jpg")
        assertNull(orphan)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        private val databaseRepo = DatabaseRepo.fromMemory()

        val photos = mutableListOf<PhotoEntity>()

        @BeforeAll
        @JvmStatic
        fun inserts() {
            LOG.debug("BeforeAll()")

            for (i in 0 ..5) {
                val dateTime = ZonedDateTime.of(2021, 12, 31, 12, i, 0, 0, ZoneId.of("CET"))
                val photo = PhotoEntity(
                    "/tmp/2021-12-31_12-0$i-00.jpg",
                    "0123456789ABCDEFGHIJ".toByteArray(),
                    50.0f + i.toFloat() / 10,
                    8.0f + i.toFloat() / 10,
                    dateTime
                )
                photos.add(photo)
            }

            val notice1 = NoticeEntity()
            notice1.apply {
                observationTime = photos[0].dateTime
                licensePlate = "DEL ET 0000"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1]
                color = VehicleColor.COLORS[1].colorName
                latitude = 50.1f
                longitude = 8.1f
                photoEntities.add(photos[0])
                photoEntities.add(photos[1])
            }
            databaseRepo.insertNotice(notice1)

            val notice2 = NoticeEntity()
            notice2.apply {
                observationTime = photos[0].dateTime
                licensePlate = "KE EP 0001"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[2]
                color = VehicleColor.COLORS[2].colorName
                latitude = 50.2f
                longitude = 8.2f
                photoEntities.add(photos[0])
                photoEntities.add(photos[2])
            }
            databaseRepo.insertNotice(notice2)

            val notice3 = NoticeEntity()
            notice3.apply {
                observationTime = photos[0].dateTime
                licensePlate = "UPD AT 0002"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[3]
                color = VehicleColor.COLORS[3].colorName
                latitude = 50.3f
                longitude = 8.3f
                photoEntities.add(photos[3])
            }
            databaseRepo.insertNotice(notice3)

            val notice4 = NoticeEntity()
            notice4.apply {
                observationTime = photos[0].dateTime
                licensePlate = "UPD AT 0002"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[3]
                color = VehicleColor.COLORS[3].colorName
                latitude = 50.4f
                longitude = 8.4f
                photoEntities.add(photos[5])
            }
            databaseRepo.insertNotice(notice4)

            for (i in 1..10) {
                val notice = NoticeEntity()
                notice.apply {
                    observationTime = ZonedDateTime.now()
                    licensePlate = "AA XX 00$i"
                    vehicleMake =
                        VehicleMakesComboBoxModel.VEHICLE_MAKES[i % VehicleMakesComboBoxModel.VEHICLE_MAKES.size]
                    color = VehicleColor.COLORS[i % VehicleColor.COLORS.size].colorName
                    latitude = 49 + i.toFloat() / 11
                    longitude = 8 + i.toFloat() / 13
                }
                databaseRepo.insertNotice(notice)
            }
            databaseRepo.logStatistics()

        }
    }
}