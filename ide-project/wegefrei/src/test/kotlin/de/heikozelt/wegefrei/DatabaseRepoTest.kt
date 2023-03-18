package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.entities.PhotoEntity
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

internal class DatabaseRepoTest {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * gleicher Datensatz, aber 2 unterschiedliche Transaktionen liefern 2 unterschiedliche Entities
     */
    @Test
    fun getPhotoByFilename() {
        val photo1 = databaseRepo.findPhotoByPath(Paths.get(dir, filenames[0]))
        val photo2 = databaseRepo.findPhotoByPath(Paths.get(dir, filenames[0]))
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
        assertNotNull(noticeIds) // kann gar nicht null sein
        assertTrue(2 in noticeIds)
        assertTrue(3 in noticeIds)
        assertTrue(13 in noticeIds)
        assertTrue(14 in noticeIds)
        assertFalse(15 in noticeIds)
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
        notice2?.let { n ->
            n.photoEntities.forEach {
                log.debug("path=${it.path}")
            }
            assertEquals(2, n.photoEntities.size)
            val paths = n.photoEntities.map { it.path }
            assertTrue(Paths.get(dir, filenames[3]).toString() in paths)
            assertTrue(Paths.get(dir, filenames[4]).toString() in paths)
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
        notice2?.let { n ->
            n.photoEntities.forEach {
                log.debug("path=${it.path}")
            }
            assertEquals(0, n.photoEntities.size)
        }
        val orphan = databaseRepo.findPhotoByPath(Paths.get(dir, filenames[5]).toString())
        assertNull(orphan)
    }

    @Test
    fun deleteNotice() {
        databaseRepo.deleteNotice(1)

        val notice = databaseRepo.findNoticeById(1)
        assertNull(notice)
        val photo0 = databaseRepo.findPhotoByPath(Paths.get(dir, filenames[0]).toString())
        assertNotNull(photo0)
        val orphan = databaseRepo.findPhotoByPath(Paths.get(dir, filenames[1]).toString())
        assertNull(orphan)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        private val databaseRepo = DatabaseRepo.fromMemory()

        val photos = mutableListOf<PhotoEntity>()
        val filenames = arrayOf(
            "dhl1.jpg", "dhl2.jpg", "dhl3.jpg", "dhl4.jpg",
            "feuerwehrzufahrt1.jpg", "feuerwehrzufahrt2.jpg", "feuerwehrzufahrt3.jpg"
        )

        val dir = Paths.get("src/test/resources").toAbsolutePath().toString()

        @BeforeAll
        @JvmStatic
        fun inserts() {
            LOG.debug("BeforeAll()")

            for (i in filenames.indices) {
                val dateTime = ZonedDateTime.of(2021, 12, 31, 12, i, 0, 0, ZoneId.of("CET"))
                val photo = PhotoEntity(
                    Paths.get(dir, filenames[i]).toString(),
                    null,
                    50.0 + i.toDouble() / 10,
                    8.0 + i.toDouble() / 10,
                    dateTime
                )
                photos.add(photo)
            }

            val notice1 = NoticeEntity()
            notice1.apply {
                observationTime = photos[0].dateTime
                licensePlate = "DEL ET 0000"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1].toString()
                color = VehicleColor.COLORS[1].colorName
                latitude = 50.1
                longitude = 8.1
                photoEntities.add(photos[0])
                photoEntities.add(photos[1])
            }
            databaseRepo.insertNotice(notice1)

            val notice2 = NoticeEntity()
            notice2.apply {
                observationTime = photos[0].dateTime
                licensePlate = "KE EP 0001"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[2].toString()
                color = VehicleColor.COLORS[2].colorName
                latitude = 50.2
                longitude = 8.2
                photoEntities.add(photos[0])
                photoEntities.add(photos[2])
            }
            databaseRepo.insertNotice(notice2)

            val notice3 = NoticeEntity()
            notice3.apply {
                observationTime = photos[0].dateTime
                licensePlate = "UPD AT 0002"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[3].toString()
                color = VehicleColor.COLORS[3].colorName
                latitude = 50.3
                longitude = 8.3
                photoEntities.add(photos[3])
            }
            databaseRepo.insertNotice(notice3)

            val notice4 = NoticeEntity()
            notice4.apply {
                observationTime = photos[0].dateTime
                licensePlate = "UPD AT 0002"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[3].toString()
                color = VehicleColor.COLORS[3].colorName
                latitude = 50.4
                longitude = 8.4
                photoEntities.add(photos[5])
            }
            databaseRepo.insertNotice(notice4)

            for (i in 1..10) {
                val notice = NoticeEntity()
                notice.apply {
                    observationTime = ZonedDateTime.now()
                    licensePlate = "AA XX 00$i"
                    vehicleMake =
                        VehicleMakesComboBoxModel.VEHICLE_MAKES[i % VehicleMakesComboBoxModel.VEHICLE_MAKES.size].toString()
                    color = VehicleColor.COLORS[i % VehicleColor.COLORS.size].colorName
                    latitude = 49 + i.toDouble() / 11
                    longitude = 8 + i.toDouble() / 13
                }
                databaseRepo.insertNotice(notice)
            }
            databaseRepo.logStatistics()

        }

        @AfterAll
        @JvmStatic
        fun close_db() {
            databaseRepo.close()
        }
    }
}