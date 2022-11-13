package de.heikozelt.wegefrei.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

class PhotoFileWithFutureTest {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    @Test
    fun load_photo() {
        fun callBack(photoFileWithFuture: PhotoFileWithFuture) {
            log.info("called back")
            assertNotNull(photoFileWithFuture)
            val photoData = photoFileWithFuture.getPhotoDataIfDone()
            assertNotNull(photoData)
            assertEquals(2022, photoData?.date?.year)
        }

        val path = "src/test/resources/feuerwehrzufahrt.jpg"
        val file = File(path)
        val absoluteStr = file.absolutePath
        val absolutePath = Paths.get(absoluteStr)

        val photoFileWithFuture = PhotoFileWithFuture(absolutePath) { callBack(it) }
        val photoData = photoFileWithFuture.getPhotoDataBlocking()
        assertNotNull(photoData)
        assertEquals(2022, photoData.date?.year)
        assertEquals(7, photoData.date?.monthValue)
        assertEquals(1, photoData.date?.dayOfMonth)
        assertEquals(18, photoData.date?.hour)
        assertEquals(24, photoData.date?.minute)
        assertEquals(57, photoData.date?.second)
        assertEquals("50.07046", "%.5f".format(photoData.latitude))
        assertEquals("8.24489", "%.5f".format(photoData.longitude))
        // todo Hash-Wert prüfen
    }
}