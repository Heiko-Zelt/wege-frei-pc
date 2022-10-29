package de.heikozelt.wegefrei.gui

import java.awt.Color
import java.awt.Font
import java.awt.Insets
import javax.swing.*
import javax.swing.border.Border


/**
 * Layout-Format-Vorlagen
 */
class Styles {
    companion object {
        val TEXTFIELD_FONT = JTextField().font ?: Font("Dialog", Font.PLAIN, 12)

        const val BUTTONS_DISTANCE = 15
        const val THUMBNAIL_SIZE = 140
        const val SELECT_BUTTON_SIZE = 26
        val BUTTON_MARGIN = Insets(0, 5, 0, 5)

        val NORMAL_BORDER: Border = BorderFactory.createLineBorder(Color.black)
        val HIGHLIGHT_BORDER: Border? = BorderFactory.createLineBorder(Color.yellow)
        val NO_BORDER: Border? = BorderFactory.createEmptyBorder()

        val TEXT_COLOR: Color? = JLabel().foreground ?: Color.black

        val FRAME_BACKGROUND = JPanel().background ?: Color(238, 238, 238)
        val PHOTO_MARKER_BACKGROUND = Color(101, 162, 235)
        val ALL_PHOTOS_BACKGROUND = FRAME_BACKGROUND
        val SELECTED_PHOTOS_BACKGROUND = FRAME_BACKGROUND
        val FORM_BACKGROUND = FRAME_BACKGROUND
        val ZOOM_PANEL_BACKGROUND = FRAME_BACKGROUND
        val NOTICES_TABLE_BACKGROUND = JTable().background ?: Color(255, 255, 255)

        /*
        todo: Prio 3: programmatische Eingriffe ins Layout soweit es geht reduzieren
        todo: Prio 4: weitere coole Open Source Look'n'Feels integrieren oder eigenes Look'n'Feel

        val NORMAL_BORDER: Border = BorderFactory.createLineBorder(Color.black)
        val HIGHLIGHT_BORDER: Border? = BorderFactory.createLineBorder(Color.yellow)
        val NO_BORDER: Border? = BorderFactory.createEmptyBorder()
        val TOOLBAR_BORDER: Border? = BorderFactory.createEmptyBorder()

        val TEXT_COLOR: Color? = Color.white

        val FRAME_BACKGROUND = Color(50, 50, 50)
        val PHOTO_MARKER_BACKGROUND = Color(101, 162, 235)
        val TOOLBAR_BACKGROUND = Color(50, 50, 50)
        val ALL_PHOTOS_BACKGROUND = Color(20, 20, 20)
        val SELECTED_PHOTOS_BACKGROUND = Color(50, 50, 50)
        val FORM_BACKGROUND = Color(20, 20, 20)
        val ZOOM_PANEL_BACKGROUND = Color(35, 35, 35)
        val NOTICES_BACKGROUND = FORM_BACKGROUND
         */
    }
}