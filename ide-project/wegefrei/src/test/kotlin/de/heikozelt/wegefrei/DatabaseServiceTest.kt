package de.heikozelt.wegefrei

import mu.KotlinLogging
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DatabaseServiceTest {

    private val log = KotlinLogging.logger {}

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