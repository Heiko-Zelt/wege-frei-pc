package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JScrollPane

/**
 * Einstellungen-Fenster
 */
class SettingsFrame(private val app: WegeFrei): JFrame() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var settings: Settings? = null
    private val settingsFormFields = SettingsFormFields(this)
    private val settingsFormButtonBar = SettingsFormButtonsBar(this)
    private var settingsFormFieldsScrollPane = JScrollPane(settingsFormFields)

    init {
        title = "Einstellungen - Wege frei!"
        layout = BorderLayout()
        add(settingsFormFieldsScrollPane, BorderLayout.CENTER)
        add(settingsFormButtonBar, BorderLayout.SOUTH)
        setSize(600, 600)
        isVisible = true
    }

    /**
     * Settings-Objekt auf Formular-Felder abbilden
     */
    fun setSettings(settings: Settings?) {
        log.debug("setSettings()")
        settings?.let {
            this.settings = it
            settingsFormFields.load(it)
        }
    }

    /**
     * Formular-Eingabe speichern und Fenster schließen
     * todo Wie JUnit-testen, wenn Settings-Dateiname hardcoded ist?
     */
    fun saveAndClose() {
        log.debug("saveAndClose()")
        settings?.let {
            isVisible = false
            dispose()
            settingsFormFields.save(it)
            app.settingsChanged(it)
            app.settingsFrameClosed()
        }
    }

    /**
     * Fenster schließen ohne zu speichern
     */
    fun cancelAndClose() {
        log.debug("cancelAndClose()")
        isVisible = false
        dispose()
        app.settingsFrameClosed()
    }
}