package de.heikozelt.wegefrei.model

/**
 * Falls ein Vergehen aus dem Katalog gestrichen wird,
 * dann soll es nicht mehr zur Auswahl stehen.
 * Es muss aber Zwecks Erhaltung der referenziellen Integrität von Altdaten im Katalog verbleiben.
 * selectable wird dann auf false gesetzt.
 * Falls ein Vergehen wesentlich umbenannt wird,
 * sollte ggf. ein neues Vergehen angelegt werden
 * und das alte auf selectable=false gesetzt werden.
 */
class Offense(val id: Int, val text: String, val selectable: Boolean) {

    companion object {

        /**
         * Factory-Methode
         */
        fun fromId(id: Int?): Offense {
            val offense = OFFENSES.find { it.id == id}
            return offense ?: OFFENSES[0]
        }

        fun withLongestText(): Offense {
            return OFFENSES.maxBy { it.text.length }
        }

        /**
         * zur Anzeige in der DropDownBox
         */
        fun selectableOffenses(): Array<Offense> {
            val offenses = OFFENSES.filter {it.selectable}
            return offenses.toTypedArray()
        }

        /**
         * used by autocomplete/filtered combo box
         * @param: filter in lower case
         */
        fun offensesFiltered(filter: String): List<Offense> {
            val lowercaseFilter = filter.lowercase()
            return OFFENSES.filter { (lowercaseFilter in it.text.lowercase()) && it.selectable}
        }

        /**
         * Anzeige-Reihenfolge in der ComboBox wie hier in diesem Array.
         */
        val OFFENSES = arrayOf(
            Offense(0,"--", true),
            Offense(1,"Sonstiges Vergehen (siehe Hinweis)", true),
            Offense(2,"Halten/Parken auf unbeschildertem Radweg", true),
            Offense(3,"Halten/Parken auf Fußgängerüberweg", true),
            Offense(4,"Halten/Parken auf beschilderten Radweg", true),
            Offense(5,"Halten/Parken auf gemeinsamen Geh- und Radweg", true),
            Offense(6,"Halten/Parken auf Fahrradstraße", true),
            Offense(7,"Halten/Parken auf Schutzstreifen", true),
            Offense(8,"Halten/Parken auf Gehweg", true),
            Offense(9,"Halten/Parken in verkehrsberuhigten Bereich außerhalb zum Parken gekennzeichneter Fläche", true),
            Offense(10,"Halten/Parken in Fußgängerzone", true),
            Offense(11,"Halten/Parken weniger als 5 Meter vor Fußgängerüberweg (Zebrastreifen)", true),
            Offense(12, "Halten/Parken weniger als 8 Meter vor Kreuzung/Einmündung, obwohl rechts neben der Fahrbahn ein Radweg baulich angelegt ist", true),
            Offense(13,"Halten/Parken weniger als 5 Meter vor/hinter Kreuzung/Einmündung", true),
            Offense(14,"Halten/Parken im absoluten Haltverbot", true),
            Offense(15,"Parken im eingeschränkten Haltverbot", true),
            Offense(16,"Halten/Parken auf Sperrfläche", true),
            Offense(17,"Halten/Parken in scharfer Kurve", true),
            Offense(18,"Halten/Parken in zweiter Reihe", true),
            Offense(19,"Halten/Parken näher als 10 Meter vor einem Lichtzeichen", true),
            Offense(20,"Halten/Parken vor oder in amtlich gekennzeichneter Feuerwehrzufahrt", true),
            Offense(21,"Halten/Parken im Bereich eines Taxenstandes", true),
            Offense(22,"Halten/Parken verbotswidrig und verhinderte dadurch die Benutzung gekennzeichneter Parkflächen", true),
            Offense(23,"Halten/Parken im Bereich Grundstückseinfahrt bzw. ausfahrt", true),
            Offense(24,"Halten/Parken auf schmaler Fahrbahn gegenüber Grundstückseinfahrt bzw. ausfahrt", true),
            Offense(25,"Halten/Parken vor Bordsteinabsenkung", true),
            Offense(26,"Halten/Parken auf linker Fahrbahnseite/linken Seitenstreifen", true)
            //todo Prio 3: Katalog der Vergehen vervollständigen
        )
    }

}