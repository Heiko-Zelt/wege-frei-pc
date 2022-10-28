package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import de.heikozelt.wegefrei.noticesframe.NoticesFrame
import de.heikozelt.wegefrei.scanframe.ScanFrame
import de.heikozelt.wegefrei.settingsframe.SettingsFrame
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import javax.swing.SwingUtilities
import javax.swing.UIManager

/**
 * Wege Frei! PC
 *
 * Diese Klasse ist der Ober-Manager.
 * Sie verwaltet die einzelnen Fenster vom Typ NoticesFrame (Übersichts-Seite),
 * NoticeFrame (neue oder bestehende Meldung bearbeiten) & SettingsFrame,
 * sowie die Einstellungen (Settings) und die Datenbank-Verbindung (DatabaseService).
 * @param settingsRepo constructor injection (enables unit tests with test settings)
 */
open class WegeFrei(private val settingsRepo: SettingsRepo = SettingsFileRepo()) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    //kotlin-logging: private val log = KotlinLogging.logger {}
    //JUL: private val logger = Logger.getLogger(this::class.java.name)

    private var databaseRepo:DatabaseRepo? = null

    private var settings: Settings? = null

    /**
     * Übersichts-Fenster (falls geöffnet sonst null)
     */
    private var noticesFrame: NoticesFrame? = null

    /**
     * reference to the Settings-Window, if the window is shown on screen otherwise null
     */
    private var settingsFrame: SettingsFrame? = null

    /**
     * einzelne Meldungs-Fenster (neue oder bestehende Meldung bearbeiten)
     */
    private val noticeFrames = mutableListOf<NoticeFrame>()

    init{
        log.debug("initializing")
        val settings = settingsRepo.load()

        //val app = WegeFrei()
        setSettings(settings)
    }

    /**
     * simple getter method
     */
    fun getSettings(): Settings? {
        return settings
    }

    /**
     * set settings (without saving to file)
     */
    private fun setSettings(settings: Settings) {
        log.debug("setSettings()")
        val dbDirChanged = settings.databaseDirectory != this.settings?.databaseDirectory
        log.debug("dbDirChanged: $dbDirChanged")
        val photosDirChanged = settings.photosDirectory != this.settings?.photosDirectory
        log.debug("photosDirChanged: $photosDirChanged")
        val lookAndFeelChanged = settings.lookAndFeel != this.settings?.lookAndFeel
        log.debug("lookAndFeel: before: ${this.settings?.lookAndFeel}, after: ${settings.lookAndFeel}, changed: $lookAndFeelChanged")

        val isNoticesFrameOpen = noticesFrame != null
        log.debug("isNoticesFrameOpen: $isNoticesFrameOpen")

        this.settings = settings

        // todo close all NoticeFrames if repo changed
        // but how to react to unsaved changes??? just save them or ask user???

        if(lookAndFeelChanged) {
            changeLookAndFeel()
        }

        if(dbDirChanged) {
            closeNoticesFrame()

            databaseRepo?.close()
            databaseRepo = DatabaseRepo.fromDirectory(settings.databaseDirectory)

            if(isNoticesFrameOpen) {
                openNoticesFrame()
            }
        }
    }

    /**
     * This method is called when Settings (may) have changed.
     * Settings are also saved to file.
     */
    fun settingsChanged(settings: Settings?) {
        log.debug("settingsChanged()")
        settings?.let {
            setSettings(it)
            settingsRepo.save(it)
        }
    }

    fun openNoticesFrame() {
        noticesFrame = NoticesFrame(this)
        noticesFrame?.loadData()
    }

    fun closeNoticesFrame() {
        noticesFrame?.let {
            it.isVisible = false
            it.dispose()
        }
        noticesFrame = null
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
     * Öffnet das Einstellungen-Fenster.
     * (Wenn es schon geöffnet ist, dann wird es nur in den Vordergrund gebracht.)
     */
    fun openSettingsFrame() {
        if (settingsFrame == null) {
            settingsFrame = SettingsFrame(this)
            // The Application needs to remember its original settings.
            // settingsFrame is not allowed to change them directly.
            // so the object must be cloned.
            settingsFrame?.setSettings(settings?.clone())
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

    /**
     * simple getter method
     */
    fun getDatabaseRepo(): DatabaseRepo? {
        return databaseRepo
    }

    /**
     * simple getter method
     */
    fun getSettingsRepo(): SettingsRepo {
        return settingsRepo
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

    private fun changeLookAndFeel() {
        log.debug("changeLookAndFeel()")
        settings?.let { setti ->
            try {
                // todo Prio 1: set look and feel according to settings
                log.info("look & feel name: ${setti.lookAndFeel}")
                val className = setti.getLookAndFeelInfo()?.className
                log.debug("look & feel class name: $className")
                className?.let {
                    log.debug("UIManager.setLookAndFeel()")
                    UIManager.setLookAndFeel(className)
                }
                noticesFrame?.let { frame -> SwingUtilities.updateComponentTreeUI(frame) }
                noticeFrames.forEach { frame -> SwingUtilities.updateComponentTreeUI(frame) }
                // settingsFrame should not be open
            } catch (e: Exception) {
                log.error("exception while setting look and feel", e)
            }
        }
    }

    /**
     * Scannt die Fotos im Dateisystem und trägt die Metadaten in die Datenbank ein.
     * todo: SHA1-Hashwert über Dateiinhalt als Primärschlüssel für Fotos.
     */
    fun scanForNewPhotos() {
        val scanFrame = ScanFrame()
        settings?.photosDirectory?.let {photosDir ->
            databaseRepo?.let { dbRepo ->
                scanFrame.scan(photosDir, dbRepo)
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        @JvmStatic
        fun main(args: Array<String>) {
            LOG.info("Wege frei!")
            //LOG.debug("Program arguments: ${args.joinToString()}")

            val shutdownHook = Thread { LOG.info("exit") }
            Runtime.getRuntime().addShutdownHook(shutdownHook)

            EventQueue.invokeLater {
                val app = WegeFrei()
                app.openNoticesFrame()
            }

            LOG.debug("de.heikozelt.wegefrei.Main.main()-method finished")
        }

    }
}