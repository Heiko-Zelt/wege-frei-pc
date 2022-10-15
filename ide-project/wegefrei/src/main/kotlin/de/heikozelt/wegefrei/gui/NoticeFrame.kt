package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.App
import de.heikozelt.wegefrei.DatabaseService
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.FRAME_BACKGROUND
import de.heikozelt.wegefrei.model.SelectedPhotos
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime
import java.util.*
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JSplitPane

/**
 * Haupt-Fenster zum Bearbeiten einer Meldung
 * (mit Dispatcher-Funktionen)
 * Instanziierung
 * <ol>
 *   <li>ohne Parameter zum Bearbeiten einer neuen Meldung. notice.id ist null.</li>
 *   <li>Instanziierung mit Notice als Parameter zum Bearbeiten einer bestehenden Meldung. notice.id enthält eine Zahl.</li>
 * </ol>
 */
class NoticeFrame(private val app: App, private val notice: Notice) : JFrame() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var selectedPhotos = SelectedPhotos(TreeSet(notice.photos))
    private var allPhotosPanel = AllPhotosPanel(this, "20220301_184952.jpg")
    private var selectedPhotosPanel = SelectedPhotosPanel(this)
    private var selectedPhotosScrollPane = JScrollPane(selectedPhotosPanel)
    private var noticeForm = NoticeForm(this)
    private var zoomPanel = ZoomPanel(this)

    private var topSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, allPhotosPanel, selectedPhotosScrollPane)
    private var bottomSplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, noticeForm, zoomPanel)
    private var mainSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, bottomSplitPane)

    init {
        log.debug("init, notice id: ${notice.id}")
        title = if(notice.id == null) {
            "Neue Meldung - Wege frei!"
        } else {
            "Meldung #${notice.id} - Wege frei!"
        }

        selectedPhotos.registerObserver(selectedPhotosPanel)
        selectedPhotos.registerObserver(allPhotosPanel)

        background = FRAME_BACKGROUND
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(1000, 700)

        topSplitPane.apply {
            isOneTouchExpandable = true
            setDividerLocation(0.5)
        }

        bottomSplitPane.apply {
            isOneTouchExpandable = true
            setDividerLocation(0.5)
        }

        mainSplitPane.apply {
            isOneTouchExpandable = true
            setDividerLocation(0.4)
        }
        add(mainSplitPane)
        isVisible = true
        log.debug("NoticeFrame.init() finished")
    }

    fun getDatabaseService(): DatabaseService {
        return app.getDatabaseService()
    }

    /**
     * wählt ein Foto aus

    fun selectPhoto(miniPhotoPanel: MiniPhotoPanel) {
    log.debug("select photo")
    allPhotosPanel.deactivatePhoto(miniPhotoPanel)
    val photo = miniPhotoPanel.getPhoto()
    selectedPhotosPanel.addPhoto(photo)
    log.debug("selected photo: $photo")
    log.debug("zoomed photo: ${zoomPanel.getMaxiPhoto()}")
    zoomPanel.showSelectedPhoto(photo)
    allPhotosPanel.hideBorder()
    selectedPhotosPanel.showBorder(photo)
    }
     */

    fun getSelectedPhotos(): SelectedPhotos {
        return selectedPhotos
    }

    fun setSelectedPhotos(selectedPhotos: SelectedPhotos) {
        this.selectedPhotos = selectedPhotos
    }

    fun getNotice(): Notice {
        return notice
    }

    /**
     * wählt ein Foto aus
     */
    fun selectPhoto(photo: Photo) {
        log.debug("select photo")
        selectedPhotos.add(photo)
        //allPhotosPanel.deactivatePhoto(photo)
        log.debug("selected photo: $photo")
        log.debug("zoomed photo: ${zoomPanel.getMaxiPhoto()}")
        zoomPanel.showSelectedPhoto(photo)
        selectedPhotosPanel.showBorder(photo)

        //val index = selectedPhotosPanel.indexOfPhoto(photo)
        //val index = selectedPhotos.getPhotos().indexOf(photo)
        //noticeForm.getMiniMap().addMarker(index, photo)
    }

    /**
     * entfernt ein Foto aus der Auswahl für die Meldung
    fun unselectPhoto(photoPanel: MiniSelectedPhotoPanel) {
    log.debug("unselect photo")
    selectedPhotosPanel.removePhoto(photoPanel)
    allPhotosPanel.activatePhoto(photoPanel.getPhoto())
    val photo = photoPanel.getPhoto()
    zoomPanel.showPhoto(photo)
    allPhotosPanel.showBorder(photo)
    selectedPhotosPanel.hideBorder()
    }
     */

    /**
     * entfernt ein Foto aus der Auswahl für die Meldung
     */
    fun unselectPhoto(photo: Photo) {
        log.debug("unselect photo")
        //val index = selectedPhotosPanel.indexOfPhoto(photo)
        //val index = selectedPhotos.getPhotos().indexOf(photo)
        //noticeForm.getMiniMap().removeMarker(index)
        //selectedPhotosPanel.removePhoto(photo)
        selectedPhotos.remove(photo)
        zoomPanel.showPhoto(photo)
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich eine große Landkarte an
     */
    fun showMaxiMap() {
        log.debug("show maxi map")
        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(true)
        zoomPanel.showMap()
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich ein großes Foto an
     */
    fun showPhoto(miniPhotoPanel: MiniPhotoPanel) {
        log.debug("show photo")
        zoomPanel.showPhoto(miniPhotoPanel.getPhoto())
        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        allPhotosPanel.showBorder(miniPhotoPanel)
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich ein großes bereits ausgewähltes Foto an
     */
    fun showSelectedPhoto(miniSelectedPhotoPanel: MiniSelectedPhotoPanel) {
        log.debug("show selected photo")
        zoomPanel.showSelectedPhoto(miniSelectedPhotoPanel.getPhoto())
        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        allPhotosPanel.hideBorder()
        selectedPhotosPanel.showBorder(miniSelectedPhotoPanel)
    }


    fun findAddress(position: GeoPosition) {
        val worker = AddressWorker(position, noticeForm)
        worker.execute()
    }

    fun saveNotice() {
        noticeForm.getNoticeFormFields().saveNotice()
        val dbService = app.getDatabaseService()
        if(notice.id == null) {
            dbService.insertNotice(notice)
            app.noticeAdded(notice)
        } else {
            dbService.updateNotice(notice)
            app.noticeUpdated(notice)
        }
    }

    fun deleteNotice() {
        val dbService = app.getDatabaseService()
        dbService.deleteNotice(notice)
        app.noticeDeleted(notice)
    }

    fun sendNotice() {
        sendEmail()
        disableFormFields()
        notice.sentTime = ZonedDateTime.now()
        saveNotice()
    }

    fun disableFormFields() {
        log.debug("disabling form fields is not yet implemented")
        // todo Prio 2 implementieren Eingabefelder deaktivieren
        noticeForm.disableFormFields()
    }

    fun sendEmail() {
        log.debug("sending email is not yet implemented")
        // todo Prio 2 E-Mail versenden
    }
}