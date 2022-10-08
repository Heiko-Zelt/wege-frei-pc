package de.heikozelt.wegefrei.gui

class ListCountrySymbol(val abbreviation: String, val countryName: String?) {

    companion object {
        /**
         * null -> COUNTRY_SYMBOLS[0]
         * "--" -> COUNTRY_SYMBOLS[0]
         * not found -> COUNTRY_SYMBOLS[0]
         */
        fun fromAbbreviation(abbreviation: String?): ListCountrySymbol {
            return if(abbreviation == null) {
                COUNTRY_SYMBOLS[0]
            } else {
                val c = COUNTRY_SYMBOLS.find { it.abbreviation == abbreviation }
                c ?: COUNTRY_SYMBOLS[0]
            }
        }

        val COUNTRY_SYMBOLS = arrayOf(
            ListCountrySymbol("--", null),
            ListCountrySymbol("A", "Österreich"),
            ListCountrySymbol("AL", "Albanien"),
            ListCountrySymbol("AND", "Andorra"),
            ListCountrySymbol("B", "Belgien"),
            ListCountrySymbol("BG", "Bulgarien"),
            ListCountrySymbol("BIH", "Bosnien-Herzegowina"),
            ListCountrySymbol("BY", "Belarus"),
            ListCountrySymbol("CH", "Schweiz"),
            ListCountrySymbol("CY", "Zypern"),
            ListCountrySymbol("CZ", "Tschechische Republik"),
            ListCountrySymbol("D", "Deutschland")
        )
    }
}