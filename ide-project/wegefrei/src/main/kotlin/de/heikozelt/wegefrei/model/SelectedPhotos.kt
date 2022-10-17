package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo
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
        for(observer in observers) {
            observer.selectedPhoto(index, photo)
        }
    }

    fun remove(photo: Photo) {
        val index = photos.indexOf(photo)
        photos.remove(photo)
        for(observer in observers) {
            observer.unselectedPhoto(index, photo)
        }
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
        for(observer in observers) {
            observer.replacedPhotoSelection(photos)
        }
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

    fun getStartTime(): ZonedDateTime? {
        val firstPhotoWithDate = photos.find { it.date != null }
        return firstPhotoWithDate?.date
    }

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
}