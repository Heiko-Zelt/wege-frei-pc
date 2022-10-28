package de.heikozelt.wegefrei

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class DatabaseRepoTest {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val databaseRepo = DatabaseRepo("~")

    // todo Prio 1: use in memory test database
    @Test
    fun getPhotoByFilename() {
        val photo1 = databaseRepo.getPhotoByFilename("20220301_184943.jpg")
        val photo2 = databaseRepo.getPhotoByFilename("20220301_184943.jpg")
        log.debug("photo1: $photo1")
        log.debug("photo2: $photo2")
        assertNotNull(photo1)
        assertNotNull(photo2)
        assertTrue(photo1 == photo2)
    }
}