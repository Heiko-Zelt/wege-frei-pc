package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class SelectedPhotosTest {

    @Test
    fun calculateMarkerIndex_3_photos_3_markers() {
       val photo1 = Photo("1.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
       val photo2 = Photo("2.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
       val photo3 = Photo("3.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
       val selectedPhotos = SelectedPhotos()
       selectedPhotos.add(photo1)
       selectedPhotos.add(photo2)
       selectedPhotos.add(photo3)
       assertEquals(2, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
       assertEquals(3, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }

    @Test
    fun calculateMarkerIndex_3_photos_2_markers_a() {
        val photo1 = Photo("1.jpeg", null, null, ZonedDateTime.now())
        val photo2 = Photo("2.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
        val photo3 = Photo("3.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
        val selectedPhotos = SelectedPhotos()
        selectedPhotos.add(photo1)
        selectedPhotos.add(photo2)
        selectedPhotos.add(photo3)
        assertEquals(1, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
        assertEquals(2, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }

    @Test
    fun calculateMarkerIndex_3_photos_2_markers_b() {
        val photo1 = Photo("1.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
        val photo2 = Photo("2.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
        val photo3 = Photo("3.jpeg", null, null, ZonedDateTime.now())
        val selectedPhotos = SelectedPhotos()
        selectedPhotos.add(photo1)
        selectedPhotos.add(photo2)
        selectedPhotos.add(photo3)
        assertEquals(2, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
        assertEquals(2, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }

    /*
    @Test
    @Disabled("fails and method is not needed")
    fun getPhotosWithGeoPosition() {
        val photo0 = Photo("0.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
        val photo1 = Photo("1.jpeg", 50.08f, 8.24f, ZonedDateTime.now())
        val photo2 = Photo("2.jpeg", null, null, ZonedDateTime.now())
        val selectedPhotos = SelectedPhotos()
        //val withPosition = selectedPhotos.getPhotosWithGeoPosition()
        //assertEquals(2, withPosition.size)
        //assertEquals(photo0, withPosition[0])
        //assertEquals(photo1, withPosition[1])
    }
    */
}