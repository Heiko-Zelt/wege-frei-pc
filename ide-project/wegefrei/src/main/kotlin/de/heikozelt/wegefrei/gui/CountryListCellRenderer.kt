package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.model.CountrySymbol
import org.slf4j.LoggerFactory
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.border.Border
import javax.swing.border.EmptyBorder


//class ColorListCellRenderer: JLabel(), DefaultListCellRenderer {
class CountryListCellRenderer: JLabel(), ListCellRenderer<CountrySymbol?> {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var noFocusBorder: Border = EmptyBorder(1, 1, 1, 1)

    override fun getListCellRendererComponent(
        list: JList<out CountrySymbol>?,
        value: CountrySymbol?,
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
            //isEnabled = list.isEnabled;
            //font = list.font;
        }

        /*
        border = if (cellHasFocus) {
            UIManager.getBorder("List.focusCellHighlightBorder")
        }  else {
            noFocusBorder
        }
         */

        value?.let {
            //todo ovales Schild oder Europaflagge + Buchstabe icon = CountrySymbolIcon(it.abbreviation)
            var txt = it.abbreviation.ifEmpty { " " }
            it.countryName?.let {name ->
                txt += " - $name"
            }
            text = txt
        }
        return this
    }

    companion object {
        // wozu?
        class UIResource : DefaultListCellRenderer(), javax.swing.plaf.UIResource
    }
}