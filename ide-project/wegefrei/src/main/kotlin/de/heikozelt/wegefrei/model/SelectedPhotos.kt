package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo
import java.util.*

class SelectedPhotos {
    /**
     * Fotos sind immer nach Dateiname (also in der Regel auch chronologisch) sortiert
     */
    private val photos = TreeSet<Photo>()
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

    fun registerObserver(observer: SelectedPhotosObserver) {
        observers.add(observer)
    }

    fun unregisterObserver(observer: SelectedPhotosObserver) {
        observers.remove(observer)
    }
}