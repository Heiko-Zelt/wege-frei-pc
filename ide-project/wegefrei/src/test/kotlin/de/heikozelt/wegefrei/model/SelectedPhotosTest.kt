package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class SelectedPhotosTest {

    // 20 Bytes = 160 bit
    private val hash1 = "0123456789abcdefghij".toByteArray()
    private val hash2 = "abcdefghij0123456789".toByteArray()
    private val hash3 = "ABCDEFGHIJ0123456789".toByteArray()

    @Test
    fun calculateMarkerIndex_3_photos_3_markers() {
       val photo1 = Photo("1.jpeg", hash1,50.08f, 8.24f, ZonedDateTime.now())
       val photo2 = Photo("2.jpeg", hash2,50.08f, 8.24f, ZonedDateTime.now())
       val photo3 = Photo("3.jpeg", hash3,50.08f, 8.24f, ZonedDateTime.now())
       val selectedPhotos = SelectedPhotos()
       selectedPhotos.add(photo1)
       selectedPhotos.add(photo2)
       selectedPhotos.add(photo3)
       assertEquals(2, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
       assertEquals(3, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }

    @Test
    fun calculateMarkerIndex_3_photos_2_markers_a() {
        val photo1 = Photo("1.jpeg", hash1,null, null, ZonedDateTime.now())
        val photo2 = Photo("2.jpeg", hash2,50.08f, 8.24f, ZonedDateTime.now())
        val photo3 = Photo("3.jpeg", hash3,50.08f, 8.24f, ZonedDateTime.now())
        val selectedPhotos = SelectedPhotos()
        selectedPhotos.add(photo1)
        selectedPhotos.add(photo2)
        selectedPhotos.add(photo3)
        assertEquals(1, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
        assertEquals(2, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }

    @Test
    fun calculateMarkerIndex_3_photos_2_markers_b() {
        val photo1 = Photo("1.jpeg", hash1,50.08f, 8.24f, ZonedDateTime.now())
        val photo2 = Photo("2.jpeg", hash2,50.08f, 8.24f, ZonedDateTime.now())
        val photo3 = Photo("3.jpeg", hash3,null, null, ZonedDateTime.now())
        val selectedPhotos = SelectedPhotos()
        selectedPhotos.add(photo1)
        selectedPhotos.add(photo2)
        selectedPhotos.add(photo3)
        assertEquals(2, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
        assertEquals(2, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }

}