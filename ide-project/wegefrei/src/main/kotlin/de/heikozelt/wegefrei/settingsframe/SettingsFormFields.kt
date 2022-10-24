package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.json.Settings
import javax.swing.JComboBox
import javax.swing.JPanel

class SettingsFormFields(private val settingsFrame: SettingsFrame): JPanel() {

    private val lookAndFeelComboBox = JComboBox(Settings.lookAndFeelNames().toTypedArray())

    init {
        add(lookAndFeelComboBox)
    }
}