package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.COLORS
import de.heikozelt.wegefrei.COUNTRY_SYMBOLS
import de.heikozelt.wegefrei.VEHICLE_MAKES
import de.heikozelt.wegefrei.gui.MainFrame.Companion.FORM_BACKGROUND
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.TEXT_COLOR
import mu.KotlinLogging
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import javax.swing.*

class NoticeForm(private val mainFrame: MainFrame): JPanel() {

    private val log = KotlinLogging.logger {}
    private val miniMap: MiniMap

    init {
        background = FORM_BACKGROUND
        border = NO_BORDER
        layout = GridBagLayout();
        val constraints = GridBagConstraints()
        constraints.anchor = WEST
        constraints.fill = BOTH
        constraints.weightx=0.5
        constraints.weighty=0.1

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
        val licensePlateTextField = JTextField()
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
        val colorComboBox = JComboBox(COLORS)
        constraints.gridx = 1
        add(colorComboBox, constraints)

        constraints.gridy++
        val coordinatesLabel = JLabel("Koordinaten:")
        coordinatesLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(coordinatesLabel, constraints)
        constraints.gridx = 1
        constraints.weighty= 1.0
        miniMap = MiniMap(mainFrame)
        add(miniMap, constraints)

        constraints.gridy++
        val streetLabel = JLabel("Straße, Hausnr:")
        streetLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        constraints.weighty=0.1
        add(streetLabel, constraints)
        val streetTextField = JTextField()
        constraints.gridx = 1
        add(streetTextField, constraints)

        constraints.gridy++
        val zipCodeLabel = JLabel("PLZ:")
        zipCodeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(zipCodeLabel, constraints)
        val zipCodeTextField = JTextField()
        constraints.gridx = 1
        add(zipCodeTextField, constraints)

        constraints.gridy++
        val townLabel = JLabel("Ort:")
        townLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(townLabel, constraints)
        val townTextField = JTextField()
        constraints.gridx = 1
        add(townTextField, constraints)

        constraints.gridy++
        val offenseDateLabel = JLabel("Datum:")
        offenseDateLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(offenseDateLabel, constraints)
        val offenseDateTextField = JTextField()
        constraints.gridx = 1
        add(offenseDateTextField, constraints)

        constraints.gridy++
        val offenseTimeLabel = JLabel("Uhrzeit:")
        offenseTimeLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(offenseTimeLabel, constraints)
        val offenseTimeTextField = JTextField()
        constraints.gridx = 1
        add(offenseTimeTextField, constraints)

        constraints.gridy++
        val durationLabel = JLabel("Dauer:")
        durationLabel.foreground = TEXT_COLOR
        constraints.gridx = 0
        add(durationLabel, constraints)
        val durationTextField = JTextField()
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
        val b = JButton("click")
        constraints.gridx = 0
        add(b, constraints)

        setSize(700, 700)
        isVisible = true
    }
    fun getMiniMap(): MiniMap {
        return miniMap
    }
}