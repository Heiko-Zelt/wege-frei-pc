package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.App
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.TOOLBAR_BACKGROUND
import mu.KotlinLogging
import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JToolBar

class MainToolBar(private val app: App): JToolBar() {

    private val log = KotlinLogging.logger {}
    init {
        background = TOOLBAR_BACKGROUND
        isFloatable = false
        border = NO_BORDER

        val newButton = JButton("neue Meldung erfassen")
        newButton.addActionListener { MainFrame(app, Notice()) }
        add(newButton, BorderLayout.SOUTH)

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
    }

}