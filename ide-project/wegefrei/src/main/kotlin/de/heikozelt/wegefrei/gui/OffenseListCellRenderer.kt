package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.model.Offense
import java.awt.Component
import javax.swing.JList
import javax.swing.JTextArea
import javax.swing.ListCellRenderer

class OffenseListCellRenderer: JTextArea(), ListCellRenderer<Offense> {

    override fun getListCellRendererComponent(
        list: JList<out Offense>?,
        value: Offense?,
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
            //text = "<html>${it.text}</html>"
            text = it.text
            //append(it.text)
        }
        /*
        val prefHeight = when(text.length) {
            in 0 until 40 -> 20
            in 40 until 80 -> 40
            in 80 until 120 -> 60
            else -> 80
        }
        this.preferredSize = Dimension(350, prefHeight)
         */

        lineWrap = true
        wrapStyleWord = true
        columns = 40
        rows = 2

        return this
    }
}