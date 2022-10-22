package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.DatabaseService
import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.FRAME_BACKGROUND
import de.heikozelt.wegefrei.jobs.AddressWorker
import de.heikozelt.wegefrei.maps.MaxiMapForm
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
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Haupt-Fenster zum Bearbeiten einer Meldung
 * (mit "Dispatcher-Funktionen" / "Business-Logik")
 * Instanziierung
 * <ol>
 *   <li>ohne Parameter zum Bearbeiten einer neuen Meldung. notice.id ist null.</li>
 *   <li>Instanziierung mit Notice als Parameter zum Bearbeiten einer bestehenden Meldung. notice.id enthält eine Zahl.</li>
 * </ol>
 */
class NoticeFrame(private val app: WegeFrei) : JFrame(), SelectedPhotosObserver {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    // Daten-Modell:
    private var notice: Notice? = null
    private var selectedPhotos: SelectedPhotos = SelectedPhotos()
    private var offensePosition: GeoPosition? = null

    // GUI-Komponenten:
    private var allPhotosPanel = AllPhotosPanel(this)
    private var selectedPhotosPanel = SelectedPhotosPanel(this)
    private var selectedPhotosScrollPane = JScrollPane(selectedPhotosPanel)
    private var noticeForm = NoticeForm(this)

    // Split-Panes:
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

        offensePosition = notice.getGeoPosition()

        /*
        Initial wird keine große Karte angezeigt
        notice.getGeoPosition()?.let {
           maxiMap.setAddressPosition(it)
        }
        */
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
        // todo: do nothing if it is already shown
        val maxiMapForm = MaxiMapForm(this)
        selectedPhotos.registerObserver(maxiMapForm.getMaxiMap())
        setZoomComponent(maxiMapForm)
        maxiMapForm.setOffenseMarker(offensePosition)
        maxiMapForm.setPhotoMarkers(selectedPhotos)

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
    private fun showPhoto(photo: Photo) {
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
        setZoomComponent(scrollPane)

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
        setZoomComponent(scrollPane)

        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        allPhotosPanel.hideBorder()
        selectedPhotosPanel.showBorder(photo)
    }


    private fun findAddress() {
        log.info("findAddress()")
        offensePosition?.let {
            val worker = AddressWorker(it, noticeForm)
            worker.execute()
        }
    }

    /**
     * Die Methode wird vom OK-Button aufgerufen.
     */
    fun saveNotice() {
        // todo addressPosition speichern
        noticeForm.getNoticeFormFields().saveNotice()
        val dbService = app.getDatabaseService()
        notice?.let {
            it.setGeoPosition(offensePosition)
            if (it.id == null) {
                dbService.insertNotice(it)
                app.noticeAdded(it)
            } else {
                dbService.updateNotice(it)
                app.noticeUpdated(it)
            }
        }
    }

    /**
     * Die Methode wird vom Löschen-Button aufgerufen.
     */
    fun deleteNotice() {
        val dbService = app.getDatabaseService()
        notice?.let {
            dbService.deleteNotice(it)
            app.noticeDeleted(it)
        }
    }

    /**
     * Die Methode wird vom E-Mail-absenden-Button aufgerufen.
     */
    fun sendNotice() {
        saveNotice()
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
        getMaxiPhotoPanel()?.let {
            if (photo == it.getPhoto()) {
                showSelectedPhoto(photo)
            }
        }
        photo.getGeoPosition()?.run {
            updateOffensePosition()
        }
    }

    /**
     * tausche ausgewähltes durch nicht ausgewähltes Foto,
     * nur falls es sich um das gleiche Foto handelt.
     */
    override fun unselectedPhoto(index: Int, photo: Photo) {
        getMaxiSelectedPhotoPanel()?.let {
            if (photo == it.getPhoto()) {
                showPhoto(photo)
            }
        }
        photo.getGeoPosition()?.run {
            updateOffensePosition()
        }
    }

    override fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        // todo was ist denn hier zu tun?
    }

    fun getZoomComponent(): Component {
        return bottomSplitPane.rightComponent
    }

    private fun setZoomComponent(comp: Component) {
        // ggf. Observer entfernen, wichtig zur Vermeidung eines Memory-Leaks
        val oldComp = getZoomComponent()
        if(oldComp is MaxiMapForm) {
            selectedPhotos.unregisterObserver(oldComp.getMaxiMap())
        }
        bottomSplitPane.rightComponent = comp
    }

    /**
     * liefert eine Referenz auf das MaxiMapForm,
     * falls dieses gerade angezeigt wird, sonst null
     */
    fun getMaxiMapForm(): MaxiMapForm? {
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
        if(zoomComp is JScrollPane) {
            val v = zoomComp.viewport.view
            if(v is MaxiPhotoPanel) {
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
        if(zoomComp is JScrollPane) {
            val v = zoomComp.viewport.view
            if(v is MaxiSelectedPhotoPanel) {
                return v
            }
        }
        return null
    }

    /**
     * wird z.B. aufgerufen, wenn ein Foto mit
     * Geo-Koordinaten ausgewählt oder entfernt wird.
     * Berechnet automatisch die neue Marker-Position
     * und benachrichtigt die MiniMap und ggf. die MaxiMap.
     */
    fun updateOffensePosition() {
        val oldPosition = offensePosition
        offensePosition = selectedPhotos.getAveragePosition()
        noticeForm.getNoticeFormFields().getMiniMap().setOffensePosition(offensePosition)
        getMaxiMapForm()?.setOffenseMarker(offensePosition)

        // aus Performance-Gründen:
        // bei nur minimalen Abweichungen keine neu Addresse suchen
        offensePosition?.let {
            if (oldPosition == null || distance(oldPosition, it) > NEARBY_DEGREES) {
                findAddress()
            }
        }
    }

    /**
     * Entfernt die Adress-Position und die beiden Adress-Marker.
     * Die Adressdaten (Straße, PLZ und Ort) bleiben im Formular erhalten, falls vorhanden.
     */
    fun deleteOffensePosition() {
        offensePosition = null
        noticeForm.getNoticeFormFields().getMiniMap().setOffensePosition(null)
        getMaxiMapForm()?.setOffenseMarker(null)
    }

    companion object {
        /**
         * Berechnet die Distanz zwischen 2 Punkten nach dem Satz vom Pythagoras
         * Die Erdkrümmung wird nicht berücksichtigt
         * todo: die Erdkrümmung berücksichtigen
         */
        private fun distance(positionA: GeoPosition, positionB: GeoPosition): Double {
            // a = betrag von ( A.longitudeA - B.longitude )
            // b = betrag von ( A.latitude - B.latitude )
            // c = wurzel aus ( a im quadrat + b im quadrat)
            val a = abs(positionA.longitude - positionB.longitude)
            val b = abs(positionA.latitude - positionA.latitude)
            val c = sqrt(a * a + b * b)
            //val num1 = " %.7f".format(NEARBY_DEGREES)
            //val num2 = " %.7f".format(c)
            //log.debug("Schwellwert: $num1, distance = $num2")
            return c
        }

        private const val EARTH_CIRCUMFERENCE = 40_075_000.0 // meters
        private const val WHOLE_CIRCLE = 360.0 // degrees
        private const val NEARBY_METERS = 6.0 // meters
        const val NEARBY_DEGREES = NEARBY_METERS * WHOLE_CIRCLE / EARTH_CIRCUMFERENCE
    }
}