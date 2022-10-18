package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.DatabaseService
import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.FRAME_BACKGROUND
import de.heikozelt.wegefrei.jobs.AddressWorker
import de.heikozelt.wegefrei.model.SelectedPhotos
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.Component
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
class NoticeFrame(private val app: WegeFrei) : JFrame(), SelectedPhotosObserver {

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
        selectedPhotos.registerObserver(this)
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
        log.debug("selectPhoto(photo=${photo.filename}")
        selectedPhotos.add(photo)
        // alle weiteren Aktionen via Observers
    }

    /**
     * entfernt ein Foto aus der Auswahl für die Meldung
     */
    fun unselectPhoto(photo: Photo) {
        log.debug("unselectPhoto(photo=${photo.filename}")
        selectedPhotos.remove(photo)
        // alle weiteren Aktionen via Observers
    }

    /**
     * zeigt im Zoom-Bereich eine große Landkarte an
     */
    fun showMaxiMap() {
        log.debug("show maxi map")
        val maxiMapForm = MaxiMapForm(this)
        val maxiMap = maxiMapForm.getMaxiMap()
        bottomSplitPane.rightComponent = maxiMapForm
        notice?.let {
            maxiMapForm.setAddressMarker(it.getGeoPosition())
            maxiMapForm.setPhotoMarkers(selectedPhotos)
        }
        maxiMap.replacedPhotoSelection(selectedPhotos.getPhotos())

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
     * zeigt im Zoom-Bereich ein großes Foto an
     */
    fun showPhoto(photo: Photo) {
        log.debug("show photo")
        val photoPanel = MaxiPhotoPanel(this, photo)
        val scrollPane = JScrollPane(photoPanel)
        bottomSplitPane.rightComponent = scrollPane

        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        allPhotosPanel.showBorder(photo)
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

    /**
     * zeigt im Zoom-Bereich ein großes bereits ausgewähltes Foto an
     */
    private fun showSelectedPhoto(photo: Photo) {
        log.debug("show selected photo")

        //todo scollpane und photo zoomPanel.showSelectedPhoto(miniSelectedPhotoPanel.getPhoto())
        val photoPanel = MaxiSelectedPhotoPanel(this, photo)
        val scrollPane = JScrollPane(photoPanel)
        bottomSplitPane.rightComponent = scrollPane

        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        allPhotosPanel.hideBorder()
        selectedPhotosPanel.showBorder(photo)
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

    /**
     * tausche nicht ausgewähltes durch ausgewähltes Foto,
     * nur falls es sich um das gleiche Foto handelt.
     */
    override fun selectedPhoto(index: Int, photo: Photo) {
        log.debug("zick 1")
        getMaxiPhotoPanel()?.let {
            log.debug("zick 2")
            if (photo == it.getPhoto()) {
                log.debug("zick 3")
                showSelectedPhoto(photo)
            }
        }
    }

    /**
     * tausche ausgewähltes durch nicht ausgewähltes Foto,
     * nur falls es sich um das gleiche Foto handelt.
     */
    override fun unselectedPhoto(index: Int, photo: Photo) {
        log.debug("zuck 1")
        getMaxiSelectedPhotoPanel()?.let {
            log.debug("zuck 2")
            if (photo == it.getPhoto()) {
                log.debug("zuck 3")
                showPhoto(photo)
            }
        }
    }

    override fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        // todo was ist denn hier zu tun?
    }

    fun getZoomComponent(): Component {
        return bottomSplitPane.rightComponent
    }

    /**
     * liefert eine Referenz auf das MaxiMapForm,
     * falls dieses gerade angezeigt wird, sonst null
     */
    fun getMaxiMapPanel(): MaxiMapForm? {
        val zoomComp = getZoomComponent()
        return if(zoomComp is MaxiMapForm) {
            zoomComp
        } else {
            null
        }
    }

    /**
     * liefert eine Referenz auf das MaxiPhotoPanel,
     * falls dieses gerade angezeigt wird, sonst null
     */
    private fun getMaxiPhotoPanel(): MaxiPhotoPanel? {
        val zoomComp = getZoomComponent()
        log.debug("zack 1")
        if(zoomComp is JScrollPane) {
            log.debug("zack 2")
            val v = zoomComp.viewport.view
            if(v is MaxiPhotoPanel) {
                log.debug("zack 3")
                return v
            }
        }
        return null
    }

    /**
     * liefert eine Referenz auf das MaxiSelectedPhotoPanel,
     * falls dieses gerade angezeigt wird, sonst null
     */
    private fun getMaxiSelectedPhotoPanel(): MaxiSelectedPhotoPanel? {
        val zoomComp = getZoomComponent()
        log.debug("zock 1")
        if(zoomComp is JScrollPane) {
            log.debug("zock 2")
            val v = zoomComp.viewport.view
            if(v is MaxiSelectedPhotoPanel) {
                log.debug("zock 3")
                return v
            }
        }
        return null
    }
}