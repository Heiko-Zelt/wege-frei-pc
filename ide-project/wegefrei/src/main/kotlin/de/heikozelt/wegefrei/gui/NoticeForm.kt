package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.MainFrame.Companion.FORM_BACKGROUND
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.TEXT_COLOR
import de.heikozelt.wegefrei.model.ListColor
import de.heikozelt.wegefrei.model.ListCountrySymbol
import de.heikozelt.wegefrei.model.ListVehicleMakes
import mu.KotlinLogging
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.*
import java.awt.GridBagLayout
import java.time.*
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.text.AbstractDocument

/**
 * todo: Button Meldung löschen
 */
class NoticeForm(private val mainFrame: MainFrame) : JPanel() {

    private val log = KotlinLogging.logger {}
    private val countrySymbolComboBox = JComboBox(ListCountrySymbol.COUNTRY_SYMBOLS)
    private val licensePlateTextField = JTextField(10)
    private val vehicleMakeComboBox = JComboBox(ListVehicleMakes.VEHICLE_MAKES)
    private val colorComboBox = JComboBox(ListColor.COLORS)
    private val miniMap = MiniMap(mainFrame)
    private var streetTextField = JTextField(30)
    private var zipCodeTextField = JTextField(5)
    private var townTextField = JTextField(30)
    private val offenseDateTextField = JTextField(10)
    private val offenseTimeTextField = JTextField(5)
    private val durationTextField = JTextField(3)
    private val environmentalStickerCheckBox = JCheckBox("Umweltplakette fehlt")
    private val vehicleInspectionStickerCheckBox = JCheckBox("HU Plakette abgelaufen")
    private val abandonedCheckBox = JCheckBox("Fahrzeug war verlassen")
    private val recipientTextField = JTextField(30)

    init {
        background = FORM_BACKGROUND
        border = NO_BORDER
        layout = GridBagLayout();
        val constraints = GridBagConstraints()
        constraints.anchor = WEST
        //constraints.fill = BOTH
        constraints.weightx = 0.5
        constraints.weighty = 0.1

        constraints.gridy++
        val countrySymbolLabel = JLabel("Landeskennzeichen:")
        countrySymbolLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        constraints.gridwidth = 1
        add(countrySymbolLabel, constraints)
        constraints.gridx = 1
        countrySymbolComboBox.renderer = CountrySymbolListCellRenderer()
        add(countrySymbolComboBox, constraints)

        constraints.gridy++
        val licensePlateLabel = JLabel("Kfz-Kennzeichen:")
        licensePlateLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(licensePlateLabel, constraints)
        constraints.gridx = 1
        val doc = licensePlateTextField.document
        if (doc is AbstractDocument) {
            doc.documentFilter = UppercaseDocumentFilter()
        }
        add(licensePlateTextField, constraints)

        constraints.gridy++
        val vehicleMakeLabel = JLabel("Fahrzeugmarke:")
        vehicleMakeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(vehicleMakeLabel, constraints)
        constraints.gridx = 1
        add(vehicleMakeComboBox, constraints)

        constraints.gridy++
        val colorLabel = JLabel("Farbe:")
        colorLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(colorLabel, constraints)
        //val modell = DefaultComboBoxModel(COLORS)

        colorComboBox.renderer = ColorListCellRenderer()
        colorComboBox.maximumRowCount = ListColor.COLORS.size
        constraints.gridx = 1
        add(colorComboBox, constraints)

        constraints.gridy++
        val coordinatesLabel = JLabel("Koordinaten:")
        coordinatesLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(coordinatesLabel, constraints)
        constraints.gridx = 1
        constraints.weighty = 1.0
        add(miniMap, constraints)

        constraints.gridy++
        val streetLabel = JLabel("Straße, Hausnr:")
        streetLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        constraints.weighty = 0.1
        add(streetLabel, constraints)
        constraints.gridx = 1
        add(streetTextField, constraints)

        constraints.gridy++
        val zipCodeLabel = JLabel("PLZ:")
        zipCodeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(zipCodeLabel, constraints)
        constraints.gridx = 1
        add(zipCodeTextField, constraints)

        constraints.gridy++
        val townLabel = JLabel("Ort:")
        townLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(townLabel, constraints)
        constraints.gridx = 1
        add(townTextField, constraints)

        constraints.gridy++
        val offenseDateLabel = JLabel("Datum:")
        offenseDateLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(offenseDateLabel, constraints)

        constraints.gridx = 1
        add(offenseDateTextField, constraints)

        constraints.gridy++
        val offenseTimeLabel = JLabel("Uhrzeit:")
        offenseTimeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(offenseTimeLabel, constraints)

        constraints.gridx = 1
        add(offenseTimeTextField, constraints)

        constraints.gridy++
        val durationLabel = JLabel("Dauer (in Minuten):")
        durationLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(durationLabel, constraints)
        constraints.gridx = 1
        add(durationTextField, constraints)

        constraints.gridy++
        environmentalStickerCheckBox.foreground = TEXT_COLOR
        environmentalStickerCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        add(environmentalStickerCheckBox, constraints)

        vehicleInspectionStickerCheckBox.foreground = TEXT_COLOR
        vehicleInspectionStickerCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 1
        add(vehicleInspectionStickerCheckBox, constraints)

        constraints.gridy++
        abandonedCheckBox.foreground = TEXT_COLOR
        abandonedCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        add(abandonedCheckBox, constraints)

        constraints.gridy++
        val recipientLabel = JLabel("Empfänger:")
        recipientLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(recipientLabel, constraints)
        constraints.gridx = 1
        add(recipientTextField, constraints)

        constraints.gridy++
        val saveButton = JButton("speichern")
        saveButton.addActionListener { mainFrame.saveNotice() }
        constraints.anchor = EAST
        constraints.gridx = 0
        add(saveButton, constraints)

        val sendButton = JButton("absenden")
        constraints.anchor = WEST
        constraints.gridx = 1
        add(sendButton, constraints)

        loadNotice()

        setSize(700, 700)
        isVisible = true
    }

    /**
     * Initialisieren der einzelnen Eingabe-Felder
     * Mapping von Notice zu GUI-Components
     */
    private fun loadNotice() {
        val notice = mainFrame.getNotice()
        // macht schon MainFrame.init()
        //mainFrame.setSelectedPhotos(SelectedPhotos(TreeSet(notice.photos)))

        val listCountrySymbol = ListCountrySymbol.fromAbbreviation(notice.countrySymbol)
        countrySymbolComboBox.selectedItem = listCountrySymbol

        licensePlateTextField.text = notice.licensePlate

        val make = ListVehicleMakes.VEHICLE_MAKES.find { it == notice.vehicleMake }
        make?.let {
            vehicleMakeComboBox.selectedItem = make
        }

        val listColor = ListColor.fromColorName(notice.color)
        colorComboBox.selectedItem = listColor

        streetTextField.text = notice.street
        zipCodeTextField.text = notice.zipCode
        townTextField.text = notice.town

        offenseDateTextField.text = if(notice.date == null) {
            ""
        } else {
            val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            fmt.format(notice.date)
        }

        offenseTimeTextField.text = if(notice.date == null) {
            ""
        } else {
            val fmt = DateTimeFormatter.ofPattern("HH:mm")
            fmt.format(notice.date)
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
        val notice = mainFrame.getNotice()
        notice.photos = mainFrame.getSelectedPhotos().getPhotos()

        val selectedCountry = countrySymbolComboBox.selectedObjects[0] as ListCountrySymbol
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

        val selectedColor = colorComboBox.selectedObjects[0] as ListColor
        notice.color = if (selectedColor.color == null) {
            null
        } else {
            selectedColor.colorName
        }

        // todo map addressLocation

        notice.street = trimmedOrNull(streetTextField.text)
        notice.zipCode = trimmedOrNull(zipCodeTextField.text)
        notice.town = trimmedOrNull(townTextField.text)

        // todo: Validierung, ob Datum oder Blank/Null
        val format = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val dat: LocalDate = LocalDate.parse(offenseDateTextField.text, format)
        // todo: Validierung, ob Uhrzeit
        // todo Problem lösen: Sommer- oder Winterzeit, eine Stunde im Jahr ist zweideutig
        val tim = LocalTime.parse(offenseTimeTextField.text)
        val datTim = ZonedDateTime.of(dat, tim, ZoneId.systemDefault())
        notice.date = datTim
        //notice.date = Date.from(datTim.atZone(ZoneId.systemDefault()).toInstant())

        // todo: Validierung, ob Zahl
        notice.duration = (trimmedOrNull(durationTextField.text) as String).toInt()
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