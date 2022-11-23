package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.PhotoEntity
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Photo(private var path: Path): Comparable<Photo> {
    private var photoFile: PhotoFile? = null
    private var photoEntity: PhotoEntity? = null
    private var fileState = States.UNINITIALIZED
    private var entityState = States.UNINITIALIZED

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

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

    fun getDateTime(): ZonedDateTime? {
        photoEntity?.dateTime?.let {
            return it
        }
        photoFile?.dateTime?.let {
            return it
        }
        return null
    }

    fun getGeoPosition(): GeoPosition? {
        photoEntity?.getGeoPosition()?.let {
            log.debug("returns photoEntity.geoPosition()")
            return it
        }
        photoFile?.getGeoPosition()?.let {
            log.debug("returns photoFile.geoPosition()")
            return it
        }
        log.debug("returns neither nor")
        return null
    }

    fun getLatitude(): Double? {
        photoEntity?.latitude?.let {
            return it
        }
        photoFile?.latitude?.let {
            return it
        }
        return null
    }

    fun getLongitude(): Double? {
        photoEntity?.longitude?.let {
            return it
        }
        photoFile?.longitude?.let {
            return it
        }
        return null
    }

    fun getToolTipText(): String? {
        photoEntity?.let {
            return it.getToolTipText()
        }
        photoFile?.let {
            return it.getToolTipText()
        }
        return null
    }

    override fun compareTo(other: Photo): Int {
        return this.path.compareTo(other.path)
    }

    companion object {
        enum class States {
            UNINITIALIZED, LOADING, FOUND, NOT_FOUND
        }

        val dateTimeFormat: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss z")
    }

}