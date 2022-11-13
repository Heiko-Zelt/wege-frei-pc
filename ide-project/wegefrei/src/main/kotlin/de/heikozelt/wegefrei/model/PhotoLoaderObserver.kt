package de.heikozelt.wegefrei.model

/**
 *
 */
interface PhotoLoaderObserver {
    fun doneLoadingFile(photo: Photo)
    fun doneLoadingEntity(photo: Photo)
}