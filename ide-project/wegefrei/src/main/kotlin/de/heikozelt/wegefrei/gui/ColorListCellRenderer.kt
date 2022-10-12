package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.model.VehicleColor
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

//class ColorListCellRenderer: JLabel(), DefaultListCellRenderer {
class ColorListCellRenderer: JLabel(), ListCellRenderer<VehicleColor> {

    override fun getListCellRendererComponent(
        list: JList<out VehicleColor>?,
        value: VehicleColor?,
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