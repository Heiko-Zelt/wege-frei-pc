package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.email.useragent.EmailMessageDialog
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.gui.Styles
import de.heikozelt.wegefrei.jobs.AddressWorker
import de.heikozelt.wegefrei.maps.MaxiMapForm
import de.heikozelt.wegefrei.model.*
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.TileFactory
import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZonedDateTime
import java.util.*
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt


/**
 * Haupt-Fenster zum Bearbeiten einer Meldung
 * (mit "Dispatcher-Funktionen" / "Business-Logik")
 *
 * Ablauf:
 * <ol>
 *   <li>Konstruktor + init()-Methode
 *     <ol>
 *       <li>Anlegen der GUI-Elemente/Widgets</li>
 *       <li>alle deaktivieren</li>
 *       <li>zum Container, hier JFrame hinzufügen</li>
 *       <li>JFrame anzeigen</li>
 *     </ol>
 *   </li>
 *   <li>Daten laden
 *     <ol>
 *       <li>hier eine neue/leere Meldung oder</li>
 *       <li>bestehende/teils oder vollständig ausgefüllte Meldung</li>
 *     </ol>
 *   </li>
 *   <li>Aktivierung der Formularfelder (abhängig von den konkreten Daten)</li>
 * </ol>
 * todo Prio 2: Wenn Fenster geschlossen wird via Close-Button oder bei Änderung der Datenbank, fragen ob Daten gespeichert werden sollen (falls sie geändert wurden)
 * todo Prio 1: Nur wenn E-Mail absenden bestätigt wurde und die E-Mail tatsächlich erfolgreich gesendet wurde, als gesendet markieren.
 */
class NoticeFrame(
    private val app: WegeFrei,
    private val dbRepo: DatabaseRepo,
    private val tileFactory: TileFactory,
    private val photoCache: LeastRecentlyUsedCache<Path, Photo>,
    private val photoLoader: PhotoLoader
) : JFrame(), ListDataListener /*, SelectedPhotosObserver */ {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * Daten-Modell:
     * todo Problem: Eine Nachricht besteht aus einfachen Benutzereingaben und generierten Status/Protokoll-Daten
     * Lösung:
     * <ul>
     *   <li>NoticeFrame.noticeEntity enthält Status-Daten</li>
     *   <li>NoticeFrame.noticeForm enthält Eingabefelder</li>
     * </ul>
     */
    private var noticeEntity: NoticeEntity? = null

    //private var selectedPhotos: SelectedPhotos = SelectedPhotos() // Achtung: Redundanz
    private var offensePosition: GeoPosition? = null // Achtung: Redundanz

    /**
     * last offense position, for which an address was searched for
     */
    private var searchedAddressForPosition: GeoPosition? = null

    // GUI-Komponenten:
    private var selectedPhotosListModel = SelectedPhotosListModel(photoLoader)
    private var browserPanel = BrowserPanel(this, dbRepo, photoCache, photoLoader, selectedPhotosListModel)
    private val selectedPhotosListCellRenderer = SelectedPhotosListCellRenderer()
    private var selectedPhotosList = JList(selectedPhotosListModel)
    private var selectedPhotosScrollPane = JScrollPane(selectedPhotosList)
    private var noticeForm = NoticeForm(this, selectedPhotosListModel, dbRepo, tileFactory)

    // Split-Panes:
    private var topSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, browserPanel, selectedPhotosScrollPane)
    private var bottomSplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, noticeForm, JPanel())
    private var mainSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, bottomSplitPane)

    init {
        log.debug("init")
        title = "Meldung - Wege frei!"
        //background = FRAME_BACKGROUND

        selectedPhotosScrollPane.minimumSize = Dimension(Styles.THUMBNAIL_SIZE + 4, Styles.THUMBNAIL_SIZE + 4)
        selectedPhotosList.cellRenderer = selectedPhotosListCellRenderer
        selectedPhotosList.fixedCellWidth = Styles.THUMBNAIL_SIZE
        selectedPhotosList.fixedCellHeight = Styles.THUMBNAIL_SIZE
        selectedPhotosList.visibleRowCount = 1
        selectedPhotosList.layoutOrientation = JList.HORIZONTAL_WRAP
        selectedPhotosList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                selectedPhotosList.selectedValue?.let { photo ->
                    this.showSelectedPhoto(photo)
                }
            }
        }

        selectedPhotosListModel.addListDataListener(browserPanel.getBrowserListModel())
        selectedPhotosListModel.addListDataListener(this)
        selectedPhotosListModel.addListDataListener(noticeForm.getNoticeFormFields())
        selectedPhotosListModel.addListDataListener(noticeForm.getNoticeFormFields().getMiniMap())

        //defaultCloseOperation = DISPOSE_ON_CLOSE ist egal
        //addWindowListener(NoticeFrameWindowListener(this))
        //todo Prio 1: bug: SaveNotice: jakarta.persistence.EntityNotFoundException:
        // Unable to find de.heikozelt.wegefrei.entities.PhotoEntity with id /home/heiko/Pictures/IMG_5148.jpg
        // todo Prio 2: ask to save or cancel before closing
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                e?.window?.let {
                    if (it is NoticeFrame) it.saveButtonClicked()
                }
            }
        })

        this.extendedState = MAXIMIZED_BOTH
        //setSize(1000, 700)

        topSplitPane.apply {
            isOneTouchExpandable = true
            setDividerLocation(0.5)
            //resizeWeight = 1.0
        }

        bottomSplitPane.apply {
            isOneTouchExpandable = true
            setDividerLocation(0.5)
            //resizeWeight = 1.0
        }

        mainSplitPane.apply {
            isOneTouchExpandable = true
            setDividerLocation(0.4)
            //resizeWeight = 1.0
        }
        add(mainSplitPane)

        minimumSize = Dimension(250, 250)
        isVisible = true
        log.debug("init finished")
    }

    /**
     * Initialisiert alle GUI-Komponenten mit dem Inhalt der Meldung.
     * Mapping von Daten-Modell/Notice auf Swing-Komponenten.
     * <ol>
     *   <li>ohne Parameter bzw. mit Default-Parameter zum Bearbeiten einer neuen Meldung. notice.id ist null.</li>
     *   <li>Aufruf mit Notice als Parameter zum Bearbeiten einer bestehenden Meldung. notice.id enthält eine Zahl.</li>
     * </ol>
     * SelectedPhotosObservers werden frühzeitig registriert.
     * Fotos werden direkt danach ersetzt.
     * Zuletzt werden sonstige Daten geladen.
     * @param noticeEntity
     */
    fun setNotice(noticeEntity: NoticeEntity = NoticeEntity.createdNow()) {
        log.debug("setNotice(id: ${noticeEntity.id})")
        this.noticeEntity = noticeEntity

        val photos = TreeSet<Photo>()
        noticeEntity.photoEntities.forEach { photoEntity ->
            photoEntity.path?.let { pathStr ->
                log.debug("photoEntity.path: $pathStr")
                val path = Paths.get(pathStr)
                //photo.setPhotoEntity(photoEntity)
                var photo = photoCache[path]
                if (photo == null) {
                    log.debug("not yet in cache")
                    photo = Photo(path)

                }
                photos.add(photo)
            }
        }
        selectedPhotosListModel.setSelectedPhotos(photos)
        browserPanel.setSelectedPhotos(selectedPhotosListModel)
        photos.forEach {
            photoLoader.loadPhotoFile(it)
            photoLoader.loadPhotoEntity(it)
        }

        title = if (noticeEntity.id == null) {
            "Neue Meldung - Wege frei!"
        } else {
            "Meldung #${noticeEntity.id} - Wege frei!"
        }

        app.getSettings()?.let { s ->
            browserPanel.setPhotosDirectory(s.getPhotosPath())
            noticeForm.setNotice(noticeEntity)
        }
        noticeEntity.id?.let {
            browserPanel.setNoticeId(it)
            selectedPhotosListCellRenderer.setNoticeId(it)
        }

        offensePosition = noticeEntity.getGeoPosition()
    }



    fun getNotice(): NoticeEntity? {
       return noticeEntity
    }

    /**
     * fügt ein Foto zur Auswahl für diese Meldung hinzu
     */
    fun selectPhoto(photo: Photo) {
        log.debug("selectPhoto(photo=${photo.getPath()}")
        selectedPhotosListModel.add(photo)
        selectedPhotosList.setSelectedValue(photo, true)
        // alle weiteren Aktionen via Observers
        // todo Beobachtungszeit updaten
    }

    /**
     * entfernt ein Foto aus der Auswahl für diese Meldung
     */
    fun unselectPhoto(photo: Photo) {
        log.debug("unselectPhoto(photo=${photo.getPath()}")
        selectedPhotosListModel.remove(photo)
        //browserPanel.getBrowserList().setSelectedValue(photo, true)
        browserPanel.setSelectedValue(photo)
        // alle weiteren Aktionen via Observers
    }

    /**
     * zeigt im Zoom-Bereich eine große Landkarte an
     */
    fun showMaxiMap() {
        log.debug("showMaxiMap()")
        // todo Prio 4: do nothing if it is already shown
        val maxiMapForm = MaxiMapForm(this, selectedPhotosListModel, tileFactory)
        selectedPhotosListModel.addListDataListener(maxiMapForm.getMaxiMap())
        setZoomComponent(maxiMapForm)
        maxiMapForm.setOffenseMarker(offensePosition)
        maxiMapForm.setPhotoMarkers(selectedPhotosListModel.getSelectedPhotos())
        noticeEntity?.let {
            if (it.isSent()) {
                maxiMapForm.enableOrDisableEditing()
            }
        }
        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(true)
        browserPanel.hideBorder()
        selectedPhotosList.clearSelection()
    }

    /**
     * zeigt im Zoom-Bereich ein großes Foto an
     */
    fun showPhoto(photo: Photo) {
        log.debug("showPhoto(photo.path=${photo.getPath()})")

        val photoPanel = MaxiPhotoPanel(this, photo)
        setZoomComponent(photoPanel)
        EventQueue.invokeLater { photoPanel.fit() }

        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        selectedPhotosList.clearSelection()
    }

    /**
     * zeigt im Zoom-Bereich ein großes bereits ausgewähltes Foto an
     */
    fun showSelectedPhoto(miniSelectedPhotoPanel: MiniSelectedPhotoPanel) {
        log.debug("showSelectedPhoto(miniSelectedPhotoPanel)")

        //todo Prio 3: 2 Methoden für den gleichen Zweck, eine soll die andere Aufrufen

        val photoPanel = MaxiSelectedPhotoPanel(this, miniSelectedPhotoPanel.getPhoto())
        setZoomComponent(photoPanel)
        EventQueue.invokeLater { photoPanel.fit() }

        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        browserPanel.hideBorder()
        //selectedPhotosPanel.showBorder(miniSelectedPhotoPanel)
    }

    /**
     * zeigt im Zoom-Bereich ein großes bereits ausgewähltes Foto an
     */
    private fun showSelectedPhoto(photo: Photo) {
        log.debug("showSelectedPhoto(photo)")

        val photoPanel = MaxiSelectedPhotoPanel(this, photo)
        setZoomComponent(photoPanel)
        EventQueue.invokeLater { photoPanel.fit() }

        noticeForm.getNoticeFormFields().getMiniMap().displayBorder(false)
        browserPanel.hideBorder()
        //selectedPhotosPanel.showBorder(photo)
    }

    /**
     * aus Performance-Gründen:
     * Bei nur minimalen Abweichungen keine neu Adresse suchen.
     */
    private fun maybeFindAddress() {
        if (distanceStraight(searchedAddressForPosition, offensePosition) > NEARBY_METERS) {
            findAddress()
        }
    }

    private fun findAddress() {
        log.info("findAddress()")
        offensePosition?.let {
            searchedAddressForPosition = offensePosition
            val worker = AddressWorker(it, noticeForm)
            worker.execute()
        }
    }

    private fun disableFormFields() {
        // todo Prio 2 ALLE Eingabefelder deaktivieren (auch Foto-Panels)
        noticeForm.enableOrDisableEditing(false)
        getMaxiMapForm()?.enableOrDisableEditing()
    }

    fun getZoomComponent(): Component {
        return bottomSplitPane.rightComponent
    }

    private fun setZoomComponent(comp: Component) {
        log.debug("setZoomComponent(comp=${comp::class})")
        // ggf. Observer entfernen, wichtig zur Vermeidung eines Memory-Leaks
        val oldComp = getZoomComponent()
        if (oldComp is MaxiMapForm) {
            selectedPhotosListModel.removeListDataListener(oldComp.getMaxiMap())
        }
        bottomSplitPane.rightComponent = comp
    }

    /**
     * liefert eine Referenz auf das MaxiMapForm,
     * falls dieses gerade angezeigt wird, sonst null
     */
    private fun getMaxiMapForm(): MaxiMapForm? {
        val zoomComp = getZoomComponent()
        return if (zoomComp is MaxiMapForm) {
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
        if (zoomComp is MaxiPhotoPanel) {
            return zoomComp
        }
        return null
    }

    /**
     * liefert eine Referenz auf das MaxiSelectedPhotoPanel,
     * falls dieses gerade angezeigt wird, sonst null
     */
    private fun getMaxiSelectedPhotoPanel(): MaxiSelectedPhotoPanel? {
        val zoomComp = getZoomComponent()
        if (zoomComp is MaxiSelectedPhotoPanel) {
            return zoomComp
        }
        return null
    }

    /**
     * wird z.B. aufgerufen, wenn ein Foto mit
     * Geo-Koordinaten ausgewählt oder entfernt wird.
     * Berechnet automatisch die neue Marker-Position
     * und benachrichtigt die MiniMap und ggf. die MaxiMap.
     */
    fun updateOffensePositionFromSelectedPhotos() {
        offensePosition = selectedPhotosListModel.getAveragePosition()
        noticeForm.getNoticeFormFields().getMiniMap().setOffensePosition(offensePosition)
        getMaxiMapForm()?.setOffenseMarker(offensePosition)
        maybeFindAddress()
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

    /**
     * because user moved the offense marker
     */
    fun setOffensePosition(newPosition: GeoPosition) {
        offensePosition = newPosition
        noticeForm.getNoticeFormFields().getMiniMap().setOffensePosition(offensePosition)
        maybeFindAddress()
    }


/////////////////////////////////////////////

    /**
     * Anwenderin hat auf den Abbrechen-Button geklickt.
     * <ol>
     *   <li>Fenster schließen</li>
     *   <li>andere Fenster/GUI-Komponenten benachrichtigen</li>
     * </ol>
     */
    fun cancelButtonClicked() {
        closeWindow()
    }

    /**
     * Anwenderin hat auf den Löschen-Button geklickt.
     * <ol>
     *   <li>Meldung löschen</li>
     *   <li>Fenster schließen</li>
     *   <li>andere Fenster/GUI-Komponenten benachrichtigen</li>
     * </ol>
     */
    fun deleteButtonClicked() {
        deleteNotice()
        closeWindow()
    }

    /**
     * Anwenderin hat auf den Ok/Speichern-Button geklickt.
     * <ol>
     *   <li>Formular-Eingaben auf Meldung abbilden und validieren (NoticeFormFields.validateAndMap(ne))</li>
     *   <li>ggf. abbrechen</li>
     *   <li>Meldung speichern</li>
     *   <li>Fenster schließen</li>
     *   <li>andere Fenster/GUI-Komponenten benachrichtigen</li>
     * </ol>
     */
    fun saveButtonClicked() {
        val errors = validateAndMap()
        if (errors.isNotEmpty()) {
            showValidationErrors(errors)
            return
        }
        saveNotice()
        updatePhotosDir()
        closeWindow()
    }

    /**
     * Anwenderin hat auf den E-Mail-absenden-Button geklickt.
     * <ol>
     *   <li>Formular-Eingaben auf Meldung abbilden und validieren.
     *     NoticeFrame.validateAndMap() -> NoticeFormFields.validateAndMap(ne)</li>
     *   <li>Vollständigkeit/Pflichtfelder prüfen.
     *     NoticeEntity.isComplete()</li>
     *   <li>ggf. abbrechen</li>
     *   <li>E-Mail-Nachricht generieren</li>
     *   <li>E-Mail-Nachricht anzeigen</li>
     *   <li>Wenn Benutzerin bestätigt</li>
     *   <li>Meldung finalisieren</li>
     *   <li>Meldung speichern</li>
     *   <li>Fenster schließen</li>
     *   <li>Sende-Thread starten, falls er nicht läuft</li>
     *   <li>andere Fenster/GUI-Komponenten benachrichtigen</li>
     * </ol>
     * todo Prio 3: asynchroner E-Mail-Versand. Vierten Status einführen, Meldung ist im Postausgang, aber noch nicht gesendet.
     * todo weitere Validierung, nicht nur prüfen, ob Empfänger-E-Mail-Adresse angegeben ist.
     * todo Detaillierte Rückmeldung über den Grund, warum die Validierung fehlgeschlagen ist
     * todo Bei Validierung: Differenzierung zwischen Warnung und Fehler
     */
    fun sendButtonClicked() {
        var errors = validateAndMap()
        if (errors.isNotEmpty()) {
            showValidationErrors(errors)
            return
        }
        noticeEntity?.let { ne ->
            errors = ne.isComplete()
            if (errors.isNotEmpty()) {
                showValidationErrors(errors)
                return
            }
            sendEmail()
        }
    }



    /////////////////////////////////////////////

    fun validateAndMap(): List<String> {
        log.debug("validateAndMap()")
        var errors = listOf<String>()
        if (noticeEntity == null) {
            noticeEntity = NoticeEntity.createdNow()
        }
        noticeEntity?.let { ne ->
            errors = noticeForm.getNoticeFormFields().validateAndMap(ne)
            ne.setGeoPosition(offensePosition)
            ne.photoEntities = selectedPhotosListModel.getPhotoEntities()
            // remember which notices belong to the photos in cache
            ne.photoEntities.forEach { pe ->
                pe.noticeEntities.add(ne)
            }
        }
        return errors
    }

    fun showValidationErrors(errors: List<String>) {
        val message = errors.joinToString("<br>", "<html>", "</html>")
        JOptionPane.showMessageDialog(
            null,
            message,
            "Validierungsfehler",
            JOptionPane.INFORMATION_MESSAGE
        )
    }

    /**
     * Sammelt die Daten einer Meldung ein und
     * speichert die Meldung in der Datenbank.
     */
    fun saveNotice() {
        val dbRepo = app.getDatabaseRepo() ?: return
        noticeEntity?.let { ne ->
            log.debug("noticeEntity.id = ${ne.id}")
            if (ne.id == null) {
                dbRepo.insertNotice(ne)
                log.debug("added")
                app.noticeAdded(ne)
            } else {
                dbRepo.updateNotice(ne)
                log.debug("updated")
                app.noticeUpdated(ne)
            }
        }
    }

    /**
     * Die Methode wird vom Löschen-Button aufgerufen.
     * Löscht die Meldung aus der Datenbank.
     */
    fun deleteNotice() {
        val dbRepo = app.getDatabaseRepo() ?: return
        noticeEntity?.let { entity ->
            entity.id?.let { id ->
                dbRepo.deleteNotice(id)
            }
            app.noticeDeleted(entity)
        }
    }

    /**
     * Wird aufgerufen, nachdem Formulareingaben auf Vollständigkeit geprüft wurden.
     * Baut aus der Meldung eine E-Mail-Nachricht
     * und zeigt den Bestätigungs-Dialog an.
     */
    private fun sendEmail() {
        log.debug("sendEmail()")
        noticeEntity?.let { ne ->
            val outbox = app.getNoticesOutbox()
            val message = outbox.buildEmailMessage(ne)
            message?.let { me ->
                log.debug("show email message dialog")
                val dialog = EmailMessageDialog { sendEmailConfirmed() }
                dialog.setEmailMessage(me)
            }
        }
    }

    /**
     * wird aufgerufen, wenn die Anwenderin das Senden einer E-Mail-Nachricht bestätigt hat.
     */
    fun sendEmailConfirmed() {
        log.debug("sendEmailConfirmed()")
        // todo in Notices-Tabelle eintragen
        noticeEntity?.let { ne ->
            ne.finalizedTime = ZonedDateTime.now()
            saveNotice()
            updatePhotosDir()
            closeWindow()
            app.startSendingEmails()
        }
    }

    fun updatePhotosDir() {
        app.getSettings()?.let { s ->
            val dir = browserPanel.getPhotosDirectory().toString()
            log.debug("browserPanel.dir = $dir")
            s.photosDirectory = dir
            app.getSettingsRepo().save(s)
        }
    }

    fun closeWindow() {
        isVisible = false
        dispose()
        app.noticeFrameClosed(this)
    }

///////////////////////////////////////////////////


    /**
     * tausche nicht ausgewähltes durch ausgewähltes Foto,
     * nur falls es sich um das gleiche Foto handelt.
     */
    private fun selectedPhoto(photo: Photo) {
        getMaxiPhotoPanel()?.let {
            if (photo == it.getPhoto()) {
                showSelectedPhoto(photo)
            }
        }
        photo.getGeoPosition()?.run {
            updateOffensePositionFromSelectedPhotos()
        }
    }

    /**
     * tausche ausgewähltes durch nicht ausgewähltes Foto,
     * nur falls es sich um das gleiche Foto handelt.
     */
    private fun unselectedPhoto(photo: Photo) {
        getMaxiSelectedPhotoPanel()?.let {
            if (photo == it.getPhoto()) {
                showPhoto(photo)
            }
        }
        photo.getGeoPosition()?.run {
            updateOffensePositionFromSelectedPhotos()
        }
    }

    override fun intervalAdded(e: ListDataEvent?) {
        e?.let {
            if (e is SelectedPhotosListDataEvent) {
                selectedPhoto(e.photos.first())
            }
        }
    }

    override fun intervalRemoved(e: ListDataEvent?) {
        e?.let {
            if (e is SelectedPhotosListDataEvent) {
                unselectedPhoto(e.photos.first())
            }
        }
    }

    override fun contentsChanged(e: ListDataEvent?) {
        // ignore
    }

    fun deletePhoto(photo: Photo) {
        browserPanel.deletePhoto(photo)
        setZoomComponent(JPanel())
    }

    companion object {

        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        /**
         * Berechnet die Distanz zwischen 2 Punkten nach dem Satz vom Pythagoras
         * Die Distanz der Längengrade nimmt vom Equator zu den Poln ab.
         * Das wird bei der Berechnung leder nicht berücksichtigt.
         */
        private fun distancePythagoras(positionA: GeoPosition?, positionB: GeoPosition?): Double {
            if (positionA == null || positionB == null) {
                return Double.POSITIVE_INFINITY
            }
            val a = abs(positionA.longitude - positionB.longitude)
            val b = abs(positionA.latitude - positionA.latitude)
            val c = sqrt(a * a + b * b)
            //val num1 = " %.7f".format(NEARBY_DEGREES)
            //val num2 = " %.7f".format(c)
            //log.debug("Schwellwert: $num1, distance = $num2")
            return c
        }

        /**
         * Distance of a straight line in meters (not moving on the curved surface of the earth).
         * The calculation is very accurate for small distances.
         * https://www.mkompf.com/gps/distcalc.html
         */
        fun distanceStraight(positionA: GeoPosition?, positionB: GeoPosition?): Double {
            if (positionA == null || positionB == null) {
                return Double.POSITIVE_INFINITY
            }
            val lat1 = positionA.latitude
            val lat2 = positionB.latitude
            val lon1 = positionA.longitude
            val lon2 = positionB.longitude
            val latDegrees = (lat1 + lat2) / 2f // Gradmaß
            val latRadian = latDegrees * (PI / 180f) // Bogenmaß
            val dx = METERS_PER_DEGREE * cos(latRadian) * abs(lon1 - lon2)
            val dxText = "%.10f".format(dx)
            val dy = METERS_PER_DEGREE * abs(lat1 - lat2)
            val dyText = "%.10f".format(dy)
            val distance = sqrt(dx * dx + dy * dy)
            val distanceText = "%.10f".format(distance)
            LOG.debug("in meters: dx: $dxText, dy: $dyText, distance: $distanceText")
            return distance
        }

        private const val EARTH_CIRCUMFERENCE = 40_075_000.0 // at the equator in meters (not at the poles)
        private const val WHOLE_CIRCLE = 360.0 // degrees
        private const val METERS_PER_DEGREE = EARTH_CIRCUMFERENCE / WHOLE_CIRCLE

        /**
         * If the offense position marker is moved less than NEARBY_METERS
         * no new address (street, house number, zip code, town) is searched for.
         */
        private const val NEARBY_METERS = 5.0
    }

}