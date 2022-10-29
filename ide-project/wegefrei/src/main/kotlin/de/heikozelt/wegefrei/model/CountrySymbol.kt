package de.heikozelt.wegefrei.model

/**
 * Landeskennzeichen, falls vorhanden
 * entweder im alten Stil, Oval mit Länderkennzeichen
 * oder bei EU-Ländern als Teil des Kennzeichens mit EU-Flagge
 * todo Prio 2: Katalog vervollständigen, Gruppierung nach Erdteil
 * todo Prio 3: Landeskennzeichen als Icon anzeigen
 */
class CountrySymbol(val abbreviation: String, val countryName: String?) {

    companion object {
        /**
         * null -> COUNTRY_SYMBOLS[0]
         * "--" -> COUNTRY_SYMBOLS[0]
         * not found -> COUNTRY_SYMBOLS[0]
         */
        fun fromAbbreviation(abbreviation: String?): CountrySymbol {
            return if(abbreviation == null) {
                COUNTRY_SYMBOLS[0]
            } else {
                val c = COUNTRY_SYMBOLS.find { it.abbreviation == abbreviation }
                c ?: COUNTRY_SYMBOLS[0]
            }
        }

        val COUNTRY_SYMBOLS = arrayOf(
            CountrySymbol("--", null),
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