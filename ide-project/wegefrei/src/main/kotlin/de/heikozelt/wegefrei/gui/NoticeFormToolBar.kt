package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.Styles.Companion.BUTTONS_SEPARATOR_DIMENSION
import de.heikozelt.wegefrei.gui.Styles.Companion.TOOLBAR_BACKGROUND
import de.heikozelt.wegefrei.gui.Styles.Companion.TOOLBAR_BORDER
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JToolBar

/**
 * Button-Leiste unterhalb des Meldungs-Formulars
 */
class NoticeFormToolBar(private val noticeFrame: NoticeFrame): JToolBar() {
    init {
        background = TOOLBAR_BACKGROUND
        isFloatable = false
        border = TOOLBAR_BORDER

        add(Box.createHorizontalGlue())

        val okButton = JButton("Ok")
        okButton.addActionListener {
            noticeFrame.saveNotice()
            noticeFrame.isVisible = false
            noticeFrame.dispose()
        }
        add(okButton)

        addSeparator(BUTTONS_SEPARATOR_DIMENSION)

        val cancelButton = JButton("Abbrechen")
        cancelButton.addActionListener {
            noticeFrame.isVisible = false
            noticeFrame.dispose()
        }
        add(cancelButton)

        addSeparator(BUTTONS_SEPARATOR_DIMENSION)

        // nur beim Bearbeiten einer existierenden Meldung
        val deleteButton = JButton("Löschen")
        deleteButton.addActionListener {
            noticeFrame.deleteNotice()
            noticeFrame.isVisible = false
            noticeFrame.dispose()
        }
        add(deleteButton)

        addSeparator(BUTTONS_SEPARATOR_DIMENSION)

        val sendButton = JButton("E-Mail absenden")
        sendButton.addActionListener { /* todo Meldung löschen */ }
        add(sendButton)

        addSeparator(BUTTONS_SEPARATOR_DIMENSION)
    }
}