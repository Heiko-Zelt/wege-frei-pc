package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.App
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.gui.Styles.Companion.BUTTONS_SEPARATOR_DIMENSION
import de.heikozelt.wegefrei.gui.Styles.Companion.TOOLBAR_BACKGROUND
import de.heikozelt.wegefrei.gui.Styles.Companion.TOOLBAR_BORDER
import mu.KotlinLogging
import javax.swing.Box
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JToolBar

class NoticesToolBar(private val app: App): JToolBar() {

    private val log = KotlinLogging.logger {}
    init {
        background = TOOLBAR_BACKGROUND
        isFloatable = false
        border = TOOLBAR_BORDER

        add(Box.createHorizontalGlue())

        val newButton = JButton("neue Meldung erfassen")
        newButton.addActionListener { NoticeFrame(app, Notice()) }
        add(newButton)

        addSeparator(BUTTONS_SEPARATOR_DIMENSION)

        val scanButton = JButton()
        val scanImageURL = this::class.java.getResource("scan_icon.gif")
        scanButton.toolTipText = "Scan"
        scanButton.addActionListener { app.scanForNewPhotos() }
        if(scanImageURL != null) {
            scanButton.icon = ImageIcon(scanImageURL, "Scan")
        } else {
            scanButton.text = "Scan"
        }
        add(scanButton)

        addSeparator(BUTTONS_SEPARATOR_DIMENSION)

        val settingsButton = JButton()
        val settingsImageURL = this::class.java.getResource("settings_icon.gif")
        //button.setActionCommand(UP)
        settingsButton.toolTipText = "Einstellungen"
        settingsButton.addActionListener{ log.debug("unhandled event")}
        if(settingsImageURL != null) {
            settingsButton.icon = ImageIcon(settingsImageURL, "Einstellungen")
        } else {
            settingsButton.text = "Einstellungen"
        }
        add(settingsButton)

        addSeparator(BUTTONS_SEPARATOR_DIMENSION)

        val helpButton = JButton()
        val helpImageURL = this::class.java.getResource("help_icon.gif")
        //button.setActionCommand(UP)
        helpButton.toolTipText = "Hilfe"
        helpButton.addActionListener { log.debug("unhandled event") }
        if(helpImageURL != null) {
            helpButton.icon = ImageIcon(helpImageURL, "Hilfe")
        } else {
            helpButton.text = "Hilfe"
        }
        add(helpButton)

        addSeparator(BUTTONS_SEPARATOR_DIMENSION)
    }

}