package de.heikozelt.wegefrei.jobs

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.ImageFilenameFilter
import de.heikozelt.wegefrei.entities.Photo
import org.slf4j.LoggerFactory
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import javax.swing.SwingWorker

/**
 * Scannt Verzeichnis mit Fotos nach Dateinamen und Metadaten.
 * Das Ergebnis wird in die Datenbank geschrieben.
 * Die Pixel-Daten verbleiben im Dateisystem (Lazy loading).
 * todo Prio 3: Hintergrundjob und Status-Balken anzeigen
 * todo Prio 3: erst Anzahl der Dateien ermitteln, dann einlesen
 */
class ScanWorker(private val photosDirectory: String, private val databaseRepo: DatabaseRepo): SwingWorker<List<Boolean>, Boolean>() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * This is done in own Thread
     * get list of filenames and for each file:
     * check if already in database
     * if not, read metadata from file and forward photo in pipeline
     */
    override fun doInBackground(): List<Boolean> {
        log.info("doInBackground() scanning for new images...")
        val dir = File(photosDirectory)
        if (!dir.isDirectory) {
            log.error("$photosDirectory ist kein Verzeichnis.")
            return emptyList()
        }
        val filenames = dir.list(ImageFilenameFilter())
        if(filenames == null){
            log.error("dir.list() liefert keine Liste. Was ist schief gelaufen?")
            return emptyList()
        }
        for ((i, filename) in filenames.withIndex()) {
            progress = 100 * i / filenames.size
            log.debug(filename)
            //JUL logger.log(Level.FINE, filename)
            if (databaseRepo.getPhotoByFilename(filename) == null) {
                log.debug("image from file system is new")
                val photo = readPhotoMetadata(File(photosDirectory, filename))
                databaseRepo.insertPhoto(photo)
                publish(true)
            } else {
                log.debug("image from file system is already in database")
            }
        }
        return emptyList()
    }

    /**
     * insert chunks of photos into database
     * läuft auch im EDT
     */
    override fun process(chunks: List<Boolean>) {
        log.debug("processing list of dummy chunks. chunks.size=${chunks.size} Update progress bar.")
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        log.debug("done()")
    }

    private fun readPhotoMetadata(file: File): Photo {
        var latitude: Float? = null
        var longitude: Float? = null
        var date: Date? = null
        val metadata = ImageMetadataReader.readMetadata(file)
        log.debug("metadata: $metadata")

        val gpsDirs = metadata.getDirectoriesOfType(GpsDirectory::class.java)
        for(gpsDir in gpsDirs) {
            val geoLocation: GeoLocation? = gpsDir.geoLocation
            if(geoLocation != null && !geoLocation.isZero) {
                log.debug("latitude: ${geoLocation.latitude}, longitude: ${geoLocation.longitude}")
                latitude = geoLocation.latitude.toFloat()
                longitude = geoLocation.longitude.toFloat()

            }
        }
        val exifDirs = metadata.getDirectoriesOfType(ExifSubIFDDirectory::class.java)
        for(exifDir in exifDirs) {
            date = exifDir.dateOriginal
            if(date != null) {
                log.debug("date: $date")
            }
        }
        val datTim = if(date == null) {
            null
        } else {
            ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
            //Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
        return Photo(file.name, latitude, longitude, datTim, null)
    }
}