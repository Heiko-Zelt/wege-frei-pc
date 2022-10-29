package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.docfilters.DateDocFilter
import de.heikozelt.wegefrei.docfilters.OnlyDigitsDocFilter
import de.heikozelt.wegefrei.docfilters.TimeDocFilter
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.*
import de.heikozelt.wegefrei.gui.Styles.Companion.FORM_BACKGROUND
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.TEXTFIELD_FONT
import de.heikozelt.wegefrei.maps.MiniMap
import de.heikozelt.wegefrei.model.*
import org.slf4j.LoggerFactory
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import java.awt.Insets
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
    private val obstructionCheckBox = JCheckBox("mit Behinderung")
    private val endangeringCheckBox = JCheckBox("mit Gefährdung")
    private val environmentalStickerCheckBox = JCheckBox("Umweltplakette fehlt/ungültig")
    private val vehicleInspectionStickerCheckBox = JCheckBox("HU Plakette abgelaufen")
    private val inspectionYearLabel = JLabel("HU-Fälligkeit Jahr:")
    private val inspectionYearTextField = JTextField(4)
    private val inspectionMonthLabel = JLabel("HU-Fälligkeit Monat:")
    private val inspectionMonthTextField = JTextField(2)
    private val abandonedCheckBox = JCheckBox("Fahrzeug war verlassen")
    private val recipientTextField = TrimmingTextField(30)
    private val noteTextArea = JTextArea(3, 50)

    init {
        log.debug("init")

        background = FORM_BACKGROUND
        border = NO_BORDER
        layout = GridBagLayout()
        val constraints = GridBagConstraints()
        constraints.insets = Insets(0, 5, 0, 0)
        constraints.anchor = WEST
        //constraints.fill = BOTH
        constraints.weightx = 0.5
        constraints.weighty = 0.1

        constraints.gridy++
        val countrySymbolLabel = JLabel("Landeskennzeichen:")
        //countrySymbolLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        constraints.gridwidth = 1
        add(countrySymbolLabel, constraints)
        constraints.gridx = 1
        countrySymbolComboBox.renderer = CountrySymbolListCellRenderer()
        countrySymbolComboBox.font = TEXTFIELD_FONT
        add(countrySymbolComboBox, constraints)

        constraints.gridy++
        val licensePlateLabel = JLabel("<html>Kfz-Kennzeichen:<sup>*</sup></html>")
        //licensePlateLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(licensePlateLabel, constraints)
        constraints.gridx = 1
        val licensePlateDoc = licensePlateTextField.document
        if (licensePlateDoc is AbstractDocument) {
            licensePlateDoc.documentFilter = UppercaseDocumentFilter()
        }
        add(licensePlateTextField, constraints)

        constraints.gridy++
        val vehicleMakeLabel = JLabel("Fahrzeugmarke:")
        //vehicleMakeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(vehicleMakeLabel, constraints)
        constraints.gridx = 1
        vehicleMakeComboBox.font = TEXTFIELD_FONT
        add(vehicleMakeComboBox, constraints)

        constraints.gridy++
        val colorLabel = JLabel("Farbe:")
        //colorLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(colorLabel, constraints)
        //val modell = DefaultComboBoxModel(COLORS)

        colorComboBox.renderer = ColorListCellRenderer()
        colorComboBox.maximumRowCount = VehicleColor.COLORS.size
        colorComboBox.font = TEXTFIELD_FONT
        constraints.gridx = 1
        add(colorComboBox, constraints)

        constraints.gridy++
        val coordinatesLabel = JLabel("Koordinaten:")
        //coordinatesLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(coordinatesLabel, constraints)
        constraints.gridx = 1
        constraints.weighty = 1.0
        miniMap.toolTipText = "Bitte positionieren Sie den roten Pin."
        add(miniMap, constraints)

        constraints.gridy++
        val streetLabel = JLabel("<html>Straße & Hausnummer:<sup>*</sup></html>")
        //streetLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        constraints.weighty = 0.1
        add(streetLabel, constraints)
        constraints.gridx = 1
        streetTextField.toolTipText = "z.B. Taunusstraße 7"
        add(streetTextField, constraints)

        constraints.gridy++
        val zipCodeLabel = JLabel("<html>PLZ:<sup>*</sup></html>")
        //zipCodeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(zipCodeLabel, constraints)
        constraints.gridx = 1
        zipCodeTextField.toolTipText = "z.B. 65183"
        add(zipCodeTextField, constraints)

        constraints.gridy++
        val townLabel = JLabel("<html>Ort:<sup>*</sup></html>")
        //townLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(townLabel, constraints)
        constraints.gridx = 1
        townTextField.toolTipText = "z.B. Wiesbaden"
        add(townTextField, constraints)

        constraints.gridy++
        val locationDescriptionLabel = JLabel("Tatort:")
        //locationDescriptionLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(locationDescriptionLabel, constraints)
        constraints.gridx = 1
        locationDescriptionTextField.toolTipText = "z.B. Bushaltestelle Kochbrunnen"
        add(locationDescriptionTextField, constraints)

        constraints.gridy++
        val offenseLabel = JLabel("<html>Verstoß:<sup>*</sup></html>")
        constraints.gridx = 0
        add(offenseLabel, constraints)
        constraints.gridx = 1
        //offenseComboBox.prototypeDisplayValue = Offense.withLongestText();
        offenseComboBox.font = TEXTFIELD_FONT

        offenseComboBox.renderer = OffenseListCellRenderer()
        add(offenseComboBox, constraints)

        constraints.gridy++
        val observationDateLabel = JLabel("<html>Beobachtungs-Datum:<sup>*</sup></html>")
        //offenseDateLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(observationDateLabel, constraints)
        constraints.gridx = 1
        val observationDateDoc = observationDateTextField.document
        if (observationDateDoc is AbstractDocument) {
            observationDateDoc.documentFilter = DateDocFilter()
        }
        observationDateTextField.toolTipText = "z.B. 31.12.2021"
        observationDateTextField.inputVerifier = Verifiers.dateVerifier
        add(observationDateTextField, constraints)

        constraints.gridy++
        val observationTimeLabel = JLabel("<html>Beobachtungs-Uhrzeit:<sup>*</sup></html>")
        //offenseTimeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(observationTimeLabel, constraints)
        constraints.gridx = 1
        val observationTimeDoc = observationTimeTextField.document
        if (observationTimeDoc is AbstractDocument) {
            observationTimeDoc.documentFilter = TimeDocFilter()
        }
        observationTimeTextField.toolTipText = "z.B. 23:59"
        observationTimeTextField.inputVerifier = Verifiers.timeVerifier
        add(observationTimeTextField, constraints)

        constraints.gridy++
        val durationLabel = JLabel("<html>Beobachtungs-Dauer (in Minuten):<sup>*</sup></html>")
        //durationLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(durationLabel, constraints)
        constraints.gridx = 1
        val durationDoc = durationTextField.document
        if (durationDoc is AbstractDocument) {
            durationDoc.documentFilter = OnlyDigitsDocFilter()
        }
        durationTextField.toolTipText = "Ganzzahl"
        add(durationTextField, constraints)

        constraints.gridy++
        abandonedCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        add(abandonedCheckBox, constraints)

        obstructionCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 1
        add(obstructionCheckBox, constraints)

        constraints.gridy++
        environmentalStickerCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        add(environmentalStickerCheckBox, constraints)

        endangeringCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 1
        add(endangeringCheckBox, constraints)

        constraints.gridy++
        vehicleInspectionStickerCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        vehicleInspectionStickerCheckBox.addChangeListener {
            val src = it.source as JCheckBox
            inspectionYearLabel.isVisible = src.isSelected
            inspectionYearTextField.isVisible = src.isSelected
            inspectionMonthLabel.isVisible = src.isSelected
            inspectionMonthTextField.isVisible = src.isSelected
        }
        add(vehicleInspectionStickerCheckBox, constraints)

        constraints.gridy++
        constraints.gridx = 0
        add(inspectionYearLabel, constraints)
        constraints.gridx = 1
        val inspectionYearDoc = inspectionYearTextField.document
        if (inspectionYearDoc is AbstractDocument) {
            inspectionYearDoc.documentFilter = OnlyDigitsDocFilter()
        }
        inspectionYearTextField.toolTipText = "Ganzzahl 4-stellig"
        inspectionYearTextField.inputVerifier = Verifiers.inspectionYearVerifier
        inspectionYearTextField.isVisible = false
        add(inspectionYearTextField, constraints)

        constraints.gridy++
        constraints.gridx = 0
        add(inspectionMonthLabel, constraints)
        constraints.gridx = 1
        val inspectionMonthDoc = inspectionMonthTextField.document
        if (inspectionMonthDoc is AbstractDocument) {
            inspectionMonthDoc.documentFilter = OnlyDigitsDocFilter()
        }
        inspectionMonthTextField.toolTipText = "Ganzzahl 1-12"
        inspectionMonthTextField.inputVerifier = Verifiers.inspectionMonthVerifier
        inspectionMonthTextField.isVisible = false
        add(inspectionMonthTextField, constraints)

        constraints.gridy++
        val recipientLabel = JLabel("<html>Empfänger:<sup>*</sup></html>")
        constraints.gridx = 0
        add(recipientLabel, constraints)
        constraints.gridx = 1
        recipientTextField.toolTipText = "z.B. verwarngeldstelle@wiesbaden.de"
        recipientTextField.inputVerifier = Verifiers.eMailVerifier
        add(recipientTextField, constraints)

        constraints.gridy++
        val noteLabel = JLabel("Hinweis:")
        constraints.gridx = 0
        add(noteLabel, constraints)
        constraints.gridx = 1
        noteTextArea.toolTipText = "z.B. Behinderung / Gefährdung beschreiben"
        add(noteTextArea, constraints)

        setSize(700, 700)

        enableOrDisableEditing()
        isVisible = true
    }

    /**
     * Initialisieren der einzelnen Eingabe-Felder
     * Mapping von Notice zu GUI-Components
     */
    fun loadData() {
        val notice = noticeFrame.getNotice() ?: return

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
        inspectionYearLabel.isVisible = inspectionYearTextField.text.isNotBlank()
        inspectionYearTextField.isVisible = inspectionYearTextField.text.isNotBlank()
        inspectionYearTextField.text = blankOrShortString(notice.vehicleInspectionYear)
        inspectionMonthLabel.isVisible = inspectionMonthTextField.text.isNotBlank()
        inspectionMonthTextField.isVisible = inspectionMonthTextField.text.isNotBlank()
        inspectionMonthTextField.text = blankOrByteString(notice.vehicleInspectionMonth)
        abandonedCheckBox.isSelected = notice.vehicleAbandoned
        recipientTextField.text = notice.recipient
        noteTextArea.text = notice.note

        enableOrDisableEditing()
    }

    /**
     * Mapping der Werte der GUI-Komponenten zu Notice
     */
    // todo Prio 1: form validation, Validierungsfehler bei Eingabefeldern anzeigen
    fun saveNotice() {
        val notice = noticeFrame.getNotice() ?: return

        notice.photos = noticeFrame.getSelectedPhotos().getPhotos()

        val selectedCountry = countrySymbolComboBox.selectedObjects[0] as CountrySymbol
        notice.countrySymbol = if (selectedCountry.countryName == null) {
            null
        } else {
            selectedCountry.abbreviation
        }

        notice.licensePlate = trimmedOrNull(licensePlateTextField.text)

        val selectedVehicleMake = vehicleMakeComboBox.selectedObjects[0] as String
        notice.vehicleMake = if (selectedVehicleMake == "--") {
            null
        } else {
            selectedVehicleMake
        }

        val selectedColor = colorComboBox.selectedObjects[0] as VehicleColor
        notice.color = if (selectedColor.color == null) {
            null
        } else {
            selectedColor.colorName
        }

        // todo Prio 1: map addressLocation

        notice.street = trimmedOrNull(streetTextField.text)
        notice.zipCode = trimmedOrNull(zipCodeTextField.text)
        notice.town = trimmedOrNull(townTextField.text)
        notice.locationDescription = trimmedOrNull(locationDescriptionTextField.text)
        val selectedOffense = offenseComboBox.selectedItem
        notice.offense = if (selectedOffense is Offense) {
            selectedOffense.id
        } else {
            null
        }

        val format = DateTimeFormatter.ofPattern("d.M.yyyy")
        val obsDateTxt = observationDateTextField.text
        notice.observationTime = if (obsDateTxt.isBlank()) {
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

        notice.duration = intOrNull(durationTextField.text)
        notice.obstruction = obstructionCheckBox.isSelected
        notice.endangering = endangeringCheckBox.isSelected
        notice.environmentalStickerMissing = environmentalStickerCheckBox.isSelected
        notice.vehicleInspectionExpired = vehicleInspectionStickerCheckBox.isSelected
        notice.vehicleInspectionYear = if(notice.vehicleInspectionExpired) {
            shortOrNull(inspectionYearTextField.text)
        } else {
            null
        }
        notice.vehicleInspectionMonth = if(notice.vehicleInspectionExpired) {
            byteOrNull(inspectionMonthTextField.text)
        } else {
            null
        }
        notice.vehicleAbandoned = abandonedCheckBox.isSelected
        notice.recipient = trimmedOrNull(recipientTextField.text)
        notice.note = trimmedOrNull(noteTextArea.text)
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
        val notice = noticeFrame.getNotice()
        val enab = (notice != null) && !notice.isSent()
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
        if(photo.date != null) {
            updateDateTimeAndDuration()
        }
    }

    override fun unselectedPhoto(index: Int, photo: Photo) {
        if(photo.date != null) {
            updateDateTimeAndDuration()
        }
    }

    override fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        if(photos.size != 0) {
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