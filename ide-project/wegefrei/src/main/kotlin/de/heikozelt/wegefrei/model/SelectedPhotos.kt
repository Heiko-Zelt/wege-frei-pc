package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo
import org.jxmapviewer.viewer.GeoPosition
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Fotos sind immer nach Dateiname (also in der Regel auch chronologisch) sortiert
 */
class SelectedPhotos(private var photos: TreeSet<Photo> = TreeSet<Photo>()) {

    /**
     * Jeder Observer kann nur einmal registriert sein.
     * Die Reihenfolge sollte egal sein.
     * Deswegen ein HashSet.
     */
    private val observers = HashSet<SelectedPhotosObserver>()

    fun add(photo: Photo) {
        photos.add(photo)
        val index = photos.indexOf(photo)
        observers.forEach { it.selectedPhoto(index, photo) }
    }

    fun remove(photo: Photo) {
        val index = photos.indexOf(photo)
        photos.remove(photo)
        observers.forEach { it.unselectedPhoto(index, photo) }
    }

    /**
     * Gefahr: Nicht verwenden, um Änderungen an den Fotos durchzuführen,
     * von denen die Observers nichts mitkriegen!
     */
    fun getPhotos(): TreeSet<Photo> {
        return photos
    }

    /**
     * Observers benachrichtigen?
     */
    fun setPhotos(photos: TreeSet<Photo>) {
        this.photos = photos
        observers.forEach { it.replacedPhotoSelection(photos) }
    }

    fun registerObserver(observer: SelectedPhotosObserver) {
        observers.add(observer)
    }

    /**
     * Wichtig, um Memory-Leaks zu vermeiden
     * todo: wenn MaxiMap nicht mehr im Zoom-Bereich angezeigt wird
     */
    fun unregisterObserver(observer: SelectedPhotosObserver) {
        observers.remove(observer)
    }

    /**
     * from all photos with a date, get the earliest date
     */
    fun getStartTime(): ZonedDateTime? {
        val firstPhotoWithDate = photos.find { it.date != null }
        return firstPhotoWithDate?.date
    }

    /**
     * from all photos with a date, get the latest date
     */
    fun getEndTime(): ZonedDateTime? {
        val reversedList = photos.reversed()
        val lastPhotoWithDate = reversedList.find { it.date != null }
        return lastPhotoWithDate?.date
    }

    /**
     * in Minutes
     */
    fun getDuartion(): Int? {
        val start = getStartTime()
        val end = getEndTime()
        return if(start == null || end == null) {
            null
        } else {
            val unit = ChronoUnit.MINUTES
            unit.between(start, end).toInt()
        }
    }

    /**
     * Die Methode liefert zu einem Foto mit dem angegebenen Index den entsprechenden Index des Markers.
     * Wenn alle Fotos eine Geo-Position haben, dann ist der Marker-Index gleich dem Foto-Index.
     * Ansonsten kann der Marker-Index kleiner sein als der Foto-Index.
     * Ist der Foto-Index größer als der Index des letzten Fotos, dann wird ein Marker-Index zurückgeliefert,
     * der um eins größer ist als der letzte Marker-Index.
     */
    fun calculateMarkerIndex(photoIndex: Int): Int {
        var markerIndex = 0
        val photoIter = photos.iterator()
        var i = 0
        while(photoIter.hasNext() && i < photoIndex) {
            i++
            if (photoIter.next().getGeoPosition() != null) {
                markerIndex++
            }
        }
        return markerIndex
    }

    fun calculateMarkerIndex_alternativ(photoIndex: Int): Int {
        var markerIndex = 0
        val photosArray: Array<Photo> = photos.toTypedArray()
        for (i in 0 until photoIndex) {
            if (photosArray[i].getGeoPosition() != null) {
                markerIndex++
            }
        }
        return markerIndex
    }

    /**
     * Die Methode ist ähnlich GeoBounds(positions).getCenter().
     * Es wird allerdings nicht die Mitte der Bounding-Box zurückgeliefert,
     * sondern jeweils der Durchschnittswert der Breiten- und Längengrade.
     * Ein einzelner Ausreißer hat also weniger Gewicht.
     */
    fun getAveragePosition(): GeoPosition? {
        val latitudes = mutableListOf<Float>()
        val longitudes = mutableListOf<Float>()
        photos.forEach { photo ->
            photo.latitude?.let { lat ->
                photo.longitude?.let { lon ->
                    latitudes.add(lat)
                    longitudes.add(lon)
                }
            }
        }
        return if(latitudes.size == 0) {
            null
        } else {
            val newLatitude = latitudes.average()
            val newLongitude = longitudes.average()
            GeoPosition(newLatitude, newLongitude)
        }
    }
}