package de.heikozelt.wegefrei.noticeframe

import java.awt.Component
import javax.swing.JList
import javax.swing.JTextArea
import javax.swing.ListCellRenderer

class OffenseListCellRenderer : ListCellRenderer<String?>, JTextArea(2, 40) {

    override fun getListCellRendererComponent(
        list: JList<out String>?,
        value: String?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        isOpaque = true
        list?.let {
            background = if (isSelected) {
                list.selectionBackground
            } else {
                list.background
            }
            foreground = if (isSelected) {
                list.selectionForeground
            } else {
                list.foreground
            }
        }

        value?.let {
            text = it
            maximumSize = preferredSize
            lineWrap = true
            wrapStyleWord = true
        }
        return this
    }
}