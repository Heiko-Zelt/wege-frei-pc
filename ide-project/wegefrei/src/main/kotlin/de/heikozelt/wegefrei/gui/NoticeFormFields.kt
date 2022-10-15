package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.docfilters.DateDocFilter
import de.heikozelt.wegefrei.docfilters.OnlyDigitsDocFilter
import de.heikozelt.wegefrei.docfilters.TimeDocFilter
import de.heikozelt.wegefrei.gui.Styles.Companion.FORM_BACKGROUND
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.TEXTFIELD_FONT
import de.heikozelt.wegefrei.model.CountrySymbol
import de.heikozelt.wegefrei.model.ListVehicleMakes
import de.heikozelt.wegefrei.model.Offense
import de.heikozelt.wegefrei.model.VehicleColor
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import java.awt.Insets
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.text.AbstractDocument

/**
 * todo: Button Meldung löschen
 */
class NoticeFormFields(private val noticeFrame: NoticeFrame) : JPanel() {

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
    private val environmentalStickerCheckBox = JCheckBox("Umweltplakette fehlt")
    private val vehicleInspectionStickerCheckBox = JCheckBox("HU Plakette abgelaufen")
    private val abandonedCheckBox = JCheckBox("Fahrzeug war verlassen")
    private val recipientTextField = TrimmingTextField(30)

    init {
        log.debug("init")
        layout = BorderLayout()

        background = FORM_BACKGROUND
        border = NO_BORDER
        layout = GridBagLayout()
        val constraints = GridBagConstraints()
        constraints.insets = Insets(0,5,0,0)
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
        val doc1 = licensePlateTextField.document
        if (doc1 is AbstractDocument) {
            doc1.documentFilter = UppercaseDocumentFilter()
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
        val streetLabel = JLabel("<html>Straße & Hausnummer.:<sup>*</sup></html>")
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
        val offenseDateLabel = JLabel("<html>Beobachtungs-Datum:<sup>*</sup></html>")
        //offenseDateLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(offenseDateLabel, constraints)
        constraints.gridx = 1
        val doc2 = observationDateTextField.document
        if (doc2 is AbstractDocument) {
            doc2.documentFilter = DateDocFilter()
        }
        observationDateTextField.toolTipText = "z.B. 31.12.2001"
        add(observationDateTextField, constraints)

        constraints.gridy++
        val offenseTimeLabel = JLabel("<html>Beobachtungs-Uhrzeit:<sup>*</sup></html>")
        //offenseTimeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(offenseTimeLabel, constraints)
        constraints.gridx = 1
        val doc3 = observationTimeTextField.document
        if (doc3 is AbstractDocument) {
            doc3.documentFilter = TimeDocFilter()
        }
        observationTimeTextField.toolTipText = "z.B. 23:59"
        add(observationTimeTextField, constraints)

        constraints.gridy++
        val durationLabel = JLabel("<html>Beobachtungs-Dauer (in Minuten):<sup>*</sup></html>")
        //durationLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(durationLabel, constraints)
        constraints.gridx = 1
        val doc4 = durationTextField.document
        if (doc4 is AbstractDocument) {
            doc4.documentFilter = OnlyDigitsDocFilter()
        }
        durationTextField.toolTipText = "Ganzzahl"
        add(durationTextField, constraints)

        constraints.gridy++
        //environmentalStickerCheckBox.foreground = TEXT_COLOR
        environmentalStickerCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        add(environmentalStickerCheckBox, constraints)

        //vehicleInspectionStickerCheckBox.foreground = TEXT_COLOR
        vehicleInspectionStickerCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 1
        add(vehicleInspectionStickerCheckBox, constraints)

        constraints.gridy++
        //abandonedCheckBox.foreground = TEXT_COLOR
        abandonedCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        add(abandonedCheckBox, constraints)

        constraints.gridy++
        val recipientLabel = JLabel("<html>Empfänger:<sup>*</sup></html>")
        //recipientLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(recipientLabel, constraints)
        constraints.gridx = 1
        recipientTextField.toolTipText = "z.B. verwarngeldstelle@wiesbaden.de"
        add(recipientTextField, constraints)

        loadNotice()

        val notice = noticeFrame.getNotice()
        if(notice.isSent()) {
            disableFormFields()
        }

        setSize(700, 700)
        isVisible = true
    }

    /**
     * Initialisieren der einzelnen Eingabe-Felder
     * Mapping von Notice zu GUI-Components
     */
    private fun loadNotice() {
        val notice = noticeFrame.getNotice()
        // macht schon MainFrame.init()
        //mainFrame.setSelectedPhotos(SelectedPhotos(TreeSet(notice.photos)))

        val countrySymbol = CountrySymbol.fromAbbreviation(notice.countrySymbol)
        countrySymbolComboBox.selectedItem = countrySymbol

        licensePlateTextField.text = notice.licensePlate

        val make = ListVehicleMakes.VEHICLE_MAKES.find { it == notice.vehicleMake }
        make?.let {
            vehicleMakeComboBox.selectedItem = make
        }

        val vehicleColor = VehicleColor.fromColorName(notice.color)
        colorComboBox.selectedItem = vehicleColor

        streetTextField.text = notice.street
        zipCodeTextField.text = notice.zipCode
        townTextField.text = notice.town
        locationDescriptionTextField.text = notice.locationDescription
        offenseComboBox.selectedItem = Offense.fromId(notice.offense)

        observationDateTextField.text = if(notice.observationTime == null) {
            ""
        } else {
            val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            fmt.format(notice.observationTime)
        }

        observationTimeTextField.text = if(notice.observationTime == null) {
            ""
        } else {
            val fmt = DateTimeFormatter.ofPattern("HH:mm")
            fmt.format(notice.observationTime)
        }

        durationTextField.text = if(notice.duration == null) {
            ""
        } else {
            notice.duration.toString()
        }

        environmentalStickerCheckBox.isSelected = notice.environmentalStickerMissing
        vehicleInspectionStickerCheckBox.isSelected = notice.vehicleInspectionExpired
        //todo Jahr und Monat

        abandonedCheckBox.isSelected = notice.vehicleAbandoned
        recipientTextField.text = notice.recipient
    }

    /**
     * Mapping der Werte der GUI-Komponenten zu Notice
     */
    // todo: form validation, Validierungsfehler bei Eingabefeldern anzeigen
    fun saveNotice() {
        val notice = noticeFrame.getNotice()
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

        // todo map addressLocation

        notice.street = trimmedOrNull(streetTextField.text)
        notice.zipCode = trimmedOrNull(zipCodeTextField.text)
        notice.town = trimmedOrNull(townTextField.text)
        notice.locationDescription = trimmedOrNull(locationDescriptionTextField.text)
        val selectedOffense = offenseComboBox.selectedItem
        notice.offense = if(selectedOffense is Offense) {
            selectedOffense.id
        } else {
            null
        }

        val format = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val obsDateTxt = observationDateTextField.text
        notice.observationTime = if(obsDateTxt.isBlank()) {
            null
        } else {
            val dat: LocalDate = LocalDate.parse(obsDateTxt, format)
            // todo Prio 2: Validierung, ob Format von Datum und Uhrzeit korrekt sind. Fehlermeldung anzeigen.
            // todo Prio 3: Problem lösen: Sommer- oder Winterzeit, eine Stunde im Jahr ist zweideutig
            // todo Prio 3: Datum/Uhrzeit darf nicht in der Zukunft liegen
            val obsTimeTxt = observationTimeTextField.text
            val tim = if(obsTimeTxt.isBlank()) {
                LocalTime.parse("00:00")
            } else {
                LocalTime.parse(obsTimeTxt)
            }
            ZonedDateTime.of(dat, tim, ZoneId.systemDefault())
        }

        val durTxt = durationTextField.text
        notice.duration = if(durTxt.isBlank()) {
            null
        } else {
            // todo: Prio 2: Validierung, ob Zahl
            durTxt.toInt()
        }

        notice.environmentalStickerMissing = environmentalStickerCheckBox.isSelected
        notice.vehicleInspectionExpired = vehicleInspectionStickerCheckBox.isSelected
        notice.vehicleAbandoned = abandonedCheckBox.isSelected
        notice.recipient = trimmedOrNull(recipientTextField.text)
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
    fun disableFormFields() {
        countrySymbolComboBox.isEnabled = false
        licensePlateTextField.isEnabled = false
        vehicleMakeComboBox.isEnabled = false
        colorComboBox.isEnabled = false
        streetTextField.isEnabled = false
        zipCodeTextField.isEnabled = false
        townTextField.isEnabled = false
        locationDescriptionTextField.isEnabled = false
        offenseComboBox.isEnabled = false
        observationDateTextField.isEnabled = false
        observationTimeTextField.isEnabled = false
        durationTextField.isEnabled = false
        environmentalStickerCheckBox.isEnabled = false
        vehicleInspectionStickerCheckBox.isEnabled = false
        abandonedCheckBox.isEnabled = false
        recipientTextField.isEnabled = false
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
    }
}