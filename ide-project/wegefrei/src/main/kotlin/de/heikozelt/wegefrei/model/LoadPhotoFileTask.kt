package de.heikozelt.wegefrei.model

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import javax.imageio.ImageIO

/**
 * todo: exception handling, file not found, access forbidden, call abortedLoadingFile()
 */
class LoadPhotoFileTask(private val photoLoader: PhotoLoader, private val photo: Photo): Runnable {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    override fun run() {
        log.debug("runs in ${Thread.currentThread().name}")
        val path = photo.getPath()
        val file = File(path.toString())

        var latitude: Double? = null
        var longitude: Double? = null
        var date: Date? = null
        val metadata = ImageMetadataReader.readMetadata(file)
        log.debug("metadata: $metadata")

        val gpsDirs = metadata.getDirectoriesOfType(GpsDirectory::class.java)
        for (gpsDir in gpsDirs) {
            val geoLocation: GeoLocation? = gpsDir.geoLocation
            if (geoLocation != null && !geoLocation.isZero) {
                log.debug("latitude: ${geoLocation.latitude}, longitude: ${geoLocation.longitude}")
                latitude = geoLocation.latitude
                longitude = geoLocation.longitude

            }
        }
        val exifDirs = metadata.getDirectoriesOfType(ExifSubIFDDirectory::class.java)
        for (exifDir in exifDirs) {
            date = exifDir.dateOriginal
            if (date != null) {
                log.debug("date: $date")
            }
        }
        val datTim = if (date == null) {
            null
        } else {
            ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
            //Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
        val sha1Hash = "0123456789abcdefghij".toByteArray()

        val img = ImageIO.read(file)
        val photoFile = PhotoFile(path, sha1Hash, latitude, longitude, datTim, img)
        photoFile.makeThumbnail()
        photo.setPhotoFile(photoFile)
        EventQueue.invokeLater {
            photoLoader.doneLoadingFile(photo)
        }
    }
}