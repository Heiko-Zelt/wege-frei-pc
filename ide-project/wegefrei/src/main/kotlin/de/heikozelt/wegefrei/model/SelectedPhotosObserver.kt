package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo

/**
 * Beobachter sind AllPhotosPanel, SelectedPhotosPanel, MiniMap und MaxiMap
 */
interface SelectedPhotosObserver {
    fun addedPhoto(index: Int, photo: Photo)
    fun removedPhoto(index: Int, photo: Photo)
}