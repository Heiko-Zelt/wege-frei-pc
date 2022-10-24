package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.gui.Styles
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JPanel

/**
 * Button-Leiste unterhalb des Meldungs-Formulars
 */
class NoticeFormButtonsBar(private val noticeFrame: NoticeFrame): JPanel() {

    private val deleteButtonStruts = Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE)
    private val deleteButton = JButton("Löschen")

    init {
        //background = TOOLBAR_BACKGROUND
        //isFloatable = false
        //border = NORMAL_BORDER

        //add(Box.createHorizontalGlue())

        layout = FlowLayout(FlowLayout.RIGHT, 5,0)

        val okButton = JButton("Ok")
        okButton.margin = Styles.BUTTON_MARGIN
        //okButton.border = NORMAL_BORDER
        okButton.addActionListener { noticeFrame.saveAndClose() }
        add(okButton)

        //addSeparator(BUTTONS_SEPARATOR_DIMENSION)
        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));

        val cancelButton = JButton("Abbrechen")
        cancelButton.margin = Styles.BUTTON_MARGIN
        cancelButton.addActionListener { noticeFrame.cancelAndClose() }
        add(cancelButton)

        deleteButtonStruts.isVisible = false
        add(deleteButtonStruts);

        deleteButton.isVisible = false
        deleteButton.margin = Styles.BUTTON_MARGIN
        deleteButton.addActionListener { noticeFrame.deleteAndClose() }
        add(deleteButton)

        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));

        val sendButton = JButton("E-Mail absenden")
        sendButton.margin = Styles.BUTTON_MARGIN
        sendButton.addActionListener { noticeFrame.sendNotice() }
        add(sendButton)

        //addSeparator(BUTTONS_SEPARATOR_DIMENSION)
        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));
    }

    fun loadData(notice: Notice) {
        // Nur beim Bearbeiten einer existierenden Meldung
        // einen Lösch-Button anzeigen.
        if(notice.id != null) {
            deleteButtonStruts.isVisible = true
            deleteButton.isVisible = true
        }
    }
}