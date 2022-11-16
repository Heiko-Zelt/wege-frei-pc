package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Offense
import java.awt.Component
import javax.swing.JList
import javax.swing.JTextArea
import javax.swing.ListCellRenderer

class OffenseListCellRenderer: ListCellRenderer<Offense> {

    init {
        /*
        component.apply {
            isOpaque = true
            lineWrap = true
            wrapStyleWord = true
        }

         */
    }

    override fun getListCellRendererComponent(
        list: JList<out Offense>?,
        value: Offense?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {

        val component = JTextArea(2, 40)
        value?.let {

            component.text = it.text
            component.maximumSize= component.preferredSize
            component.apply {
                isOpaque = true
                lineWrap = true
                wrapStyleWord = true
            }
        }
        return component
    }
}