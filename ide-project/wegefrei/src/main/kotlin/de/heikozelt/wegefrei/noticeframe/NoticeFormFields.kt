package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.email.combobox.RecipientComboBox
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.gui.*
import de.heikozelt.wegefrei.maps.MiniMap
import de.heikozelt.wegefrei.model.Photo
import de.heikozelt.wegefrei.model.SelectedPhotosListDataEvent
import de.heikozelt.wegefrei.model.SelectedPhotosListModel
import de.heikozelt.wegefrei.model.VehicleColor
import org.jxmapviewer.viewer.TileFactory
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import javax.swing.text.AbstractDocument


/**
 * Panel mit den Formular-Feldern
 *
 * Anordnung der Check Boxes:
 * <pre>
 * [] Fahrzeug war verlassen        [] mit Behinderung [] Umweltplakette fehlt/ungültig
 * [] Warnblinkanlage eingeschaltet [] mit Gefährdung  [] HU-Plakette abgelaufen
 * </pre>
 * todo Prio 4: Rechtschreibprüfung insbesondere für Hinweis-Textarea
 */
class NoticeFormFields(
    private val noticeFrame: NoticeFrame,
    private val selectedPhotosListModel: SelectedPhotosListModel,
    private val dbRepo: DatabaseRepo,
    private val tileFactory: TileFactory
) : JPanel(), ListDataListener /* SelectedPhotosObserver */ {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    //private val countrySymbolComboBox = JComboBox(CountrySymbol.COUNTRY_SYMBOLS)
    private val countryComboBox = CountryComboBox()
    private val licensePlateTextField = TrimmingTextField(10)

    //private val vehicleMakeComboBox = JComboBox(ListVehicleMakes.VEHICLE_MAKES)
    private val vehicleMakeComboBox = VehicleMakeComboBox()
    private val colorComboBox = JComboBox(VehicleColor.COLORS)
    private val miniMap = MiniMap(noticeFrame, selectedPhotosListModel, tileFactory)
    private var streetTextField = TrimmingTextField(30)
    private var zipCodeTextField = TrimmingTextField(5)
    private var townTextField = TrimmingTextField(30)
    private var locationDescriptionTextField = TrimmingTextField(40)
    private var offenseComboBox = OffenseComboBox()

    //private var offenseComboBox = JComboBox(Offense.selectableOffenses())
    private val observationDateTextField = JTextField(10)
    private val observationTimeTextField = TrimmingTextField(5)
    private val durationTextField = JTextField(3)
    private val abandonedCheckBox = JCheckBox("Fahrzeug war verlassen")
    private val obstructionCheckBox = JCheckBox("mit Behinderung")
    private val endangeringCheckBox = JCheckBox("mit Gefährdung")
    private val environmentalStickerCheckBox = JCheckBox("Umweltplakette fehlt/ungültig")
    private val vehicleInspectionStickerCheckBox = JCheckBox("HU-Plakette abgelaufen")
    private val warningLightsCheckBox = JCheckBox("Warnblinkanlage eingeschaltet")
    private val inspectionMonthYearLabel = JLabel("HU-Fälligkeit Jahr:")
    private val inspectionMonthTextField = JTextField(2)
    private val monthYearSeparatorLabel = JLabel("/")
    private val inspectionYearTextField = JTextField(4)

    // Idealfall: Addresse wird automatisch eingetragen, Ausnahmefall Benutzer wählt aus Adressbuch
    // todo Prio 3: Auswahl des Empfängers aus Addressbuch (Button öffnet "AddressChooser")
    // todo Prio 3: eine Adresse aus der Datenbank anhand der GeoPosition vorschlagen
    private val recipientComboBox = RecipientComboBox(dbRepo)
    private val noteTextArea = JTextArea(2, 40)

    private var noticeEntity: NoticeEntity? = null

    init {
        log.debug("init")

        // GUI components
        val licensePlateLabel = JLabel("<html>Landes- & Kfz-Kennzeichen<sup>*</sup>:</html>")
        //countryComboBox.renderer = CountrySymbolListCellRenderer()
        val licensePlateDoc = licensePlateTextField.document
        if (licensePlateDoc is AbstractDocument) {
            licensePlateDoc.documentFilter = UppercaseDocumentFilter()
        }
        val vehicleMakeLabel = JLabel("Fahrzeugmarke & Farbe:")
        //vehicleMakeComboBox.isEditable = true


        colorComboBox.renderer = ColorListCellRenderer()
        colorComboBox.maximumRowCount = VehicleColor.COLORS.size
        miniMap.toolTipText = "Bitte positionieren Sie den roten Pin."
        val coordinatesLabel = JLabel("Koordinaten:")
        val streetLabel = JLabel("<html>Straße & Hausnummer:<sup>*</sup></html>")
        streetTextField.toolTipText = "z.B. Taunusstraße 7"
        val zipCodeTownLabel = JLabel("<html>PLZ:<sup>*</sup>, Ort:<sup>*</sup></html>")
        zipCodeTextField.toolTipText = "z.B. 65183"
        val locationDescriptionLabel = JLabel("Tatort:")
        townTextField.toolTipText = "z.B. Wiesbaden"
        locationDescriptionTextField.toolTipText = "z.B. Bushaltestelle Kochbrunnen"

        // todo Prio 1: Auto-complete
        // Benutzer gibt ein Wortbestandteil ein. Die Einträge im PullDownMenü werden gefiltert.
        val offenseLabel = JLabel("<html>Verstoß:<sup>*</sup></html>")
        //offenseComboBox.renderer = OffenseListCellRenderer()

        val observationDateTimeLabel = JLabel("<html>Beobachtungsdatum<sup>*</sup>, Uhrzeit:<sup>*</sup></html>")
        val observationDateDoc = observationDateTextField.document
        if (observationDateDoc is AbstractDocument) {
            observationDateDoc.documentFilter = CharPredicateDocFilter.dateDocFilter
        }

        observationDateTextField.toolTipText = "z.B. 31.12.2021"
        observationDateTextField.inputVerifier = PatternVerifier.dateVerifier
        val observationTimeDoc = observationTimeTextField.document
        if (observationTimeDoc is AbstractDocument) {
            observationTimeDoc.documentFilter = CharPredicateDocFilter.timeDocFilter
        }
        observationTimeTextField.toolTipText = "z.B. 23:59"
        observationTimeTextField.inputVerifier = PatternVerifier.timeVerifier
        val durationLabel = JLabel("<html>Beobachtungs-Dauer (in Minuten):<sup>*</sup></html>")
        val durationDoc = durationTextField.document
        if (durationDoc is AbstractDocument) {
            durationDoc.documentFilter = CharPredicateDocFilter.onlyDigitsDocFilter
        }
        durationTextField.toolTipText = "Ganzzahl"
        vehicleInspectionStickerCheckBox.addChangeListener {
            val src = it.source as JCheckBox
            inspectionMonthYearLabel.isVisible = src.isSelected
            inspectionMonthTextField.isVisible = src.isSelected
            monthYearSeparatorLabel.isVisible = src.isSelected
            inspectionYearTextField.isVisible = src.isSelected
        }
        vehicleInspectionStickerCheckBox.toolTipText = "Hauptuntersuchung/\"TÜV\""
        val inspectionMonthDoc = inspectionMonthTextField.document
        if (inspectionMonthDoc is AbstractDocument) {
            inspectionMonthDoc.documentFilter = CharPredicateDocFilter.onlyDigitsDocFilter
        }
        inspectionMonthTextField.toolTipText = "Ganzzahl 1-12"
        inspectionMonthTextField.inputVerifier = PatternVerifier.inspectionMonthVerifier
        inspectionMonthTextField.isVisible = false
        val inspectionYearDoc = inspectionYearTextField.document
        if (inspectionYearDoc is AbstractDocument) {
            inspectionYearDoc.documentFilter = CharPredicateDocFilter.onlyDigitsDocFilter
        }
        inspectionYearTextField.toolTipText = "Ganzzahl 4-stellig"
        inspectionYearTextField.inputVerifier = PatternVerifier.inspectionYearVerifier
        inspectionYearTextField.isVisible = false

        val recipientLabel = JLabel("<html>Empfänger:<sup>*</sup></html>")
        recipientComboBox.toolTipText = "z.B. verwarngeldstelle@wiesbaden.de"
        recipientComboBox.inputVerifier = PatternVerifier.eMailVerifier
        val noteLabel = JLabel("Hinweis:")
        noteTextArea.toolTipText = "z.B. Behinderung / Gefährdung beschreiben"


        // layout
        val lay = GroupLayout(this)
        lay.autoCreateGaps = true
        lay.autoCreateContainerGaps = true

        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup( // labels & form fields
                    lay.createSequentialGroup()
                        .addGroup( // labels
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(licensePlateLabel)
                                .addComponent(vehicleMakeLabel)
                                .addComponent(coordinatesLabel)
                                .addComponent(streetLabel)
                                .addComponent(zipCodeTownLabel)
                                .addComponent(locationDescriptionLabel)
                                .addComponent(offenseLabel)
                                .addComponent(observationDateTimeLabel)
                                .addComponent(durationLabel)
                                .addComponent(recipientLabel)
                                .addComponent(inspectionMonthYearLabel)
                                .addComponent(noteLabel)
                        )
                        .addGroup( // form fields
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(countryComboBox)
                                        .addComponent(licensePlateTextField)
                                )
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(vehicleMakeComboBox)
                                        .addComponent(colorComboBox)
                                )
                                .addComponent(miniMap)
                                .addComponent(streetTextField)
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(zipCodeTextField)
                                        .addComponent(townTextField)
                                )
                                .addComponent(locationDescriptionTextField)
                                .addComponent(offenseComboBox)
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(observationDateTextField)
                                        .addComponent(observationTimeTextField)
                                )
                                .addComponent(durationTextField)
                                .addGroup(
                                    lay.createSequentialGroup()
                                        .addComponent(inspectionMonthTextField)
                                        .addComponent(monthYearSeparatorLabel)
                                        .addComponent(inspectionYearTextField)
                                )
                                .addComponent(recipientComboBox)
                                .addComponent(noteTextArea)
                        )
                )
                .addGroup( // check boxes
                    lay.createSequentialGroup()
                        .addGroup( // 1st column of check boxes
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(abandonedCheckBox)
                                .addComponent(warningLightsCheckBox)

                        )
                        .addGroup( // 2nd column of check boxes
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(obstructionCheckBox)
                                .addComponent(endangeringCheckBox)
                        )
                        .addGroup( // 3rd column of check boxes
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(environmentalStickerCheckBox)
                                .addComponent(vehicleInspectionStickerCheckBox)
                        )
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(licensePlateLabel).addComponent(countryComboBox)
                        .addComponent(licensePlateTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(vehicleMakeLabel).addComponent(vehicleMakeComboBox).addComponent(colorComboBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(coordinatesLabel).addComponent(miniMap)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(streetLabel).addComponent(streetTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(zipCodeTownLabel).addComponent(zipCodeTextField).addComponent(townTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(locationDescriptionLabel).addComponent(locationDescriptionTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(offenseLabel).addComponent(offenseComboBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(observationDateTimeLabel).addComponent(observationDateTextField)
                        .addComponent(observationTimeTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(durationLabel).addComponent(durationTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(abandonedCheckBox).addComponent(obstructionCheckBox)
                        .addComponent(environmentalStickerCheckBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(warningLightsCheckBox).addComponent(endangeringCheckBox)
                        .addComponent(vehicleInspectionStickerCheckBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(inspectionMonthYearLabel).addComponent(inspectionMonthTextField)
                        .addComponent(monthYearSeparatorLabel).addComponent(inspectionYearTextField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(recipientLabel).addComponent(recipientComboBox)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(noteLabel).addComponent(noteTextArea)
                )
        )
        layout = lay
        components.filterIsInstance<JTextField>().forEach(Styles::restrictHeight)
        components.filterIsInstance<JComboBox<*>>().forEach(Styles::restrictSize)
        components.filterIsInstance<JLabel>().forEach(Styles::restrictSize)
        Styles.restrictSize(licensePlateTextField)
        Styles.restrictSize(zipCodeTextField)
        Styles.restrictSize(observationDateTextField)
        Styles.restrictSize(observationTimeTextField)
        Styles.restrictSize(durationTextField)
        Styles.restrictSize(inspectionYearTextField)
        Styles.restrictSize(inspectionMonthTextField)

        enableOrDisableEditing()
    }

    /**
     * Initialisieren der einzelnen Eingabe-Felder
     * Mapping von Notice zu GUI-Components
     */
    fun setNotice(noticeEntity: NoticeEntity) {
        this.noticeEntity = noticeEntity

        noticeEntity.getGeoPosition()?.let {
            miniMap.setOffensePosition(it)
        }
        countryComboBox.setValue(noticeEntity.countrySymbol)
        licensePlateTextField.text = noticeEntity.licensePlate
        vehicleMakeComboBox.setValue(noticeEntity.vehicleMake)
        colorComboBox.selectedItem = VehicleColor.fromColorName(noticeEntity.color)
        streetTextField.text = noticeEntity.street
        zipCodeTextField.text = noticeEntity.zipCode
        townTextField.text = noticeEntity.town
        locationDescriptionTextField.text = noticeEntity.locationDescription
        offenseComboBox.setValue(noticeEntity.offense)
        observationDateTextField.text = blankOrDateString(noticeEntity.observationTime)
        observationTimeTextField.text = blankOrTimeString(noticeEntity.observationTime)
        durationTextField.text = blankOrIntString(noticeEntity.duration)
        obstructionCheckBox.isSelected = noticeEntity.obstruction
        endangeringCheckBox.isSelected = noticeEntity.endangering
        environmentalStickerCheckBox.isSelected = noticeEntity.environmentalStickerMissing
        vehicleInspectionStickerCheckBox.isSelected = noticeEntity.vehicleInspectionExpired
        inspectionMonthYearLabel.isVisible = inspectionYearTextField.text.isNotBlank()
        inspectionYearTextField.isVisible = inspectionYearTextField.text.isNotBlank()
        inspectionYearTextField.text = blankOrShortString(noticeEntity.vehicleInspectionYear)
        inspectionMonthTextField.isVisible = inspectionMonthTextField.text.isNotBlank()
        inspectionMonthTextField.text = blankOrByteString(noticeEntity.vehicleInspectionMonth)
        abandonedCheckBox.isSelected = noticeEntity.vehicleAbandoned
        warningLightsCheckBox.isSelected = noticeEntity.warningLights
        recipientComboBox.loadData()
        recipientComboBox.setValue(noticeEntity.getRecipient())
        noteTextArea.text = noticeEntity.note

        enableOrDisableEditing()
    }

    /**
     * Mapping der Werte der GUI-Komponenten zu Notice
     */
    // todo Prio 1: form validation, Validierungsfehler bei Eingabefeldern anzeigen
    fun getNotice(): NoticeEntity {
        // Normalerweise sollte vorher setNotice() aufgerufen worden sein.
        // Aber falls nicht, wird ein neues Notice-Objekt instanziiert.
        val n = noticeEntity ?: NoticeEntity.createdNow()

        n.countrySymbol = countryComboBox.getValue()
        n.licensePlate = trimmedOrNull(licensePlateTextField.text)
        n.vehicleMake = vehicleMakeComboBox.getValue()

        val selectedColor = colorComboBox.selectedObjects[0] as VehicleColor
        n.color = if (selectedColor.color == null) {
            null
        } else {
            selectedColor.colorName
        }

        // todo Prio 1: map addressLocation

        n.street = trimmedOrNull(streetTextField.text)
        n.zipCode = trimmedOrNull(zipCodeTextField.text)
        n.town = trimmedOrNull(townTextField.text)
        n.locationDescription = trimmedOrNull(locationDescriptionTextField.text)
        n.offense = offenseComboBox.getValue()

        val format = DateTimeFormatter.ofPattern("d.M.yyyy")
        val obsDateTxt = observationDateTextField.text
        n.observationTime = if (obsDateTxt.isBlank()) {
            null
        } else {
            val dat: LocalDate = LocalDate.parse(obsDateTxt, format)
            // todo Prio 2: Validierung, ob Format von Datum und Uhrzeit korrekt sind. Fehlermeldung anzeigen.
            // todo Prio 3: Problem lösen: Sommer- oder Winterzeit, eine Stunde im Jahr ist zweideutig
            // todo Prio 3: Datum/Uhrzeit darf nicht in der Zukunft liegen
            val obsTimeTxt = observationTimeTextField.text
            val tim = if (obsTimeTxt.isBlank()) {
                LocalTime.parse("00:00")
            } else {
                val tFormat = DateTimeFormatter.ofPattern("H:m")
                LocalTime.parse(obsTimeTxt, tFormat)
                //LocalTime.parse(obsTimeTxt)
            }
            ZonedDateTime.of(dat, tim, ZoneId.systemDefault())
        }

        n.duration = intOrNull(durationTextField.text)
        n.obstruction = obstructionCheckBox.isSelected
        n.endangering = endangeringCheckBox.isSelected
        n.environmentalStickerMissing = environmentalStickerCheckBox.isSelected
        n.vehicleInspectionExpired = vehicleInspectionStickerCheckBox.isSelected
        n.vehicleInspectionYear = if (n.vehicleInspectionExpired) {
            shortOrNull(inspectionYearTextField.text)
        } else {
            null
        }
        n.vehicleInspectionMonth = if (n.vehicleInspectionExpired) {
            byteOrNull(inspectionMonthTextField.text)
        } else {
            null
        }
        n.vehicleAbandoned = abandonedCheckBox.isSelected
        n.warningLights = warningLightsCheckBox.isSelected

        val recipient = recipientComboBox.getValue()
        n.recipientEmailAddress = recipient?.address
        n.recipientName = recipient?.name

        n.note = trimmedOrNull(noteTextArea.text)
        return n
    }

    fun getMiniMap(): MiniMap {
        return miniMap
    }

    fun setStreet(street: String) {
        streetTextField.text = street
    }

    fun setZipCode(zipCode: String) {
        zipCodeTextField.text = zipCode
    }

    fun setTown(town: String) {
        townTextField.text = town
    }

    /**
     * keine weitere Bearbeitung mehr zulassen,
     * wenn die Meldung bereits versendet wurde.
     */
    fun enableOrDisableEditing() {
        var enab = false
        noticeEntity?.let {
            enab = !it.isSent()
        }
        //val enab = (notice != null) && !notice.isSent()
        countryComboBox.isEnabled = enab
        licensePlateTextField.isEnabled = enab
        vehicleMakeComboBox.isEnabled = enab
        colorComboBox.isEnabled = enab
        streetTextField.isEnabled = enab
        zipCodeTextField.isEnabled = enab
        townTextField.isEnabled = enab
        locationDescriptionTextField.isEnabled = enab
        offenseComboBox.isEnabled = enab
        observationDateTextField.isEnabled = enab
        observationTimeTextField.isEnabled = enab
        durationTextField.isEnabled = enab
        obstructionCheckBox.isEnabled = enab
        endangeringCheckBox.isEnabled = enab
        environmentalStickerCheckBox.isEnabled = enab
        vehicleInspectionStickerCheckBox.isEnabled = enab
        inspectionYearTextField.isEnabled = enab
        inspectionMonthTextField.isEnabled = enab
        abandonedCheckBox.isEnabled = enab
        recipientComboBox.isEnabled = enab
        warningLightsCheckBox.isEnabled = enab
        noteTextArea.isEnabled = enab
    }

    fun selectedPhoto(photo: Photo) {
        if (photo.getDateTime() != null) {
            updateDateTimeAndDuration()
        }
    }

    fun unselectedPhoto(photo: Photo) {
        if (photo.getDateTime() != null) {
            updateDateTimeAndDuration()
        }
    }

    fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        if (photos.size != 0) {
            updateDateTimeAndDuration()
        }
    }

    private fun updateDateTimeAndDuration() {
        val newStartTime = selectedPhotosListModel.getStartTime()
        observationDateTextField.text = blankOrDateString(newStartTime)
        observationTimeTextField.text = blankOrTimeString(newStartTime)
        durationTextField.text = blankOrIntString(selectedPhotosListModel.getDuration())
    }

    companion object {
        /**
         * trimms String and returns null if blank
         * to be used before storing a String to the database
         * examples:
         * <ul>
         *   <li>null -> null</li>
         *   <li>"" -> null</li>
         *   <li>" " -> null</li>
         *   <li>" Hello " -> "Hello"</li>
         *   <li>"Hello\n" -> "Hello"</li>
         * </ul>
         */
        fun trimmedOrNull(str: String?): String? {
            return if (str == null) {
                null
            } else {
                val trimmed = str.trim()
                if (trimmed == "") {
                    null
                } else {
                    trimmed
                }
            }
        }

        /**
         * <ul>
         *   <li>null -> null</li>
         *   <li>"" -> null</li>
         *   <li>" " -> null</li>
         *   <li>"1" -> 1</li>
         *   <li>"01" -> 1</li>
         * </ul>
         */
        fun intOrNull(str: String?): Int? {
            return if (str.isNullOrBlank()) {
                null
            } else {
                str.toInt()
            }
        }

        fun shortOrNull(str: String?): Short? {
            return if (str.isNullOrBlank()) {
                null
            } else {
                str.toShort()
            }
        }

        fun byteOrNull(str: String?): Byte? {
            return if (str.isNullOrBlank()) {
                null
            } else {
                str.toByte()
            }
        }

        fun blankOrString(str: String?): String {
            return str ?: ""
        }

        fun blankOrIntString(i: Int?): String {
            return i?.toString() ?: ""
        }

        fun blankOrShortString(s: Short?): String {
            return s?.toString() ?: ""
        }

        fun blankOrByteString(b: Byte?): String {
            return b?.toString() ?: ""
        }

        fun blankOrDateString(zdt: ZonedDateTime?): String {
            return if (zdt == null) {
                ""
            } else {
                val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                fmt.format(zdt)
            }
        }

        fun blankOrTimeString(zdt: ZonedDateTime?): String {
            return if (zdt == null) {
                ""
            } else {
                val fmt = DateTimeFormatter.ofPattern("HH:mm")
                fmt.format(zdt)
            }
        }
    }

    override fun intervalAdded(e: ListDataEvent?) {
        if (e is SelectedPhotosListDataEvent) {
            e.photos.forEach { selectedPhoto(it) }
        }
    }

    override fun intervalRemoved(e: ListDataEvent?) {
        if (e is SelectedPhotosListDataEvent) {
            e.photos.forEach { unselectedPhoto(it) }
        }
    }

    override fun contentsChanged(e: ListDataEvent?) {
        // ignore
    }
}