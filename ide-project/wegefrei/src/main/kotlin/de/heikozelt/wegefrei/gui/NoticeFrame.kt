package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.DatabaseService
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.FRAME_BACKGROUND
import de.heikozelt.wegefrei.jobs.AddressWorker
import de.heikozelt.wegefrei.model.SelectedPhotos
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
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
class NoticeFrame(private val app: WegeFrei) : JFrame() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var notice: Notice? = null
    private var selectedPhotos: SelectedPhotos = SelectedPhotos()
    private var allPhotosPanel = AllPhotosPanel(this)
    private var selectedPhotosPanel = SelectedPhotosPanel(this)
    private var selectedPhotosScrollPane = JScrollPane(selectedPhotosPanel)
    private var noticeForm = NoticeForm(this)

    private var topSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, allPhotosPanel, selectedPhotosScrollPane)
    private var bottomSplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, noticeForm, JPanel())
    private var mainSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, bottomSplitPane)

    init {
        log.debug("init")
        title = "Meldung - Wege frei!"
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
        log.debug("init finished")
    }

    /**
     * SelectedPhotosObservers werden frühzeitig registriert.
     * Fotos werden direkt danach ersetzt.
     * Zuletzt werden sonstige Daten geladen.
     */
    fun loadData(notice: Notice) {
        log.debug("loadData(notice id: ${notice.id})")
        selectedPhotos.registerObserver(selectedPhotosPanel)
        selectedPhotos.registerObserver(allPhotosPanel)
        selectedPhotos.registerObserver(noticeForm.getNoticeFormFields())
        selectedPhotos.registerObserver(noticeForm.getNoticeFormFields().getMiniMap())
        selectedPhotos.setPhotos(TreeSet(notice.photos))

        title = if(notice.id == null) {
            "Neue Meldung - Wege frei!"
        } else {
            "Meldung #${notice.id} - Wege frei!"
        }
        this.notice = notice
        allPhotosPanel.loadData("20220301_184952.jpg")
        noticeForm.loadData(notice)
    }

    fun getDatabaseService(): DatabaseService {
        return app.getDatabaseService()
    }

    fun getSelectedPhotos(): SelectedPhotos {
        return selectedPhotos
    }

    fun setSelectedPhotos(selectedPhotos: SelectedPhotos) {
        this.selectedPhotos = selectedPhotos
    }

    fun getNotice(): Notice? {
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
        selectedPhotosPanel.showBorder(photo)
        bottomSplitPane.rightComponent = MaxiSelectedPhotoPanel(this, photo)
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
        //todo scrollpane und photo zoomPanel.showPhoto(photo)
        selectedPhotosPanel.hideBorder()
        bottomSplitPane.rightComponent = MaxiPhotoPanel(this, photo)
    }

    /**
     * zeigt im Zoom-Bereich eine große Landkarte an
     */
    fun showMaxiMap() {
        log.debug("show maxi map")
        val maxiMapForm = MaxiMapForm(this)
        bottomSplitPane.rightComponent = maxiMapForm
        notice?.let {
            maxiMapForm.setAddressMarker(it.getGeoPosition())
            maxiMapForm.setPhotoMarkers(selectedPhotos)
        }

        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(true)
        allPhotosPanel.hideBorder()
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich ein großes Foto an
     */
    fun showPhoto(miniPhotoPanel: MiniPhotoPanel) {
        log.debug("show photo")
        val photoPanel = MaxiPhotoPanel(this, miniPhotoPanel.getPhoto())
        val scrollPane = JScrollPane(photoPanel)
        bottomSplitPane.rightComponent = scrollPane

        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        allPhotosPanel.showBorder(miniPhotoPanel)
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich ein großes bereits ausgewähltes Foto an
     */
    fun showSelectedPhoto(miniSelectedPhotoPanel: MiniSelectedPhotoPanel) {
        log.debug("show selected photo")

        //todo scollpane und photo zoomPanel.showSelectedPhoto(miniSelectedPhotoPanel.getPhoto())
        val photoPanel = MaxiSelectedPhotoPanel(this, miniSelectedPhotoPanel.getPhoto())
        val scrollPane = JScrollPane(photoPanel)
        bottomSplitPane.rightComponent = scrollPane

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
        notice?.let {
            if (it.id == null) {
                dbService.insertNotice(it)
                app.noticeAdded(it)
            } else {
                dbService.updateNotice(it)
                app.noticeUpdated(it)
            }
        }
    }

    fun deleteNotice() {
        val dbService = app.getDatabaseService()
        notice?.let {
            dbService.deleteNotice(it)
            app.noticeDeleted(it)
        }
    }

    fun sendNotice() {
        notice?.let {
            sendEmail()
            disableFormFields()
            it.sentTime = ZonedDateTime.now()
            saveNotice()
        }
    }

    private fun disableFormFields() {
        log.debug("disabling form fields is not yet implemented")
        // todo Prio 2 implementieren Eingabefelder deaktivieren
        noticeForm.disableFormFields()
    }

    private fun sendEmail() {
        log.debug("sending email is not yet implemented")
        // todo Prio 2 E-Mail versenden
    }
}