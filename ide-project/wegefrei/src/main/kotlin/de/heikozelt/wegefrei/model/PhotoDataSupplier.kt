package de.heikozelt.wegefrei.model

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.function.Supplier
import javax.imageio.ImageIO

/**
 * to be used with CompletableFuture.supplyAsync()
 */
class PhotoDataSupplier(private val path: Path): Supplier<PhotoData> {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    override fun get(): PhotoData {
        val file = File(path.toString())

        var latitude: Float? = null
        var longitude: Float? = null
        var date: Date? = null
        val metadata = ImageMetadataReader.readMetadata(file)
        log.debug("metadata: $metadata")

        val gpsDirs = metadata.getDirectoriesOfType(GpsDirectory::class.java)
        for (gpsDir in gpsDirs) {
            val geoLocation: GeoLocation? = gpsDir.geoLocation
            if (geoLocation != null && !geoLocation.isZero) {
                log.debug("latitude: ${geoLocation.latitude}, longitude: ${geoLocation.longitude}")
                latitude = geoLocation.latitude.toFloat()
                longitude = geoLocation.longitude.toFloat()

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
        return PhotoData(sha1Hash, latitude, longitude, datTim, img)
    }

}