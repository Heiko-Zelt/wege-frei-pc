package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JScrollPane

class SettingsFrame(private val app: WegeFrei): JFrame() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var settings: Settings? = null
    private val settingsFormFields = SettingsFormFields(this)
    private val settingsFormButtonBar = SettingsFormButtonsBar(this)
    private var settingsFormFieldsScrollPane = JScrollPane(settingsFormFields)

    init {
        layout = BorderLayout()
        add(settingsFormFieldsScrollPane, BorderLayout.CENTER)
        add(settingsFormButtonBar, BorderLayout.SOUTH)
        setSize(600, 600)
        isVisible = true
    }

    fun setSettings(settings: Settings) {
        this.settings = settings
    }

    fun saveAndClose() {
        isVisible = false
        dispose()
        settings?.save()
        app.setSettings(settings)
        app.settingsFrameClosed()
    }

    fun cancelAndClose() {
        isVisible = false
        dispose()
        app.settingsFrameClosed()
    }
}