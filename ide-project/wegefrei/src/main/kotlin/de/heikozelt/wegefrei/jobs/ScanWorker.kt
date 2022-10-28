package de.heikozelt.wegefrei.jobs

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.ImageFilenameFilter
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.scanframe.ScanFrame
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
 * todo Indexieren, Fotos in der Datenbank fortlaufend nummerieren für AllPhotosPanel-List-Modell.
 */
class ScanWorker(
    private val scanFrame: ScanFrame,
    private val photosDirectory: String,
    private val databaseRepo: DatabaseRepo
    ): SwingWorker<Boolean, ScanWorker.Companion.IntermediateResult>()
{
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var phase = IntermediateResult.START

    /**
     * This is done in own Thread
     * get list of filenames and for each file:
     * check if already in database
     * if not, read metadata from file and forward photo in pipeline
     *
     * progress:
     * <ol>
     *   <li>Dateinamen lesen 0...10%</li>
     *   <li>Metadaten lesen 11...90%</li>
     *   <li>Indexieren 91...100%</li>
     * </ol>
     */
    override fun doInBackground(): Boolean {
        log.info("doInBackground() scanning for new images...")
        progress = 0
        val dir = File(photosDirectory)
        if (!dir.isDirectory) {
            log.error("$photosDirectory ist kein Verzeichnis.")
            return false
        }
        val filenames = dir.list(ImageFilenameFilter())
        progress = 10
        if(filenames == null){
            log.error("dir.list() liefert keine Liste. Was ist schief gelaufen?")
            return false
        }
        publish(IntermediateResult.FILE_NAME)
        for ((i, filename) in filenames.withIndex()) {
            progress = 10 + 80 * i / filenames.size
            publish(IntermediateResult.META_DATA)
            log.debug(filename)
            //JUL logger.log(Level.FINE, filename)
            if (databaseRepo.getPhotoByFilename(filename) == null) {
                log.debug("image from file system is new")
                val photo = readPhotoMetadata(File(photosDirectory, filename))
                databaseRepo.insertPhoto(photo)
            } else {
                log.debug("image from file system is already in database")
            }
        }
        return true
    }

    /**
     * insert chunks of photos into database
     * läuft auch im EDT
     */
    override fun process(chunks: List<IntermediateResult>) {
        log.debug("processing list of dummy chunks. chunks.size=${chunks.size} Update progress bar.")
        scanFrame.updateProgressBar(progress)
        if(chunks.last() != phase) {
            phase = chunks.last()
            scanFrame.updatePhase(phase)
        }
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        scanFrame.done()
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

    companion object {
        enum class IntermediateResult {
            START, FILE_NAME, META_DATA, INDEX_ENTRY
        }
    }
}