package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo
import java.util.*

/**
 * Beobachter sind AllPhotosPanel, SelectedPhotosPanel, MiniMap und MaxiMap
 */
interface SelectedPhotosObserver {
    fun addedPhoto(index: Int, photo: Photo)
    fun removedPhoto(index: Int, photo: Photo)

    /**
     * Das geht schneller, als alle Fotos einzeln zu entfernen und einzeln hinzuzufügen.
     * Es ist aber für die Observer eine zusätzliche Methode zu implementieren.
     * In der Oberfläche dauert es minimal länger, bis ein Feedback zu sehen ist.
     */
    fun replacedAllPhotos(photos: TreeSet<Photo>)
}