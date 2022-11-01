package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.*
import de.heikozelt.wegefrei.maps.MiniMap
import de.heikozelt.wegefrei.model.*
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.*
import javax.swing.text.AbstractDocument

/**
 * Panel mit den Formular-Feldern
 *
 * Anordnung der Check Boxes:
 * <pre>
 * [] Fahrzeug war verlassen        [] mit Behinderung [] Umweltplakette fehlt/ungültig
 * [] Warnblinkanlage eingeschaltet [] mit Gefährdung  [] HU-Plakette abgelaufen
 * </pre>
 */
class NoticeFormFields(private val noticeFrame: NoticeFrame) : JPanel(), SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val countrySymbolComboBox = JComboBox(CountrySymbol.COUNTRY_SYMBOLS)
    private val licensePlateTextField = TrimmingTextField(10)
    private val vehicleMakeComboBox = JComboBox(ListVehicleMakes.VEHICLE_MAKES)
    private val colorComboBox = JComboBox(VehicleColor.COLORS)
    private val miniMap = MiniMap(noticeFrame)
    private var streetTextField = TrimmingTextField(30)
    private var zipCodeTextField = TrimmingTextField(5)
    private var townTextField = TrimmingTextField(30)
    private var locationDescriptionTextField = TrimmingTextField(40)
    private var offenseComboBox = JComboBox(Offense.selectableOffenses())
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
    private val recipientTextField = TrimmingTextField(30)
    private val noteTextArea = JTextArea(2, 40)

    private var notice: Notice? = null

    init {
        log.debug("init")

        // GUI components
        val licensePlateLabel = JLabel("<html>Landes- & Kfz-Kennzeichen<sup>*</sup>:</html>")
        countrySymbolComboBox.renderer = CountrySymbolListCellRenderer()
        val licensePlateDoc = licensePlateTextField.document
        if (licensePlateDoc is AbstractDocument) {
            licensePlateDoc.documentFilter = UppercaseDocumentFilter()
        }
        val vehicleMakeLabel = JLabel("Fahrzeugmarke & Farbe:")
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
        val offenseLabel = JLabel("<html>Verstoß:<sup>*</sup></html>")
        offenseComboBox.renderer = OffenseListCellRenderer()
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
        recipientTextField.toolTipText = "z.B. verwarngeldstelle@wiesbaden.de"
        recipientTextField.inputVerifier = PatternVerifier.eMailVerifier
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
                                        .addComponent(countrySymbolComboBox)
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
                                .addComponent(recipientTextField)
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
                        .addComponent(licensePlateLabel).addComponent(countrySymbolComboBox).addComponent(licensePlateTextField)
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
                        .addComponent(recipientLabel).addComponent(recipientTextField)
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
    fun setNotice(notice: Notice) {
        this.notice = notice

        notice.getGeoPosition()?.let {
            miniMap.setOffensePosition(it)
        }

        val countrySymbol = CountrySymbol.fromAbbreviation(notice.countrySymbol)
        countrySymbolComboBox.selectedItem = countrySymbol
        licensePlateTextField.text = notice.licensePlate

        val make = ListVehicleMakes.VEHICLE_MAKES.find { it == notice.vehicleMake }
        make?.let {
            vehicleMakeComboBox.selectedItem = make
        }

        colorComboBox.selectedItem = VehicleColor.fromColorName(notice.color)
        streetTextField.text = notice.street
        zipCodeTextField.text = notice.zipCode
        townTextField.text = notice.town
        locationDescriptionTextField.text = notice.locationDescription
        offenseComboBox.selectedItem = Offense.fromId(notice.offense)
        observationDateTextField.text = blankOrDateString(notice.observationTime)
        observationTimeTextField.text = blankOrTimeString(notice.observationTime)
        durationTextField.text = blankOrIntString(notice.duration)
        obstructionCheckBox.isSelected = notice.obstruction
        endangeringCheckBox.isSelected = notice.endangering
        environmentalStickerCheckBox.isSelected = notice.environmentalStickerMissing
        vehicleInspectionStickerCheckBox.isSelected = notice.vehicleInspectionExpired
        inspectionMonthYearLabel.isVisible = inspectionYearTextField.text.isNotBlank()
        inspectionYearTextField.isVisible = inspectionYearTextField.text.isNotBlank()
        inspectionYearTextField.text = blankOrShortString(notice.vehicleInspectionYear)
        inspectionMonthTextField.isVisible = inspectionMonthTextField.text.isNotBlank()
        inspectionMonthTextField.text = blankOrByteString(notice.vehicleInspectionMonth)
        abandonedCheckBox.isSelected = notice.vehicleAbandoned
        warningLightsCheckBox.isSelected = notice.warningLights
        recipientTextField.text = notice.recipient
        noteTextArea.text = notice.note

        enableOrDisableEditing()
    }

    /**
     * Mapping der Werte der GUI-Komponenten zu Notice
     */
    // todo Prio 1: form validation, Validierungsfehler bei Eingabefeldern anzeigen
    fun getNotice(): Notice {
        // Normalerweise sollte vorher setNotice() aufgerufen worden sein.
        // Aber falls nicht, wird ein neues Notice-Objekt instanziiert.
        val n = notice?:Notice()

        n.photos = noticeFrame.getSelectedPhotos().getPhotos()

        val selectedCountry = countrySymbolComboBox.selectedObjects[0] as CountrySymbol
        n.countrySymbol = if (selectedCountry.countryName == null) {
            null
        } else {
            selectedCountry.abbreviation
        }

        n.licensePlate = trimmedOrNull(licensePlateTextField.text)

        val selectedVehicleMake = vehicleMakeComboBox.selectedObjects[0] as String
        n.vehicleMake = if (selectedVehicleMake == "--") {
            null
        } else {
            selectedVehicleMake
        }

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
        val selectedOffense = offenseComboBox.selectedItem
        n.offense = if (selectedOffense is Offense) {
            selectedOffense.id
        } else {
            null
        }

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
        n.recipient = trimmedOrNull(recipientTextField.text)
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
        notice?.let {
            enab = !it.isSent()
        }
        //val enab = (notice != null) && !notice.isSent()
        countrySymbolComboBox.isEnabled = enab
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
        recipientTextField.isEnabled = enab
        noteTextArea.isEnabled = enab
    }

    override fun selectedPhoto(index: Int, photo: Photo) {
        if (photo.date != null) {
            updateDateTimeAndDuration()
        }
    }

    override fun unselectedPhoto(index: Int, photo: Photo) {
        if (photo.date != null) {
            updateDateTimeAndDuration()
        }
    }

    override fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        if (photos.size != 0) {
            updateDateTimeAndDuration()
        }
    }

    private fun updateDateTimeAndDuration() {
        val selectedPhotos = noticeFrame.getSelectedPhotos()
        val newStartTime = selectedPhotos.getStartTime()
        observationDateTextField.text = blankOrDateString(newStartTime)
        observationTimeTextField.text = blankOrTimeString(newStartTime)
        durationTextField.text = blankOrIntString(selectedPhotos.getDuartion())
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
}