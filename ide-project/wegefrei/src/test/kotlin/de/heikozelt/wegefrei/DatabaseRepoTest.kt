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

    @Test
    fun getPhotoByFilename() {
        // 2 unterschiedliche Transaktionen liefern 2 unterschiedliche Entities
        val photo1 = databaseRepo.findPhotoByPath("/tmp/20220301_184943.jpg")
        val photo2 = databaseRepo.findPhotoByPath("/tmp/20220301_184943.jpg")
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
        assertEquals(10, noticeIds?.size)
        assertEquals(10, noticeIds?.get(0))
        assertEquals(1, noticeIds?.get(9))
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        private val databaseRepo = DatabaseRepo.fromMemory()

        @BeforeAll @JvmStatic
        fun inserts() {
            LOG.debug("BeforeAll()")
            val zdt1 = ZonedDateTime.of(2021, 12, 30, 19, 49, 59, 0, ZoneId.of("CET"))
            val givenPhoto1Entity = PhotoEntity(
                "/tmp/20220301_184943.jpg",
                "0123456789ABCDEFGHIJ".toByteArray(),
                50.1f,
                8.1f,
                zdt1
            )
            databaseRepo.insertPhoto(givenPhoto1Entity)

            for (i in 1..10) {
                val noticeEntity = NoticeEntity()
                noticeEntity.apply {
                    observationTime = ZonedDateTime.now()
                    licensePlate = "AA XX 00$i"
                    vehicleMake = VehicleMakesComboBoxModel.VEHICLE_MAKES[i % VehicleMakesComboBoxModel.VEHICLE_MAKES.size]
                    color = VehicleColor.COLORS[i % VehicleColor.COLORS.size].colorName
                    latitude = 49 + i.toFloat() / 11
                    longitude = 8 + i.toFloat() / 13
                }
                databaseRepo.insertNotice(noticeEntity)
            }
        }
    }
}