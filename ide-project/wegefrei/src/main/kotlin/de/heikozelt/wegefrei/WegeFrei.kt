package de.heikozelt.wegefrei

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import de.heikozelt.wegefrei.noticesframe.NoticesFrame
import de.heikozelt.wegefrei.settingsframe.SettingsFrame
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import javax.swing.SwingUtilities
import javax.swing.UIManager

/**
 * Wege Frei! PC
 *
 * Diese Klasse ist der Ober-Manager.
 * Sie verwaltet die einzelnen Fenster vom Typ NoticesFrame (Übersichts-Seite),
 * NoticeFrame (neue oder bestehende Meldung bearbeiten) & SettingsFrame,
 * sowie die Einstellungen (Settings) und die Datenbank-Verbindung (DatabaseService).
 */
class WegeFrei {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    //kotlin-logging: private val log = KotlinLogging.logger {}
    //JUL: private val logger = Logger.getLogger(this::class.java.name)

    private val databaseService = DatabaseService()

    private var noticesFrame: NoticesFrame? = null

    private var settings: Settings? = null

    /**
     * reference to SettingsFrame, if window is shown on screen otherwise null
     */
    private var settingsFrame: SettingsFrame? = null

    /**
     * einzelne Meldungs-Fenster (neue oder bestehende Meldung bearbeiten)
     */
    private val noticeFrames = mutableListOf<NoticeFrame>()

    init{
        log.debug("initializing")
    }

    fun setSettings(settings: Settings) {
        this.settings = settings
        changeLookAndFeel()
    }

    fun openNoticesFrame() {
        noticesFrame = NoticesFrame(this)
        noticesFrame?.loadData()
    }

    /**
     * Opens Window to create new or edit existing notice
     * @param notice The notice to edit or new notice if omitted.
     */
    fun openNoticeFrame(notice: Notice = Notice()) {
        log.debug("Anzahl NoticeFrames: " + noticeFrames.size)
        val noticeFrame = NoticeFrame(this)
        noticeFrames.add(noticeFrame)
        EventQueue.invokeLater {
            // Thread.sleep(5000) // simulate slowness
            noticeFrame.loadData(notice)
        }
    }

    /**
     * must be called to prevent memory leaks
     */
    fun noticeFrameClosed(noticeFrame: NoticeFrame) {
        noticeFrames.remove(noticeFrame)
    }

    /**
     * Is called when Settings (may) have changed.
     */
    fun settingsChanged() {
        settings?.let {
            changeLookAndFeel()
        }
    }

    /**
     * Öffnet das Einstellungen-Fenster.
     * (Wenn es schon geöffnet ist, dann wird es nur in den Vordergrund gebracht.)
     */
    fun openSettingsFrame() {
        if (settingsFrame == null) {
            settingsFrame = SettingsFrame(this)
            settingsFrame?.setSettings(settings)
        } else {
            settingsFrame?.toFront()
        }
    }

    /**
     * must be called, when settings frame is closed
     */
    fun settingsFrameClosed() {
        this.settingsFrame = null
    }

    fun getDatabaseService(): DatabaseService {
        return databaseService
    }

    /**
     * called, when new notice is saved, added to database
     */
    fun noticeAdded(notice: Notice) {
        noticesFrame?.noticeAdded(notice)
    }

    /**
     * called, when existing notice is saved, updated in database
     */
    fun noticeUpdated(notice: Notice) {
        noticesFrame?.noticeUpdated(notice)
    }

    /**
     * called, when existing notice is deleted
     */
    fun noticeDeleted(notice: Notice) {
        noticesFrame?.noticeDeleted(notice)
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

    private fun changeLookAndFeel() {
        settings?.let { setti ->
            try {
                // todo Prio 1: set look and feel according to settings
                LOG.info("look & feel name: ${setti.lookAndFeel}")
                val className = setti.getLookAndFeelInfo()?.className
                LOG.debug("look & feel class name: $className")
                className?.let {
                    UIManager.setLookAndFeel(className);
                }
                noticesFrame?.let { frame -> SwingUtilities.updateComponentTreeUI(frame) }
                noticeFrames.forEach { frame -> SwingUtilities.updateComponentTreeUI(frame) }
            } catch (e: Exception) {
                LOG.error("exception while setting look and feel", e)
            }
        }
    }

    companion object {
        // todo Prio 1: Einstellungen in Settings-Datei speichern
        const val PHOTO_DIR = "/media/veracrypt1/_Fotos/2022/03"

        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        @JvmStatic
        fun main(args: Array<String>) {
            LOG.info("Wege frei!")
            //LOG.debug("Program arguments: ${args.joinToString()}")

            val settings = Settings.loadFromFile()

            val shutdownHook = Thread { LOG.info("exit") }
            Runtime.getRuntime().addShutdownHook(shutdownHook)

            EventQueue.invokeLater {
                val app = WegeFrei()
                app.setSettings(settings)
                app.openNoticesFrame()
            }

            LOG.debug("de.heikozelt.wegefrei.Main.main()-method finished")
        }

    }
}