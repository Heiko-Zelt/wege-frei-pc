package de.heikozelt.wegefrei.model

import org.slf4j.LoggerFactory
import java.lang.Math.min
import javax.swing.DefaultComboBoxModel

/**
 * Fahrzeugmarken
 * todo Prio 3: Logos als Icon hinzufügen
 * todo Prio 3: "--" anzeigen aber null speichern, siehe VehicleColor
 */
class CountryComboBoxModel: DefaultComboBoxModel<CountrySymbol>() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val filteredCountrySymbols = mutableListOf<CountrySymbol>()

    init {
        filteredCountrySymbols.addAll(COUNTRY_SYMBOLS)
    }

    /**
     * needed for autocomplete/filtered combo box
     * @param: syllable part of a word
     */
    fun setFilter(syllable: String) {
        selectedItem = syllable
        log.debug("setFilter(syllable=$syllable)")
        val oldSize = filteredCountrySymbols.size
        val normalized = syllable.lowercase()
        filteredCountrySymbols.clear()
        filteredCountrySymbols.addAll(COUNTRY_SYMBOLS.filter {
            normalized in it.abbreviation.lowercase() || normalized in (it.countryName?.lowercase()?:"")
        })
        val newSize = filteredCountrySymbols.size
        log.debug("newSize = $newSize")
        //EventQueue.invokeLater {
            if (newSize != 0 || oldSize != 0) fireContentsChanged(this, 0, min(oldSize, newSize) - 1)
            if (newSize > oldSize) fireIntervalAdded(this, oldSize, newSize - 1)
            if (newSize < oldSize) fireIntervalRemoved(this, newSize, oldSize - 1)
        //}
    }

    override fun getSize(): Int {
        return filteredCountrySymbols.size
    }

    override fun getElementAt(index: Int): CountrySymbol {
        return filteredCountrySymbols[index]
    }

    companion object {
        val COUNTRY_SYMBOLS = arrayOf(
            CountrySymbol("", null),
            CountrySymbol("A", "Österreich"),
            CountrySymbol("AL", "Albanien"),
            CountrySymbol("AND", "Andorra"),
            CountrySymbol("B", "Belgien"),
            CountrySymbol("BG", "Bulgarien"),
            CountrySymbol("BIH", "Bosnien-Herzegowina"),
            CountrySymbol("BY", "Belarus"),
            CountrySymbol("CH", "Schweiz"),
            CountrySymbol("CY", "Zypern"),
            CountrySymbol("CZ", "Tschechische Republik"),
            CountrySymbol("D", "Deutschland")
        )
    }

}