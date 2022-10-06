package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.MainFrame.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NORMAL_BORDER
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

//class ColorListCellRenderer: JLabel(), DefaultListCellRenderer {
class ColorListCellRenderer: JLabel(), ListCellRenderer<ListColor> {

    override fun getListCellRendererComponent(
        list: JList<out ListColor>?,
        value: ListColor?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        isOpaque = true
        //background = value?.color
        /*
        if(isSelected) {
            text = "<$text>"
            border = HIGHLIGHT_BORDER
        }
         */
        value?.let {
            icon = ColorIcon(it.color)
            text = it.colorName
        }
        return this
    }
}