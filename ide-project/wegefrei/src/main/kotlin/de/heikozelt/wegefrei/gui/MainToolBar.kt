package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.TOOLBAR_BACKGROUND
import de.heikozelt.wegefrei.log
import de.heikozelt.wegefrei.scanForNewPhotos
import mu.KotlinLogging
import java.awt.Color
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JToolBar

class MainToolBar: JToolBar() {

    private val log = KotlinLogging.logger {}
    init {
        background = TOOLBAR_BACKGROUND
        isFloatable = false
        border = NO_BORDER

        val scanButton = JButton()
        val scanImageURL = this::class.java.getResource("scan_icon.gif")
        //button.setActionCommand(UP)
        scanButton.toolTipText = "Scan"
        scanButton.addActionListener { scanForNewPhotos() }
        if(scanImageURL != null) {
            scanButton.icon = ImageIcon(scanImageURL, "Scan")
        } else {
            scanButton.text = "Scan"
        }
        add(scanButton)

        val indexButton = JButton()
        val indexImageURL = this::class.java.getResource("index_icon.gif")
        //button.setActionCommand(UP)
        indexButton.toolTipText = "Übersicht"
        indexButton.addActionListener{ log.debug("unhandled event")}
        if(indexImageURL != null) {
            indexButton.icon = ImageIcon(indexImageURL, "Übersicht")
        } else {
            indexButton.text = "Übersicht"
        }
        add(indexButton)

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