package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.gui.Styles
import de.heikozelt.wegefrei.jobs.AddressWorker
import de.heikozelt.wegefrei.json.Witness
import de.heikozelt.wegefrei.maps.MaxiMapForm
import de.heikozelt.wegefrei.model.*
import de.heikozelt.wegefrei.mua.EmailAddressWithName
import de.heikozelt.wegefrei.mua.EmailAttachment
import de.heikozelt.wegefrei.mua.EmailMessage
import org.jxmapviewer.viewer.GeoPosition
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
    private val dbRepo: DatabaseRepo
) : JFrame(), ListDataListener /*, SelectedPhotosObserver */ {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    // Daten-Modell:
    private var noticeEntity: NoticeEntity? = null // wozu brauche ich dieses Feld, wenn getNotice() delegiert

    //private var selectedPhotos: SelectedPhotos = SelectedPhotos() // Achtung: Redundanz
    private var offensePosition: GeoPosition? = null // Achtung: Redundanz

    /**
     * last offense position, for which an address was searched for
     */
    private var searchedAddressForPosition: GeoPosition? = null
    private var cache = LeastRecentlyUsedCache<Path, Photo>(128)
    private val photoLoader = PhotoLoader(dbRepo)

    // GUI-Komponenten:
    private var selectedPhotosListModel = SelectedPhotosListModel(photoLoader)
    private var browserPanel = BrowserPanel(this, dbRepo, cache, photoLoader, selectedPhotosListModel)
    private val selectedPhotosListCellRenderer = SelectedPhotosListCellRenderer()
    private var selectedPhotosList = JList(selectedPhotosListModel)
    private var selectedPhotosScrollPane = JScrollPane(selectedPhotosList)
    private var noticeForm = NoticeForm(this, selectedPhotosListModel)

    // Split-Panes:
    private var topSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, browserPanel, selectedPhotosScrollPane)
    private var bottomSplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, noticeForm, JPanel())
    private var mainSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, bottomSplitPane)

    init {
        log.debug("init")
        title = "Meldung - Wege frei!"
        //background = FRAME_BACKGROUND
        selectedPhotosScrollPane.minimumSize = Dimension(Styles.THUMBNAIL_SIZE / 2, Styles.THUMBNAIL_SIZE / 2)
        selectedPhotosList.cellRenderer = selectedPhotosListCellRenderer
        selectedPhotosList.fixedCellWidth = Styles.THUMBNAIL_SIZE
        selectedPhotosList.fixedCellHeight = Styles.THUMBNAIL_SIZE
        selectedPhotosList.visibleRowCount = 1
        selectedPhotosList.layoutOrientation = JList.HORIZONTAL_WRAP
        selectedPhotosList.addListSelectionListener {
            selectedPhotosList.selectedValue?.let { photo ->
                this.showSelectedPhoto(photo)
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
                    if (it is NoticeFrame) it.saveAndClose()
                }
            }
        })

        this.extendedState = MAXIMIZED_BOTH
        //setSize(1000, 700)

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
                var photo = cache[path]
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

        app.getSettings()?.let {
            browserPanel.loadData(it.getPhotosPath())
            noticeForm.setNotice(noticeEntity)
        }
        noticeEntity.id?.let {
            browserPanel.setNoticeId(it)
            selectedPhotosListCellRenderer.setNoticeId(it)
        }

        offensePosition = noticeEntity.getGeoPosition()
    }

    /**
     * delegiert nur
     */
    fun getNotice(): NoticeEntity {
        return noticeForm.getNotice()
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
        log.debug("show maxi map")
        // todo Prio 4: do nothing if it is already shown
        val maxiMapForm = MaxiMapForm(this, selectedPhotosListModel)
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
        log.debug("show photo")

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
        log.debug("show selected photo")

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
        log.debug("show selected photo")

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

    /**
     * Die Methode wird vom OK-Button aufgerufen.
     * Speichert die Meldung in der Datenbank.
     */
    fun saveNotice() {
        log.debug("saveNotice()")
        // todo Prio 1: bug: Fotos, die noch nicht in der Datenbank sind, werden nicht gespeichert.
        noticeEntity = noticeForm.getNoticeFormFields().getNotice()
        val dbRepo = app.getDatabaseRepo() ?: return
        noticeEntity?.let {
            it.setGeoPosition(offensePosition)
            it.photoEntities = selectedPhotosListModel.getPhotoEntities()
            log.debug("noticeEntity.id = ${it.id}")
            if (it.id == null) {
                dbRepo.insertNotice(it)
                log.debug("added")
                app.noticeAdded(it)
            } else {
                dbRepo.updateNotice(it)
                log.debug("updated")
                app.noticeUpdated(it)
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
     * Die Methode wird vom E-Mail-absenden-Button aufgerufen.
     * todo Prio 3: asynchroner E-Mail-Versand. Vierten Status einführen, Meldung ist im Postausgang, aber noch nicht gesendet.
     * todo weitere Validierung, nicht nur prüfen, ob Empfänger-E-Mail-Adresse angegeben ist.
     * todo Detaillierte Rückmeldung über den Grund, warum die Validierung fehlgeschlagen ist
     * todo Bei Validierung: Differenzierung zwischen Warnung und Fehler
     */
    fun sendNotice() {
        noticeEntity = getNotice()
        noticeEntity?.let {
            if (it.recipient == null) {
                JOptionPane.showMessageDialog(
                    null,
                    "Kein Empfänger angegeben.",
                    "Validierungsfehler",
                    JOptionPane.INFORMATION_MESSAGE
                )
            } else {
                sendEmail()
                // todo Prio 1: nur wenn E-Mail tatsächlich erfolgreich gesendet wurde
                it.sentTime = ZonedDateTime.now()
                disableFormFields()
                saveNotice()
            }
        }
    }

    private fun disableFormFields() {
        // todo Prio 2 ALLE Eingabefelder deaktivieren (auch Foto-Panels)
        noticeForm.enableOrDisableEditing()
        getMaxiMapForm()?.enableOrDisableEditing()
    }

    /**
     */
    private fun sendEmail() {
        log.debug("sendEmail()")

        app.getSettings()?.let { setti ->
            noticeEntity?.let { n ->
                n.recipient?.let { reci ->
                    val from = EmailAddressWithName(setti.witness.emailAddress, setti.witness.getFullName())
                    // todo Prio 3: mehrere Empfänger erlauben
                    val to = EmailAddressWithName(reci)
                    val tos = TreeSet<EmailAddressWithName>()
                    tos.add(to)
                    var subject = "Anzeige"
                    n.licensePlate?.let { lic ->
                        subject += " $lic"
                    }
                    val content = buildMailContent(n, setti.witness)
                    val message = EmailMessage(from, tos, subject, content)
                    if (from.address != to.address) message.ccs.add(from)

                    selectedPhotosListModel.getSelectedPhotos().forEach {
                        val attachment = EmailAttachment(it.getPath())
                        message.attachments.add(attachment)
                    }

                    // todo Prio 3: Nicht jedes Mal einen neuen User Agent instanziieren

                    val agent = app.getEmailUserAgent()
                    //agent.setEmailServerConfig(setti.emailServerConfig)
                    agent?.sendMailAfterConfirmation(message)
                }
            }
        }
    }

    fun getZoomComponent(): Component {
        return bottomSplitPane.rightComponent
    }

    private fun setZoomComponent(comp: Component) {
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

    fun cancelAndClose() {
        isVisible = false
        dispose()
        app.noticeFrameClosed(this)
    }

    fun saveAndClose() {
        saveNotice()
        isVisible = false
        dispose()
        app.noticeFrameClosed(this)
    }

    fun deleteAndClose() {
        deleteNotice()
        isVisible = false
        dispose()
        app.noticeFrameClosed(this)
    }

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

        fun buildMailContent(n: NoticeEntity, w: Witness): String {
            fun htmlEncode(str: String?): String {
                str?.let {
                    return it
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                }
                return ""
            }

            fun tableRow(label: String, value: String?): String {
                return if (value.isNullOrBlank()) {
                    ""
                } else {
                    "|    <tr><td>$label:</td><td>${htmlEncode(value)}</td></tr>\n"
                }
            }

            fun tableRowHtmlValue(label: String, value: String?): String {
                return if (value.isNullOrBlank()) {
                    ""
                } else {
                    "|    <tr><td>$label:</td><td>$value</td></tr>\n"
                }
            }

            val countryRow = tableRow("Landeskennzeichen", n.getCountryFormatted())
            val licensePlateRow = tableRow("Kennzeichen", n.licensePlate)
            val makeRow = tableRow("Marke", n.vehicleMake)
            val colorRow = tableRow("Farbe", n.color)
            val offenseAddressRow = tableRow("Tatortadresse", n.getAddress())
            val locationDescriptionRow = tableRow("Tatortbeschreibung", n.locationDescription)
            val positionRow = tableRow("Geoposition", n.getGeoPositionFormatted())
            val offenseRow = tableRow("Verstoß", n.offense)
            val circumstancesRow = tableRowHtmlValue("Umstände", n.getCircumstancesHtml())
            val inspectionDateRow = tableRow("HU-Fälligkeit", n.getInspectionMonthYear())
            // todo: Wochentag einfügen, wegen Werktags-Beschränkungen
            val observationTimeRow = tableRow("Beobachtungszeit", n.getObservationTimeFormatted())
            val observationDurationRow = tableRow("Beobachtungsdauer", n.getDurationFormatted())
            val noteRow = tableRow("Hinweis", n.note)

            val nameRow = tableRow("Name", w.getFullName())
            val witnessAddressRow = tableRow("Adresse", w.getAddress())
            val witnessEmailRow = tableRow("E-Mail", w.emailAddress)
            val telephoneRow = tableRow("Telefon", w.telephoneNumber)

            val attachmentsSection = if (n.photoEntities.isEmpty()) {
                ""
            } else {
                val sb = StringBuilder()
                sb.append("|  <h1>Anlagen</h1>\n")
                sb.append("|  <ol>\n")
                n.photoEntities.forEach { sb.append("|    <li>${it.getFilename()} (SHA1: ${it.getHashHex()})</li>\n") }
                sb.append("|  </ol>\n")
            }

            val content = """
              |<html>
              |  <p>Sehr geehrte Damen und Herren,</p>
              |  <p>hiermit zeige ich, mit der Bitte um Weiterverfolgung, folgende Verkehrsordnungswidrigkeit an:</p>
              |  <h1>Falldaten</h1>
              |  <table>
              $countryRow$licensePlateRow$makeRow$colorRow$offenseAddressRow$locationDescriptionRow$positionRow$offenseRow$circumstancesRow$inspectionDateRow$observationTimeRow$observationDurationRow$noteRow
              |  </table>  
              |  <h1>Zeuge</h1>
              |  <table>
              $nameRow$witnessAddressRow$witnessEmailRow$telephoneRow  
              |  </table>
              $attachmentsSection
              |  <h1>Erklärung</h1>
              |  <p>Hiermit bestätige ich, dass ich die Datenschutzerklärung zur Kenntnis genommen habe und ihr zustimme.
              |    Meine oben gemachten Angaben einschließlich meiner Personalien sind zutreffend und vollständig.
              |    Als Zeuge bin ich zur wahrheitsgemäßen Aussage und auch zu einem möglichen Erscheinen vor Gericht verpflichtet.
              |    Vorsätzlich falsche Angaben zu angeblichen Ordnungswidrigkeiten können eine Straftat darstellen.
              |    Ich weiß, dass mir die Kosten des Verfahrens und die Auslagen des Betroffenen auferlegt werden,
              |    wenn ich vorsätzlich oder leichtfertig eine unwahre Anzeige erstatte.</p>
              |  <p>Beweisfotos, aus denen Kennzeichen und Tatvorwurf erkennbar hervorgehen, befinden sich im Anhang.
              |    Bitte prüfen Sie den Sachverhalt auch auf etwaige andere Verstöße, die aus den Beweisfotos zu ersehen sind.</p>
              |  <p>Bitte bestätigen Sie Ihre Zuständigkeit und den Erhalt dieser Anzeige mit der Zusendung des Aktenzeichens an hz@heikozelt.de.
              |    Falls Sie nicht zuständig sein sollten, leiten Sie bitte meine Anzeige weiter und informieren Sie mich darüber.
              |    Sie dürfen meine persönlichen Daten auch weiterleiten und diese für die Dauer des Verfahrens speichern.</p>                                  
              |  <p>Mit freundlichen Grüßen</p>
              |  <p>${w.getFullName()}</p>
              |</html>""".trimMargin()
            LOG.debug("html content:\n$content")
            return content
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