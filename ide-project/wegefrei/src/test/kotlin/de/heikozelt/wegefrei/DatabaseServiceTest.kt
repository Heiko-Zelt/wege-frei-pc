package de.heikozelt.wegefrei

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class DatabaseServiceTest {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val databaseService = DatabaseService()

    @Test
    fun getPhotoByFilename() {
        val photo1 = databaseService.getPhotoByFilename("20220301_184943.jpg")
        val photo2 = databaseService.getPhotoByFilename("20220301_184943.jpg")
        log.debug("photo1: $photo1")
        log.debug("photo2: $photo2")
        assertNotNull(photo1)
        assertNotNull(photo2)
        assertTrue(photo1 == photo2)
    }
}