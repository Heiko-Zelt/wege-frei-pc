package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.COUNTRY_SYMBOLS
import de.heikozelt.wegefrei.VEHICLE_MAKES
import de.heikozelt.wegefrei.gui.MainFrame.Companion.FORM_BACKGROUND
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.TEXT_COLOR
import de.heikozelt.wegefrei.model.SelectedPhotos
import mu.KotlinLogging
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.*
import java.awt.GridBagLayout
import javax.swing.*

class NoticeForm(private val mainFrame: MainFrame, selectedPhotos: SelectedPhotos) : JPanel() {

    private val log = KotlinLogging.logger {}
    private val miniMap = MiniMap(mainFrame)
    private var streetTextField = JTextField(30)
    private var zipCodeTextField = JTextField(5)
    private var townTextField = JTextField(30)

    init {
        selectedPhotos.registerObserver(miniMap)

        background = FORM_BACKGROUND
        border = NO_BORDER
        layout = GridBagLayout();
        val constraints = GridBagConstraints()
        constraints.anchor = WEST
        //constraints.fill = BOTH
        constraints.weightx = 0.5
        constraints.weighty = 0.1

        constraints.gridy++
        val countrySymbolLabel = JLabel("Länderkennzeichen:")
        countrySymbolLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        constraints.gridwidth = 1
        add(countrySymbolLabel, constraints)
        val countrySymbolComboBox = JComboBox(COUNTRY_SYMBOLS)
        constraints.gridx = 1
        add(countrySymbolComboBox, constraints)

        constraints.gridy++
        val licensePlateLabel = JLabel("Kfz-Kennzeichen:")
        licensePlateLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(licensePlateLabel, constraints)
        val licensePlateTextField = JTextField(10)
        //licensePlateTextField.size = Dimension(100, 20)
        constraints.gridx = 1
        add(licensePlateTextField, constraints)

        constraints.gridy++
        val vehicleMakeLabel = JLabel("Fahrzeugmarke:")
        vehicleMakeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(vehicleMakeLabel, constraints)
        val vehicleMakeComboBox = JComboBox(VEHICLE_MAKES)
        constraints.gridx = 1
        add(vehicleMakeComboBox, constraints)

        constraints.gridy++
        val colorLabel = JLabel("Farbe:")
        colorLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(colorLabel, constraints)

        val modell = DefaultComboBoxModel(LIST_COLORS)
        //modell.addAll(LIST_COLORS)
        val colorComboBox = JComboBox(modell)
        colorComboBox.renderer = ColorListCellRenderer()
        colorComboBox.maximumRowCount = LIST_COLORS.size
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
        val offenseDateTextField = JTextField(10)
        constraints.gridx = 1
        add(offenseDateTextField, constraints)

        constraints.gridy++
        val offenseTimeLabel = JLabel("Uhrzeit:")
        offenseTimeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(offenseTimeLabel, constraints)
        val offenseTimeTextField = JTextField(5)
        constraints.gridx = 1
        add(offenseTimeTextField, constraints)

        constraints.gridy++
        val durationLabel = JLabel("Dauer (in Minuten):")
        durationLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(durationLabel, constraints)
        val durationTextField = JTextField(3)
        constraints.gridx = 1
        add(durationTextField, constraints)

        constraints.gridy++
        val environmentalStickerCheckBox = JCheckBox("Umweltplakette fehlt")
        environmentalStickerCheckBox.foreground = TEXT_COLOR
        environmentalStickerCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        add(environmentalStickerCheckBox, constraints)

        val vehicleInspectionStickerCheckBox = JCheckBox("HU Plakette abgelaufen")
        vehicleInspectionStickerCheckBox.foreground = TEXT_COLOR
        vehicleInspectionStickerCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 1
        add(vehicleInspectionStickerCheckBox, constraints)

        constraints.gridy++
        val abandonedCheckBox = JCheckBox("Fahrzeug war verlassen")
        abandonedCheckBox.foreground = TEXT_COLOR
        abandonedCheckBox.background = FORM_BACKGROUND
        constraints.gridx = 0
        add(abandonedCheckBox, constraints)

        constraints.gridy++
        val recipientLabel = JLabel("Empfänger:")
        recipientLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(recipientLabel, constraints)
        val recipientTextField = JTextField(30)
        constraints.gridx = 1
        add(recipientTextField, constraints)

        constraints.gridy++
        val saveButton = JButton("speichern")
        constraints.anchor = EAST
        constraints.gridx = 0
        add(saveButton, constraints)

        val sendButton = JButton("absenden")
        constraints.anchor = WEST
        constraints.gridx = 1
        add(sendButton, constraints)

        setSize(700, 700)
        isVisible = true
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
        val LIST_COLORS = arrayOf(
            ListColor("--", null),
            ListColor("Weiß", Color.white),
            ListColor("Silber", Color(192, 192, 192)),
            ListColor("Grau", Color.gray),
            ListColor("Schwarz", Color.black),
            ListColor("Beige", Color(240, 240, 210)),
            ListColor("Gelb", Color.yellow),
            ListColor("Orange", Color.orange),
            ListColor("Gold", Color(218,165,32)),
            ListColor("Braun", Color(139,69,19)),
            ListColor("Rot", Color(240, 0,0)),
            ListColor("Grün", Color(0,200,0)),
            ListColor("Blau", Color.blue),
            ListColor("Pink", Color.pink),
            ListColor("Violett/Lila", Color(136,0,255))
        )
    }
}