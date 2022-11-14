package de.heikozelt.wegefrei.model

class SelectedPhotosTest {

    // 20 Bytes = 160 bit
    private val hash1 = "0123456789abcdefghij".toByteArray()
    private val hash2 = "abcdefghij0123456789".toByteArray()
    private val hash3 = "ABCDEFGHIJ0123456789".toByteArray()

    /*
    @Test
    fun calculateMarkerIndex_3_photos_3_markers() {
       val photoEntity1 = PhotoEntity("1.jpeg", hash1,50.08f, 8.24f, ZonedDateTime.now())
       val photoEntity2 = PhotoEntity("2.jpeg", hash2,50.08f, 8.24f, ZonedDateTime.now())
       val photoEntity3 = PhotoEntity("3.jpeg", hash3,50.08f, 8.24f, ZonedDateTime.now())
       val selectedPhotos = SelectedPhotos()
       selectedPhotos.add(photoEntity1)
       selectedPhotos.add(photoEntity2)
       selectedPhotos.add(photoEntity3)
       assertEquals(2, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
       assertEquals(3, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }

     */

    /*
    @Test
    fun calculateMarkerIndex_3_photos_2_markers_a() {
        val photoEntity1 = PhotoEntity("1.jpeg", hash1,null, null, ZonedDateTime.now())
        val photoEntity2 = PhotoEntity("2.jpeg", hash2,50.08f, 8.24f, ZonedDateTime.now())
        val photoEntity3 = PhotoEntity("3.jpeg", hash3,50.08f, 8.24f, ZonedDateTime.now())
        val selectedPhotos = SelectedPhotos()
        selectedPhotos.add(photoEntity1)
        selectedPhotos.add(photoEntity2)
        selectedPhotos.add(photoEntity3)
        assertEquals(1, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
        assertEquals(2, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }

     */

    /*
    @Test
    fun calculateMarkerIndex_3_photos_2_markers_b() {
        val photoEntity1 = PhotoEntity("1.jpeg", hash1,50.08f, 8.24f, ZonedDateTime.now())
        val photoEntity2 = PhotoEntity("2.jpeg", hash2,50.08f, 8.24f, ZonedDateTime.now())
        val photoEntity3 = PhotoEntity("3.jpeg", hash3,null, null, ZonedDateTime.now())
        val selectedPhotos = SelectedPhotos()
        selectedPhotos.add(photoEntity1)
        selectedPhotos.add(photoEntity2)
        selectedPhotos.add(photoEntity3)
        assertEquals(2, selectedPhotos.calculateMarkerIndex(2)) // letztes Foto
        assertEquals(2, selectedPhotos.calculateMarkerIndex(3)) // letztes Foto wurde entfernt
    }
     */

}