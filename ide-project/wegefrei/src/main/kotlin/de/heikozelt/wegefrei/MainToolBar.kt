package de.heikozelt.wegefrei

import log
import scanForNewImages
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JToolBar

class MainToolBar: JToolBar(), ActionListener {
    init {
        val scanButton = JButton()
        val scanImageURL = this::class.java.getResource("scan_icon.gif")
        //button.setActionCommand(UP)
        scanButton.toolTipText = "Scan"
        scanButton.addActionListener(this)
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
        //button.addActionListener(this)
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
        //button.addActionListener(this)
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
        //button.addActionListener(this)
        if(helpImageURL != null) {
            helpButton.icon = ImageIcon(helpImageURL, "Hilfe")
        } else {
            helpButton.text = "Hilfe"
        }
        add(helpButton)
    }

    override fun actionPerformed(p0: ActionEvent?) {
        when(p0?.actionCommand) {
            "Scan" -> scanForNewImages()
            else -> log.debug("unhandled event")
        }
    }
}