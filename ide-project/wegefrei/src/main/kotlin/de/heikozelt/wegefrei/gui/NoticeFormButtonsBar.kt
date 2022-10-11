package de.heikozelt.wegefrei.gui

import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JPanel

/**
 * Button-Leiste unterhalb des Meldungs-Formulars
 */
class NoticeFormButtonsBar(private val noticeFrame: NoticeFrame): JPanel() {
    init {
        //background = TOOLBAR_BACKGROUND
        //isFloatable = false
        //border = NORMAL_BORDER

        //add(Box.createHorizontalGlue())

        layout = FlowLayout(FlowLayout.RIGHT, 5,0)

        val okButton = JButton("Ok")
        okButton.margin = Styles.BUTTON_MARGIN
        //okButton.border = NORMAL_BORDER
        okButton.addActionListener {
            noticeFrame.saveNotice()
            noticeFrame.isVisible = false
            noticeFrame.dispose()
        }
        add(okButton)

        //addSeparator(BUTTONS_SEPARATOR_DIMENSION)
        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));

        val cancelButton = JButton("Abbrechen")
        cancelButton.margin = Styles.BUTTON_MARGIN
        cancelButton.addActionListener {
            noticeFrame.isVisible = false
            noticeFrame.dispose()
        }
        add(cancelButton)


        // Nur beim Bearbeiten einer existierenden Meldung
        // einen Lösch-Button anzeigen.
        if(noticeFrame.getNotice().id != null) {
            //addSeparator(BUTTONS_SEPARATOR_DIMENSION)
            add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));

            val deleteButton = JButton("Löschen")
            deleteButton.margin = Styles.BUTTON_MARGIN
            deleteButton.addActionListener {
                noticeFrame.deleteNotice()
                noticeFrame.isVisible = false
                noticeFrame.dispose()
            }
            add(deleteButton)
        }

        //addSeparator(BUTTONS_SEPARATOR_DIMENSION)
        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));

        val sendButton = JButton("E-Mail absenden")
        sendButton.margin = Styles.BUTTON_MARGIN
        sendButton.addActionListener { /* todo Meldung löschen */ }
        add(sendButton)

        //addSeparator(BUTTONS_SEPARATOR_DIMENSION)
        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));
    }
}