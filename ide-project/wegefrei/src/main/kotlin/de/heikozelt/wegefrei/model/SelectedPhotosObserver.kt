package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.PhotoEntity
import java.util.*

/**
 * Beobachter sind AllPhotosPanel, SelectedPhotosPanel, MiniMap und MaxiMap
 */
interface SelectedPhotosObserver {
    fun selectedPhoto(index: Int, photoEntity: PhotoEntity)
    fun unselectedPhoto(index: Int, photoEntity: PhotoEntity)

    /**
     * Das geht schneller, als alle Fotos einzeln zu entfernen und einzeln hinzuzufügen.
     * Es ist aber für die Observer eine zusätzliche Methode zu implementieren.
     * In der Oberfläche dauert es minimal länger, bis ein Feedback zu sehen ist.
     */
    fun replacedPhotoSelection(photoEntities: TreeSet<PhotoEntity>)
}