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

    @Test
    fun findAllNoticesIds() {
        val noticeIds = databaseRepo.findAllNoticesIdsDesc()
        assertEquals(12, noticeIds?.size)
        assertEquals(12, noticeIds?.get(0))
        assertEquals(1, noticeIds?.get(11))
    }

    @Test
    fun deleteNotice() {
        val notice = NoticeEntity(0)
        databaseRepo.deleteNotice(notice)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        private val databaseRepo = DatabaseRepo.fromMemory()

        @BeforeAll @JvmStatic
        fun inserts() {
            LOG.debug("BeforeAll()")
            val zdt0 = ZonedDateTime.of(2021, 12, 31, 12, 0, 0, 0, ZoneId.of("CET"))
            val zdt1 = ZonedDateTime.of(2021, 12, 31, 12, 1, 0, 0, ZoneId.of("CET"))
            val zdt2 = ZonedDateTime.of(2021, 12, 31, 12, 2, 0, 0, ZoneId.of("CET"))
            val photo0a = PhotoEntity(
                "/tmp/2021-12-31_12-00-00.jpg",
                "0123456789ABCDEFGHIJ".toByteArray(),
                50.0f,
                8.0f,
                zdt0
            )
            val photo1 = PhotoEntity(
                "/tmp/2021-12-31_12-01-00.jpg",
                "0123456789ABCDEFGHIJ".toByteArray(),
                50.1f,
                8.1f,
                zdt1
            )
            val notice0 = NoticeEntity()
            notice0.apply {
                observationTime = zdt0
                licensePlate = "DEL ET 0000"
                vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[1]
                color = VehicleColor.COLORS[1].colorName
                latitude = 50.0f
                longitude = 8.0f
                photoEntities.add(photo0a)
                photoEntities.add(photo1)
            }
            databaseRepo.insertPhoto(photo0a)
            databaseRepo.insertPhoto(photo1)
            databaseRepo.insertNotice(notice0)

            val photo0b = databaseRepo.findPhotoByPath("/tmp/2021-12-31_12-00-00.jpg")
            photo0b?.let {
                val photo2 = PhotoEntity(
                    "/tmp/2021-12-31_12-02-00.jpg",
                    "0123456789ABCDEFGHIJ".toByteArray(),
                    50.2f,
                    8.2f,
                    zdt2
                )
                val notice1 = NoticeEntity()
                notice1.apply {
                    observationTime = zdt1
                    licensePlate = "KE EP 0001"
                    vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[2]
                    color = VehicleColor.COLORS[2].colorName
                    latitude = 50.1f
                    longitude = 8.1f
                    photoEntities.add(it)
                    photoEntities.add(photo2)
                }
                databaseRepo.insertPhoto(photo2)
                databaseRepo.insertNotice(notice1)
            }

            for (i in 1..10) {
                val notice = NoticeEntity()
                notice.apply {
                    observationTime = ZonedDateTime.now()
                    licensePlate = "AA XX 00$i"
                    vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[i % VehicleMakesComboBoxModel.VEHICLE_MAKES.size]
                    color = VehicleColor.COLORS[i % VehicleColor.COLORS.size].colorName
                    latitude = 49 + i.toFloat() / 11
                    longitude = 8 + i.toFloat() / 13
                }
                databaseRepo.insertNotice(notice)
            }
        }
    }
}