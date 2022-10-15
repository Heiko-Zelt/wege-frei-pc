package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.model.Offense
import java.awt.Component
import javax.swing.JList
import javax.swing.JTextArea
import javax.swing.ListCellRenderer

class OffenseListCellRenderer: JTextArea(2, 40), ListCellRenderer<Offense> {

    override fun getListCellRendererComponent(
        list: JList<out Offense>?,
        value: Offense?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        isOpaque = true
        value?.let {
            text = it.text
        }
        lineWrap = true
        wrapStyleWord = true
        return this
    }
}