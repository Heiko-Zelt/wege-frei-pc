package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.PhotoEntity
import java.nio.file.Path

class Photo(private var path: Path) {
    private var photoFile: PhotoFile? = null
    private var photoEntity: PhotoEntity? = null
    private var fileState = States.UNINITIALIZED
    private var entityState = States.UNINITIALIZED

    init {
        fileState = States.UNINITIALIZED
        entityState = States.UNINITIALIZED
    }

    fun getPath(): Path {
        return path
    }

    fun getPhotoFile(): PhotoFile? {
        return photoFile
    }

    fun getPhotoEntity(): PhotoEntity? {
        return photoEntity
    }

    fun startedLoadingFile() {
        fileState = States.LOADING
    }

    fun setPhotoFile(photoFile: PhotoFile) {
        this.photoFile = photoFile
        fileState = States.FOUND
    }

    fun abortedLoadingFile() {
        fileState = States.NOT_FOUND
    }

    fun getFileState(): States {
        return fileState
    }

    fun startedLoadingEntity() {
        entityState = States.LOADING
    }

    fun setPhotoEntity(photoEntity: PhotoEntity) {
        this.photoEntity = photoEntity
        entityState = States.FOUND
    }

    fun abortedLoadingEntity() {
        entityState = States.NOT_FOUND
    }

    fun getEntityState(): States {
        return entityState
    }

    fun copyMetaDataFromFileToEntity() {
        if(photoEntity == null) {
            photoEntity = PhotoEntity()
        }
        photoEntity?.hash = photoFile?.hash
        photoEntity?.path = photoFile?.path.toString()
        photoEntity?.latitude = photoFile?.latitude
        photoEntity?.longitude = photoFile?.longitude
        photoEntity?.dateTime = photoFile?.dateTime
    }

    fun getToolTipText(): String? {
        photoFile?.let {
            return it.getToolTipText()
        }
        photoEntity?.let {
            return it.getToolTipText()
        }
        return null
    }

    companion object {
        enum class States {
            UNINITIALIZED, LOADING, FOUND, NOT_FOUND
        }
    }
}