package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.Photo
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.ZonedDateTime

internal class DatabaseRepoTest {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val databaseRepo = DatabaseRepo.fromMemory()

    @Test
    fun getPhotoByFilename() {
        val zdt1 = ZonedDateTime.of(2021, 12, 30, 19, 49, 59, 0, ZoneId.of("CET"))
        val givenPhoto1 = Photo(
            "20220301_184943.jpg",
            "0123456789ABCDEFGHIJ".toByteArray(),
            50.1f,
            8.1f,
            zdt1
        )

        databaseRepo.insertPhoto(givenPhoto1)

        val photo1 = databaseRepo.getPhotoByFilename("20220301_184943.jpg")
        val photo2 = databaseRepo.getPhotoByFilename("20220301_184943.jpg")
        log.debug("photo1: $photo1")
        log.debug("photo2: $photo2")
        assertNotNull(photo1)
        assertNotNull(photo2)
        assertTrue(photo1 == photo2)
    }
}