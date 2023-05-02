package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.db.DatabaseRepo
import de.heikozelt.wegefrei.db.entities.PhotoEntity
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths
import java.time.ZoneId
import java.time.ZonedDateTime

class PhotoEntityTest {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    @Test
    fun photo_loading_entity() {
        val photo = Photo(absolutePath)
        val photoLoader = PhotoLoader(dbRepo)
        photoLoader.loadPhotoEntity(photo)
        Thread.sleep(2000)

        assertEquals(Photo.Companion.States.FOUND, photo.getEntityState())
        val photoEntity = photo.getPhotoEntity()
        assertNotNull(photoEntity)
        val dateTime = photoEntity?.dateTime
        assertNotNull(dateTime)
        assertEquals(2022, dateTime?.year)
        assertEquals(7, dateTime?.monthValue)
        assertEquals(1, dateTime?.dayOfMonth)
        assertEquals(18, dateTime?.hour)
        assertEquals(24, dateTime?.minute)
        assertEquals(57, dateTime?.second)
        assertEquals("50.07046° N 008.24489° E WGS 84", photoEntity?.getGeoPositionFormatted())
        // todo Hash-Wert prüfen
    }

    @Test
    fun photo_loading_entity_callback() {
        class Observer: PhotoLoaderObserver {
            private val log = LoggerFactory.getLogger("Observer")

            override fun doneLoadingFile(photo: Photo) {
                TODO("Not yet implemented")
            }

            override fun doneLoadingEntity(photo: Photo) {
                log.debug("doneLoadingEntity()")
                assertEquals(Photo.Companion.States.FOUND, photo.getEntityState())
                val photoEntity = photo.getPhotoEntity()
                assertNotNull(photoEntity)
                val dateTime = photoEntity?.dateTime
                assertNotNull(dateTime)
                assertEquals(2022, dateTime?.year)
                assertEquals(7, dateTime?.monthValue)
                assertEquals(1, dateTime?.dayOfMonth)
                assertEquals(18, dateTime?.hour)
                assertEquals(24, dateTime?.minute)
                assertEquals(57, dateTime?.second)
                assertEquals("50.07046", "%.5f".format(photoEntity?.latitude))
                assertEquals("8.24489", "%.5f".format(photoEntity?.longitude))
            }
        }

        val path = "src/test/resources/feuerwehrzufahrt1.jpg"
        val file = File(path)
        val absoluteStr = file.absolutePath
        val absolutePath = Paths.get(absoluteStr)

        val photo = Photo(absolutePath)
        val photoLoader = PhotoLoader(dbRepo)
        val observer = Observer()
        photoLoader.registerObserver(observer)
        photoLoader.loadPhotoEntity(photo)
        Thread.sleep(2000)
    }

    companion object {
        private val dbRepo = DatabaseRepo.fromMemory()
        private const val relativePath = "src/test/resources/feuerwehrzufahrt4.jpg"
        private val file = File(relativePath)
        private val absoluteStr: String = file.absolutePath
        private val absolutePath = Paths.get(absoluteStr)

        @BeforeAll
        @JvmStatic
        fun initialize_in_memory_database() {
            val dateTime = ZonedDateTime.of(2022,7,1,18,24,57,0, ZoneId.systemDefault())
            val photoEntity = PhotoEntity(
                absoluteStr,
                null,
                50.07046,
                8.24489,
                dateTime,
                hashSetOf()
            )
            dbRepo.insertPhoto(photoEntity)
        }

        @AfterAll @JvmStatic
        fun close_db() {
            dbRepo.close()
        }
    }
}