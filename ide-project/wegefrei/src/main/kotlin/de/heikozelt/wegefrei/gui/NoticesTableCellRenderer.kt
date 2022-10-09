package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.model.ListColor
import mu.KotlinLogging
import java.awt.Component
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer


class NoticesTableCellRenderer : JLabel(), TableCellRenderer {

    private val log = KotlinLogging.logger {}

    init {
        super.setOpaque(true)
    }

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        font = font.deriveFont(font.getStyle() and Font.BOLD.inv())
        if (value is ListColor) {
            icon = ColorIcon(value.color)
            text = value.colorName
        }
        return this
    }

}