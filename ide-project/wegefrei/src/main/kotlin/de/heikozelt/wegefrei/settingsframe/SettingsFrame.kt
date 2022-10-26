package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.WindowConstants

/**
 * Einstellungen-Fenster
 */
class SettingsFrame(private val app: WegeFrei) : JFrame() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var originalSettings: Settings? = null
    private var settings: Settings? = null
    private val settingsFormFields = SettingsFormFields(this)
    private val settingsFormButtonBar = SettingsFormButtonsBar(this)
    private var settingsFormFieldsScrollPane = JScrollPane(settingsFormFields)


    init {
        log.debug("init")
        title = "Einstellungen - Wege frei!"
        layout = BorderLayout()
        add(settingsFormFieldsScrollPane, BorderLayout.CENTER)
        add(settingsFormButtonBar, BorderLayout.SOUTH)
        setSize(600, 600)
        isVisible = true
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE;
        addWindowListener(SettingsWindowListener(this))
    }

    /**
     * Settings-Objekt auf Formular-Felder abbilden
     */
    fun setSettings(settings: Settings?) {
        log.debug("setSettings()")
        originalSettings = settings
        originalSettings?.let {
            settingsFormFields.load(it)
        }
    }

    /**
     * Formular-Eingabe speichern und Fenster schließen
     * (Speichern ist eigentlich nur nötig, wenn sich etwas geändert hat,
     * aber der Wille des Benutzers hat Vorrang)
     */
    fun saveAndClose() {
        log.debug("saveAndClose()")
        if(settings == null) {
            settings = Settings()
        }

        isVisible = false
        dispose()
        app.settingsFrameClosed()

        settings?.let {
            settingsFormFields.save(it)
            app.settingsChanged(it)
        }
    }

    /**
     * Fenster schließen ohne zu speichern
     */
    fun discardChangesAndClose() {
        log.debug("discardChangesAndClose()")
        isVisible = false
        dispose()
        app.settingsFrameClosed()
    }

    fun mayAskMayClose() {
        log.debug("mayAskMayClose()")

        if(settings == null) {
            settings = Settings()
        }
        settings?.let {
            settingsFormFields.save(it)
        }

        settings?.let {
            if (it == originalSettings) {
                discardChangesAndClose()
            } else {
                askAndMayClose()
            }
        }
    }

    private fun askAndMayClose() {
        log.debug("askAndMayClose()")
        //val op = JOptionPane("Änderungen speichern?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION)
        //JOptionPane.showMessageDialog(this, "Änderungen speichern?", "Einstellungen schließen", JOptionPane.QUESTION_MESSAGE)
        val result = JOptionPane.showOptionDialog(
            this,
            "Änderungen speichern?",
            "Einstellungen schließen",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            null
        )
        log.debug("result: $result")
        when (result) {
            JOptionPane.YES_OPTION -> {
                log.debug("yes -> save and close")
                saveAndClose()
            }

            JOptionPane.NO_OPTION -> {
                log.debug("no -> discard changes and close")
                discardChangesAndClose()
            }

            JOptionPane.CANCEL_OPTION -> {
                log.debug("cancel -> do nothing")
            }
        }
    }
}