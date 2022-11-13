package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.DatabaseRepo

class LoadPhotoEntityTask(
    private val photoLoader: PhotoLoader,
    private val databaseRepo: DatabaseRepo,
    private val photo: Photo
) : Runnable {
    override fun run() {
        val photoEntity = databaseRepo.findPhotoByPath(photo.getPath())
        if(photoEntity == null) {
            photo.abortedLoadingEntity()
        } else {
            photo.setPhotoEntity(photoEntity)
        }
        photoLoader.doneLoadingEntity(photo)
    }
}
