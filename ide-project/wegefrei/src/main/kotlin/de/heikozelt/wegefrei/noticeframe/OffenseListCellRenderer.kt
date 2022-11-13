package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Offense
import java.awt.Component
import javax.swing.JList
import javax.swing.JTextArea
import javax.swing.ListCellRenderer

class OffenseListCellRenderer: ListCellRenderer<Offense> {
    val component = JTextArea(2, 40)

    init {
        component.apply {
            isOpaque = true
            lineWrap = true
            wrapStyleWord = true
        }
    }

    override fun getListCellRendererComponent(
        list: JList<out Offense>?,
        value: Offense?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {

        value?.let {
            component.text = it.text
        }
        return component
    }
}