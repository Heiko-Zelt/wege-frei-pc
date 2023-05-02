package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.db.DatabaseRepo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

class PhotoFileTest {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    @Test
    fun photo_loading_file() {
        val path = "src/test/resources/feuerwehrzufahrt1.jpg"
        val file = File(path)
        val absoluteStr = file.absolutePath
        val absolutePath = Paths.get(absoluteStr)
        val databaseRepo = DatabaseRepo.fromMemory()

        val photo = Photo(absolutePath)
        val photoLoader = PhotoLoader(databaseRepo)
        photoLoader.loadPhotoFile(photo)
        Thread.sleep(3000)

        assertEquals(Photo.Companion.States.FOUND, photo.getFileState())
        val photoFile = photo.getPhotoFile()
        assertNotNull(photoFile)
        val dateTime = photoFile?.dateTime
        assertNotNull(dateTime)
        assertEquals(2022, dateTime?.year)
        assertEquals(7, dateTime?.monthValue)
        assertEquals(1, dateTime?.dayOfMonth)
        assertEquals(18, dateTime?.hour)
        assertEquals(24, dateTime?.minute)
        assertEquals(57, dateTime?.second)
        assertEquals("50.07046° N 008.24489° E WGS 84", photoFile?.getGeoPositionFormatted())
        val img = photoFile?.image
        assertNotNull(img)
        // todo Bug: 90 Grad gedreht
        assertEquals(1280, img?.width)
        assertEquals(720, img?.height)
        // todo Hash-Wert prüfen
    }

    @Test
    fun photo_loading_file_callback() {
        class Observer: PhotoLoaderObserver {
            private val log = LoggerFactory.getLogger("Observer")

            override fun doneLoadingFile(photo: Photo) {
                log.debug("doneLoadingFile()")
                assertEquals(Photo.Companion.States.FOUND, photo.getFileState())
                val photoFile = photo.getPhotoFile()
                assertNotNull(photoFile)
                val dateTime = photoFile?.dateTime
                assertNotNull(dateTime)
                log.debug("dateTime?.year: ${dateTime?.year}")
                assertEquals(2022, dateTime?.year)
                assertEquals(7, dateTime?.monthValue)
                assertEquals(1, dateTime?.dayOfMonth)
                assertEquals(18, dateTime?.hour)
                assertEquals(24, dateTime?.minute)
                assertEquals(57, dateTime?.second)
                assertEquals("50.07046", "%.5f".format(photoFile?.latitude))

                assertEquals("8.24489", "%.5f".format(photoFile?.longitude))
                val img = photoFile?.image
                assertNotNull(img)
                // todo Bug: 90 Grad gedreht
                assertEquals(1280, img?.width)
                assertEquals(720, img?.height)
                // todo Hash-Wert prüfen
            }

            override fun doneLoadingEntity(photo: Photo) {
                TODO("Not yet implemented")
            }
        }

        val path = "src/test/resources/feuerwehrzufahrt1.jpg"
        val file = File(path)
        val absoluteStr = file.absolutePath
        val absolutePath = Paths.get(absoluteStr)
        val databaseRepo = DatabaseRepo.fromMemory()

        val photo = Photo(absolutePath)
        val photoLoader = PhotoLoader(databaseRepo)
        val observer = Observer()
        photoLoader.registerObserver(observer)
        photoLoader.loadPhotoFile(photo)
        Thread.sleep(3000)
    }
}