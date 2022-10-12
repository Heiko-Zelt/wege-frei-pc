package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.model.CountrySymbol
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

//class ColorListCellRenderer: JLabel(), DefaultListCellRenderer {
class CountrySymbolListCellRenderer: JLabel(), ListCellRenderer<CountrySymbol> {

    override fun getListCellRendererComponent(
        list: JList<out CountrySymbol>?,
        value: CountrySymbol?,
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
            //icon = ColorIcon(it.color)
            text = it.abbreviation
            it.countryName?.let {name ->
                text += " - $name"
            }
        }
        return this
    }
}