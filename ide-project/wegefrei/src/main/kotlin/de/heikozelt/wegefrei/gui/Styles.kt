package de.heikozelt.wegefrei.gui

import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.border.Border

/**
 * Layout-Format-Vorlagen
 */
class Styles {
    companion object {
        val NORMAL_BORDER: Border? = BorderFactory.createLineBorder(Color.black)
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

        val BUTTONS_SEPARATOR_DIMENSION = Dimension(20, 20)
    }
}