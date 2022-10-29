package de.heikozelt.wegefrei.noticesframe

import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.gui.Styles.Companion.BUTTONS_DISTANCE
import de.heikozelt.wegefrei.gui.Styles.Companion.BUTTON_MARGIN
import de.heikozelt.wegefrei.settingsframe.SettingsFrame
import org.slf4j.LoggerFactory
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JPanel

class NoticesButtonsBar(private val app: WegeFrei): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    // todo Prio 2: set null again, when closed
    private var settingsFrame: SettingsFrame? = null
    init {
        layout = FlowLayout(FlowLayout.RIGHT, 5,0)

        log.debug("layout: $layout")

        val newButton = JButton("neue Meldung erfassen")
        newButton.margin = BUTTON_MARGIN
        newButton.addActionListener { app.openNoticeFrame() }
        add(newButton)

        add(Box.createHorizontalStrut(BUTTONS_DISTANCE))

        val scanButton = JButton()
        scanButton.margin = BUTTON_MARGIN
        val scanImageURL = this::class.java.getResource("scan_icon.gif")
        scanButton.toolTipText = "Scan"
        scanButton.addActionListener { app.scanForNewPhotos() }
        if(scanImageURL != null) {
            scanButton.icon = ImageIcon(scanImageURL, "Scan")
        } else {
            scanButton.text = "Scan"
        }
        add(scanButton)

        add(Box.createHorizontalStrut(BUTTONS_DISTANCE))

        val settingsButton = JButton()
        settingsButton.margin = BUTTON_MARGIN
        val settingsImageURL = this::class.java.getResource("settings_icon.gif")
        //button.setActionCommand(UP)
        settingsButton.toolTipText = "Einstellungen"
        settingsButton.addActionListener{ app.openSettingsFrame() }
        if(settingsImageURL != null) {
            settingsButton.icon = ImageIcon(settingsImageURL, "Einstellungen")
        } else {
            settingsButton.text = "Einstellungen"
        }
        add(settingsButton)

        add(Box.createHorizontalStrut(BUTTONS_DISTANCE))

        val helpButton = JButton()
        helpButton.margin = BUTTON_MARGIN
        val helpImageURL = this::class.java.getResource("help_icon.gif")
        helpButton.toolTipText = "Hilfe"
        helpButton.addActionListener { log.debug("unhandled event") }
        if(helpImageURL != null) {
            helpButton.icon = ImageIcon(helpImageURL, "Hilfe")
        } else {
            helpButton.text = "Hilfe"
        }
        add(helpButton)

        add(Box.createHorizontalStrut(BUTTONS_DISTANCE))
    }

}