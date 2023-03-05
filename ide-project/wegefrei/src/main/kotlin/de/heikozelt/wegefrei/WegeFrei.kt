package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.email.addressbook.AddressBookFrame
import de.heikozelt.wegefrei.email.useragent.EmailSender
import de.heikozelt.wegefrei.email.useragent.EmailUserAgent
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.model.LeastRecentlyUsedCache
import de.heikozelt.wegefrei.model.NoticesOutbox
import de.heikozelt.wegefrei.model.Photo
import de.heikozelt.wegefrei.model.PhotoLoader
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import de.heikozelt.wegefrei.noticesframe.NoticesFrame
import de.heikozelt.wegefrei.scanframe.ScanFrame
import de.heikozelt.wegefrei.settingsframe.SettingsFrame
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.DefaultTileFactory
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.nio.file.Path
import javax.swing.JOptionPane
import javax.swing.JOptionPane.QUESTION_MESSAGE
import javax.swing.JOptionPane.YES_NO_OPTION
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
 * todo Prio 3: Logo/Icon für die Anwendung
 * todo Prio 1: update notice in NoticesFrame after it was sent
 */
open class WegeFrei(private val settingsRepo: SettingsRepo = SettingsFileRepo()) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    //kotlin-logging: private val log = KotlinLogging.logger {}
    //JUL: private val logger = Logger.getLogger(this::class.java.name)

    private var databaseRepo: DatabaseRepo? = null

    private var settings: Settings? = null

    private var emailUserAgent: EmailUserAgent = EmailUserAgent()

    /**
     * Übersichts-Fenster (falls geöffnet sonst null)
     */
    private var noticesFrame: NoticesFrame? = null

    /**
     * reference to the Settings-Window, if the window is shown on screen otherwise null
     */
    private var settingsFrame: SettingsFrame? = null

    /**
     * reference to the address book window, if the window is shown on screen otherwise null
     */
    private var addressBookFrame: AddressBookFrame? = null

    /**
     * einzelne Meldungs-Fenster (neue oder bestehende Meldung bearbeiten)
     */
    private val noticeFrames = mutableListOf<NoticeFrame>()

    /**
     * Singleton used by multiple JxMapViewer instances
     */
    private val tileFactoryInfo = OSMTileFactoryInfo()
    private val tileFactory = DefaultTileFactory(tileFactoryInfo)

    private var photoCache = LeastRecentlyUsedCache<Path, Photo>(128)
    private var photoLoader: PhotoLoader? = null

    private val noticesOutbox = NoticesOutbox()
    private val emailSender = EmailSender(noticesOutbox, emailUserAgent)

    init {
        log.debug("initializing")
        val settings = settingsRepo.load()
        setSettings(settings)
        emailUserAgent?.setEmailServerConfig(settings.emailServerConfig)
    }

    fun getStarted() {
        settings?.let { setti ->
            if (setti.privacyConsent == PRIVACY_CONSENT_VERSION) {
                openNoticesFrame()
            } else {
                val text = """
                    |<html>
                    |  <p>Diese Software verwendet Web-Services/APIs.</p>
                    |  <p>Bei der Benutzung wird Deine IP-Adresse daher an folgende Websites übermittelt:</p>
                    |  <ul>
                    |    <li>https://tile.openstreetmap.org/<br>
                    |      für die Kartendarstellung (Download von Kachel-Bildern)</li>
                    |    <li>https://nominatim.openstreetmap.org/<br>
                    |      für die Suche nach Postanschriften/Adressen von Tatorten</li> 
                    |  </ul>
                    |</html>""".trimMargin().replace("\n", "")
                log.debug(text)
                val result = JOptionPane.showOptionDialog(
                    null,
                    text,
                    "Datenschutzvereinbarung",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE,
                    null,
                    arrayOf("Zustimmen", "Ablehnen"),
                    null
                )
                // todo save result
                log.info("result = $result")
                // result is array index or -1
                if (result == 0) {
                    openNoticesFrame()
                    setti.privacyConsent = PRIVACY_CONSENT_VERSION
                    settingsRepo.save(setti)
                    databaseRepo?.let {
                      noticesOutbox.setDatabaseRepo(it)
                    }
                    noticesOutbox.setSettings(setti)
                }
            }
        }
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

        // initial ist this.settings == null. dbDirChanged also true
        val dbDirChanged = settings.databaseDirectory != this.settings?.databaseDirectory
        log.debug("dbDirChanged: $dbDirChanged")
        val photosDirChanged = settings.photosDirectory != this.settings?.photosDirectory
        log.debug("photosDirChanged: $photosDirChanged")
        val lookAndFeelChanged = settings.lookAndFeel != this.settings?.lookAndFeel
        log.debug("lookAndFeel: before: ${this.settings?.lookAndFeel}, after: ${settings.lookAndFeel}, changed: $lookAndFeelChanged")

        val isNoticesFrameOpen = noticesFrame != null
        log.debug("isNoticesFrameOpen: $isNoticesFrameOpen")

        this.settings = settings

        // todo Prio 2: close all NoticeFrames if repo changed. ask user if unsaved changes exist.

        if (lookAndFeelChanged) {
            changeLookAndFeel()
        }

        settings.databaseDirectory?.let {
            if (dbDirChanged) {
                closeNoticesFrame()
                databaseRepo?.close()
                databaseRepo = DatabaseRepo.fromDirectory(it)
                databaseRepo?.let {dbRe ->
                    photoLoader = PhotoLoader(dbRe)
                    noticesOutbox.setDatabaseRepo(dbRe)
                }

                if (isNoticesFrameOpen) {
                    openNoticesFrame()
                }
            }
        }

        noticesOutbox.setSettings(settings)
    }

    fun getEmailUserAgent(): EmailUserAgent? {
        return emailUserAgent
    }

    fun getNoticesOutbox(): NoticesOutbox {
        return noticesOutbox
    }

    fun startSendingEmails() {
        if(!emailSender.isAlive) {
            emailSender.start()
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
     * @param noticeEntity The notice to edit or new notice if omitted.
     */
    fun openNoticeFrame(noticeEntity: NoticeEntity = NoticeEntity.createdNow()) {
        log.debug("Anzahl NoticeFrames: " + noticeFrames.size)
        databaseRepo?.let { dbRepo ->
            photoLoader?.let { pLoader ->
                val noticeFrame = NoticeFrame(this, dbRepo, tileFactory, photoCache, pLoader)
                noticeFrames.add(noticeFrame)
                EventQueue.invokeLater {
                    // Thread.sleep(5000) // simulate slowness
                    noticeFrame.setNotice(noticeEntity)
                }
            }
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
            settings?.let { setti ->
               settingsFrame?.setSettings(setti.clone())
            }
        } else {
            settingsFrame?.toFront()
        }
    }

    fun openAddressBook() {
        if (addressBookFrame == null) {
            log.debug("open address book")
            databaseRepo?.let {
                addressBookFrame = AddressBookFrame(this, it)
                addressBookFrame?.loadAddresses()
            }
        } else {
            addressBookFrame?.toFront()
        }
    }

    /**
     * must be called, when settings frame is closed
     */
    fun settingsFrameClosed() {
        this.settingsFrame = null
    }

    /**
     * must be called, when address book frame is closed
     */
    fun addressBookFrameClosed() {
        this.addressBookFrame = null
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
    fun noticeAdded(noticeEntity: NoticeEntity) {
        log.debug("noticeAdded(id=${noticeEntity.id})")
        noticesFrame?.noticeAdded(noticeEntity)
    }

    /**
     * called, when existing notice is saved, updated in database
     */
    fun noticeUpdated(noticeEntity: NoticeEntity) {
        log.debug("noticeUpdated(id=${noticeEntity.id})")
        noticesFrame?.noticeUpdated(noticeEntity)
    }

    /**
     * called, when existing notice is deleted
     */
    fun noticeDeleted(noticeEntity: NoticeEntity) {
        noticesFrame?.noticeDeleted(noticeEntity)
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
                noticesFrame?.let(SwingUtilities::updateComponentTreeUI)
                noticeFrames.forEach(SwingUtilities::updateComponentTreeUI)
                // settingsFrame should not be open
            } catch (e: Exception) {
                log.error("exception while setting look and feel", e)
            }
        }
    }

    /**
     * Scannt die Fotos im Dateisystem und trägt die Metadaten in die Datenbank ein.
     * todo Prio 1: SHA1-Hashwert über Dateiinhalt als Primärschlüssel für Fotos.
     */
    fun scanForNewPhotos() {
        val scanFrame = ScanFrame()
        settings?.photosDirectory?.let { photosDir ->
            databaseRepo?.let { dbRepo ->
                scanFrame.scan(photosDir, dbRepo)
            }
        }
    }

    /**
     * I prefer the main class having a name of WegeFrei instead of WegeFreiKt.
     * Defining a companion object mit JvmStatic function main
     * instead of a main function outside a class solves it.
     * Maybe the annotation @file:JvmName() would do the job as well.
     */
    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        private const val PRIVACY_CONSENT_VERSION = 1

        @JvmStatic
        fun main(args: Array<String>) {
            LOG.info("Wege frei!")
            //LOG.debug("Program arguments: ${args.joinToString()}")

            val shutdownHook = Thread { LOG.info("exit") }
            Runtime.getRuntime().addShutdownHook(shutdownHook)

            EventQueue.invokeLater {
                val app = WegeFrei()
                app.getStarted()
            }

            LOG.debug("de.heikozelt.wegefrei.Main.main()-method finished")
        }

    }
}