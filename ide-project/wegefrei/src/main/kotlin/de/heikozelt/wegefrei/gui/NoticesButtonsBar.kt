package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.App
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.gui.Styles.Companion.BUTTONS_DISTANCE
import de.heikozelt.wegefrei.gui.Styles.Companion.BUTTON_MARGIN
import mu.KotlinLogging
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JPanel

class NoticesButtonsBar(private val app: App): JPanel() {

    private val log = KotlinLogging.logger {}
    init {
        //background = TOOLBAR_BACKGROUND
        //isFloatable = false
        //border = TOOLBAR_BORDER

        layout = FlowLayout(FlowLayout.RIGHT, 5,0)

        log.debug("layout " + layout)
        //JToolBar: javax.swing.plaf.synth.SynthToolBarUI$SynthToolBarLayoutManager
        //JPanel: java.awt.FlowLayout[hgap=5,vgap=5,align=center]

        //add(Box.createHorizontalGlue())

        val newButton = JButton("neue Meldung erfassen")
        newButton.margin = BUTTON_MARGIN

        newButton.addActionListener {
            val newNotice = Notice()
            NoticeFrame(app, newNotice)
        }
        add(newButton)

        //addSeparator(BUTTONS_SEPARATOR_DIMENSION)
        add(Box.createHorizontalStrut(BUTTONS_DISTANCE));
        //add(BUTTONS_SEPARATOR)

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

        //add(Box.createHorizontalStrut(25));
        add(Box.createHorizontalStrut(BUTTONS_DISTANCE));
        //add(BUTTONS_SEPARATOR)

        val settingsButton = JButton()
        settingsButton.margin = BUTTON_MARGIN
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

        //add(Box.createHorizontalStrut(25));
        add(Box.createHorizontalStrut(BUTTONS_DISTANCE));
        //add(BUTTONS_SEPARATOR)

        val helpButton = JButton()
        helpButton.margin = BUTTON_MARGIN
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

        //add(Box.createHorizontalStrut(25));
        add(Box.createHorizontalStrut(BUTTONS_DISTANCE));
        //add(BUTTONS_SEPARATOR)
    }

}