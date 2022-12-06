package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

/**
 * Einstellungen-Fenster
 * besteht grob aus vielen Formularfeldern in einer ScrollPane und 2 Buttons am unteren Rand
 */
class SettingsFrame(private val app: WegeFrei) : JFrame() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var originalSettings: Settings? = null
    private val settingsFormFields = SettingsFormFields()
    private var settingsFormFieldsScrollPane = JScrollPane(settingsFormFields)

    init {
        log.debug("init")
        val okButton = JButton("Ok")
        okButton.addActionListener { saveAndClose() }
        val cancelButton = JButton("Abbrechen")
        cancelButton.addActionListener { discardChangesAndClose() }

        title = "Einstellungen - Wege frei!"

        val lay = GroupLayout(contentPane)
        lay.autoCreateGaps = false
        lay.autoCreateContainerGaps = false
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(settingsFormFieldsScrollPane)
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            GroupLayout.PREFERRED_SIZE,
                            Int.MAX_VALUE
                        )
                        .addComponent(okButton)
                        .addComponent(cancelButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(settingsFormFieldsScrollPane)
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(okButton)
                        .addComponent(cancelButton)
                )
        )
        lay.linkSize(SwingConstants.HORIZONTAL, okButton, cancelButton)
        layout = lay

        minimumSize = Dimension(250, 250)
        //pack()
        //size = Dimension(width + 100, height + 50)
        size = Dimension(preferredSize.width + 120, preferredSize.height + 40)
        //setSize(600, 600)

        isVisible = true
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        //addWindowListener(SettingsWindowListener(this))

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                mayAskMayClose()
            }
        })
    }

    /**
     * Settings-Objekt auf Formular-Felder abbilden
     */
    fun setSettings(settings: Settings) {
        log.debug("setSettings()")
        originalSettings = settings
        settingsFormFields.setSettings(settings)
    }

    /**
     * Formular-Eingabe speichern und Fenster schließen
     * (Speichern ist eigentlich nur nötig, wenn sich etwas geändert hat,
     * aber der Wille des Benutzers hat Vorrang)
     */
    fun saveAndClose() {
        log.debug("saveAndClose()")
        val settings = (originalSettings?.clone())?:Settings()
        isVisible = false
        dispose()
        app.settingsFrameClosed()
        settingsFormFields.updateSettings(settings)
        if(settings != originalSettings) {
            app.settingsChanged(settings)
        }
    }

    /**
     * Fenster schließen ohne zu speichern
     */
    private fun discardChangesAndClose() {
        log.debug("discardChangesAndClose()")
        isVisible = false
        dispose()
        app.settingsFrameClosed()
    }

    fun mayAskMayClose() {
        log.debug("mayAskMayClose()")

        val settings = originalSettings?:Settings()
        settingsFormFields.updateSettings(settings)

        if (settings == originalSettings) {
             discardChangesAndClose()
        } else {
             askAndMayClose()
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