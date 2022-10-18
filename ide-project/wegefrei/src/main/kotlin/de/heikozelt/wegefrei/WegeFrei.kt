package de.heikozelt.wegefrei

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.NoticesFrame
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class WegeFrei {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    //kotlin-logging: private val log = KotlinLogging.logger {}
    //JUL: private val logger = Logger.getLogger(this::class.java.name)

    private val databaseService = DatabaseService()

    private val noticesFrame = NoticesFrame(this)

    init{
        log.debug("initializing")
        noticesFrame.loadData()
    }

    fun getDatabaseService(): DatabaseService {
        return databaseService
    }

    /**
     * called, when new notice is saved, added to database
     */
    fun noticeAdded(notice: Notice) {
        noticesFrame.noticeAdded(notice)
    }

    /**
     * called, when existing notice is saved, updated in database
     */
    fun noticeUpdated(notice: Notice) {
        noticesFrame.noticeUpdated(notice)
    }

    /**
     * called, when existing notice is deleted
     */
    fun noticeDeleted(notice: Notice) {
        noticesFrame.noticeDeleted(notice)
    }

    // todo Prio 3: Hintergrundjob und Status-Balken anzeigen
    // todo Prio 3: erst Anzahl der Dateien ermitteln, dann einlesen
    fun scanForNewPhotos() {
        log.info("scanning for new images...")

        val dir = File(PHOTO_DIR)
        if (!dir.isDirectory) {
            log.error("$PHOTO_DIR ist kein Verzeichnis.")
            return
        }
        val filenames = dir.list(ImageFilenameFilter())
        for (filename in filenames) {
            log.debug(filename)
            //JUL logger.log(Level.FINE, filename)
            if (databaseService.getPhotoByFilename(filename) == null) {
                log.debug("image in filesystem is new")

                val photo = readPhotoMetadata(File(PHOTO_DIR, filename))
                //    ProofPhoto(filename, null, null, null)
                databaseService.insertPhoto(photo)
            } else {
                log.debug("image in filesystem is already in database")
            }
        }
    }

    fun readPhotoMetadata(file: File): Photo {
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
        // todo: Einstellungen in DB speichern und Ort der Datenbank via Kommando-Zeilen-Parameter übergeben
        const val PHOTO_DIR = "/media/veracrypt1/_Fotos/2022/03"

        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        @JvmStatic
        fun main(args: Array<String>) {
            LOG.info("Wege frei!")
            LOG.debug("Program arguments: ${args.joinToString()}")

            /*
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (e: Exception) {
                LOG.error("exception while setting look and feel", e)
            }
            */

            val shutdownHook = Thread { LOG.info("exit") }
            Runtime.getRuntime().addShutdownHook(shutdownHook)

            EventQueue.invokeLater { WegeFrei() }

            LOG.debug("de.heikozelt.wegefrei.main function finished")
        }

    }
}