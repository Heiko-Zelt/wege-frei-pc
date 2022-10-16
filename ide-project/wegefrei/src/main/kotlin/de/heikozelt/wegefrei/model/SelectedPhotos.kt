package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo
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
            observer.addedPhoto(index, photo)
        }
    }

    fun remove(photo: Photo) {
        val index = photos.indexOf(photo)
        photos.remove(photo)
        for(observer in observers) {
            observer.removedPhoto(index, photo)
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
            observer.replacedAllPhotos(photos)
        }
    }

    fun registerObserver(observer: SelectedPhotosObserver) {
        observers.add(observer)
    }

    fun unregisterObserver(observer: SelectedPhotosObserver) {
        observers.remove(observer)
    }
}