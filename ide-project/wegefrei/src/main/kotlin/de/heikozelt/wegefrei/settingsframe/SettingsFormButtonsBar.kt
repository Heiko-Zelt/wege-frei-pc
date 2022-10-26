package de.heikozelt.wegefrei.settingsframe

import de.heikozelt.wegefrei.gui.Styles
import org.slf4j.LoggerFactory
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JPanel

class SettingsFormButtonsBar(settingsFrame: SettingsFrame): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        // todo: hgap or horizontalStruts?
        layout = FlowLayout(FlowLayout.RIGHT, 5,0)

        val okButton = JButton("Ok")
        okButton.margin = Styles.BUTTON_MARGIN
        okButton.addActionListener { settingsFrame.saveAndClose() }
        add(okButton)

        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));

        val cancelButton = JButton("Abbrechen")
        cancelButton.margin = Styles.BUTTON_MARGIN
        cancelButton.addActionListener { settingsFrame.discardChangesAndClose() }
        add(cancelButton)

        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));
    }
}