package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory
import javax.swing.JComboBox
import javax.swing.JPanel

class SettingsFormFields(private val settingsFrame: SettingsFrame): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val lookAndFeelNames = Settings.lookAndFeelNames().toTypedArray()
    private val lookAndFeelComboBox = JComboBox(lookAndFeelNames)

    init {
        add(lookAndFeelComboBox)
    }

    fun load(settings: Settings) {
        lookAndFeelComboBox.selectedItem = lookAndFeelNames.find { it == settings.lookAndFeel }
    }

    fun save(settings: Settings) {
        val selected = lookAndFeelComboBox.selectedItem
        if(selected == null) {
            log.warn("No String selected as look and feel.")
        } else {
            settings.lookAndFeel = lookAndFeelComboBox.selectedItem as String
        }
    }
}