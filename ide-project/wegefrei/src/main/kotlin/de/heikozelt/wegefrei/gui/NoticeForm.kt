package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.COLORS
import de.heikozelt.wegefrei.COUNTRY_SYMBOLS
import de.heikozelt.wegefrei.VEHICLE_MAKES
import mu.KotlinLogging
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import javax.swing.*

class NoticeForm: JPanel() {

    private val log = KotlinLogging.logger {}

    init {
        layout = GridBagLayout();
        val constraints = GridBagConstraints()
        constraints.anchor = WEST
        constraints.fill = BOTH
        constraints.weightx=0.5
        constraints.weighty=0.1

        constraints.gridy++
        val countrySymbolLabel = JLabel("Länderkennzeichen:")
        constraints.gridx = 0
        constraints.gridwidth = 1
        add(countrySymbolLabel, constraints)
        val countrySymbolComboBox = JComboBox(COUNTRY_SYMBOLS)
        constraints.gridx = 1
        add(countrySymbolComboBox, constraints)

        constraints.gridy++
        val licensePlateLabel = JLabel("Kfz-Kennzeichen:")
        constraints.gridx = 0
        add(licensePlateLabel, constraints)
        val licensePlateTextField = JTextField()
        constraints.gridx = 1
        add(licensePlateTextField, constraints)

        constraints.gridy++
        val vehicleMakeLabel = JLabel("Fahrzeugmarke:")
        constraints.gridx = 0
        add(vehicleMakeLabel, constraints)
        val vehicleMakeComboBox = JComboBox(VEHICLE_MAKES)
        constraints.gridx = 1
        add(vehicleMakeComboBox, constraints)

        constraints.gridy++
        val colorLabel = JLabel("Farbe:")
        constraints.gridx = 0
        add(colorLabel, constraints)
        val colorComboBox = JComboBox(COLORS)
        constraints.gridx = 1
        add(colorComboBox, constraints)

        constraints.gridy++
        val coordinatesLabel = JLabel("Koordinaten:")
        constraints.gridx = 0
        add(coordinatesLabel, constraints)
        constraints.gridx = 1
        constraints.weighty= 1.0
        add(MiniMap(), constraints)

        constraints.gridy++
        val streetLabel = JLabel("Straße, Hausnr:")
        constraints.gridx = 0
        constraints.weighty=0.1
        add(streetLabel, constraints)
        val streetTextField = JTextField()
        constraints.gridx = 1
        add(streetTextField, constraints)

        constraints.gridy++
        val zipCodeLabel = JLabel("PLZ:")
        constraints.gridx = 0
        add(zipCodeLabel, constraints)
        val zipCodeTextField = JTextField()
        constraints.gridx = 1
        add(zipCodeTextField, constraints)

        constraints.gridy++
        val townLabel = JLabel("Ort:")
        constraints.gridx = 0
        add(townLabel, constraints)
        val townTextField = JTextField()
        constraints.gridx = 1
        add(townTextField, constraints)

        constraints.gridy++
        val offenseDateLabel = JLabel("Datum:")
        constraints.gridx = 0
        add(offenseDateLabel, constraints)
        val offenseDateTextField = JTextField()
        constraints.gridx = 1
        add(offenseDateTextField, constraints)

        constraints.gridy++
        val offenseTimeLabel = JLabel("Uhrzeit:")
        constraints.gridx = 0
        add(offenseTimeLabel, constraints)
        val offenseTimeTextField = JTextField()
        constraints.gridx = 1
        add(offenseTimeTextField, constraints)

        constraints.gridy++
        val durationLabel = JLabel("Dauer:")
        constraints.gridx = 0
        add(durationLabel, constraints)
        val durationTextField = JTextField()
        constraints.gridx = 1
        add(durationTextField, constraints)

        constraints.gridy++
        val environmentalStickerCheckBox = JCheckBox("Umweltplakette fehlt")
        constraints.gridx = 0
        add(environmentalStickerCheckBox, constraints)

        val vehicleInspectionStickerCheckBox = JCheckBox("HU Plakette abgelaufen")
        constraints.gridx = 1
        add(vehicleInspectionStickerCheckBox, constraints)

        constraints.gridy++
        val abandonedCheckBox = JCheckBox("Fahrzeug war verlassen")
        constraints.gridx = 0
        add(abandonedCheckBox, constraints)

        constraints.gridy++
        val b = JButton("click")
        constraints.gridx = 0
        add(b, constraints)

        setSize(700, 700)
        isVisible = true
    }

}