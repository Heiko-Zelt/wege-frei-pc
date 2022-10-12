package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.model.VehicleColor
import mu.KotlinLogging
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.border.Border
import javax.swing.border.EmptyBorder
import javax.swing.table.TableCellRenderer


class NoticesTableColorCellRenderer : JLabel(), TableCellRenderer {

    private val log = KotlinLogging.logger {}

    private var noFocusBorder: Border = EmptyBorder(1, 1, 1, 1)

    init {
        super.setOpaque(false)
    }

    /**
     * Quellcode größtenteils aus DefaultCellRenderer übernommen
     */
    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        // todo Hintergrundfarbe, abhängig, ob ausgewählt
        // todo Rahmen nicht verändern, wenn Fokus auf Zelle, oder doch?
        if(table == null) {
            return this
        }

        if (isSelected) {
            log.debug("is selected")
            super.setBackground(table.selectionBackground) // r=184, g=207, b=229 also hellblau
            super.setForeground(table.selectionForeground)
        } else {
            log.debug("is not selected")
            super.setBackground(table.background) // JTable Background
            super.setForeground(table.foreground)
        }

        var b: Border? = null
        if(hasFocus) {
            if(isSelected) {
                b = UIManager.getBorder("Table.focusSelectedCellHighlightBorder")
            }
            if (b == null) {
                b = UIManager.getBorder("Table.focusCellHighlightBorder")
            }
        } else {
            b = noFocusBorder
        }
        border = b

        font = Styles.TEXTFIELD_FONT

        // If the current background is equal to the table's background, then we
        // can avoid filling the background by setting the renderer opaque.
        // Wo ist die Negation?
        //isOpaque = background != null && !(background.equals(table.background));
        super.setOpaque(true)

        if (value is VehicleColor) {
            icon = ColorIcon(value.color)
            text = value.colorName
        }
        return this
    }

}