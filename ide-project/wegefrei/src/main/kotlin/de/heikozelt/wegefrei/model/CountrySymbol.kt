package de.heikozelt.wegefrei.model

/**
 * Landeskennzeichen, falls vorhanden
 * entweder im alten Stil, Oval mit Länderkennzeichen
 * oder bei EU-Ländern als Teil des Kennzeichens mit EU-Flagge
 * todo Prio 2: Katalog vervollständigen, Gruppierung nach Erdteil
 * todo Prio 3: Landeskennzeichen als Icon anzeigen
 */
class CountrySymbol(val abbreviation: String, val countryName: String?) {

    override fun toString(): String {
        var txt = abbreviation
        if(countryName != null) {
            txt += " - $countryName"
        }
        return txt
    }

    companion object {
        /**
         * null -> COUNTRY_SYMBOLS[0]
         * "--" -> COUNTRY_SYMBOLS[0]
         * not found -> COUNTRY_SYMBOLS[0]
         */
        fun fromAbbreviation(abbreviation: String?): CountrySymbol {
            return if(abbreviation == null) {
                CountryComboBoxModel.COUNTRY_SYMBOLS[0]
            } else {
                val c = CountryComboBoxModel.COUNTRY_SYMBOLS.find { it.abbreviation == abbreviation }
                c ?: CountryComboBoxModel.COUNTRY_SYMBOLS[0]
            }
        }

        /**
         * "BIH - Bosnien-Herzegowina" --> "BIH"
         * "Wunderland" --> "Wunderland"
         * " " --> null
         */
        fun abbreviationFromString(text: String): String? {
            val needle = text.trim()
            return if(needle.isBlank()) {
                null
            } else {
                val symbol = CountryComboBoxModel.COUNTRY_SYMBOLS.find { needle == it.toString() }
                symbol?.abbreviation ?: needle
            }
        }
    }
}