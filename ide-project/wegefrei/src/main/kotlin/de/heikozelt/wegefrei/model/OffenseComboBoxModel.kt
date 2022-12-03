package de.heikozelt.wegefrei.model

import org.slf4j.LoggerFactory
import java.lang.Math.min
import javax.swing.DefaultComboBoxModel

/**
 * Ordnungswidrigkeiten
 * todo Prio 3: Verkehrsschilder / Pictogramme hinzufügen
 */
class OffenseComboBoxModel: DefaultComboBoxModel<String>() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val filteredList = mutableListOf<String>()

    init {
        filteredList.addAll(OFFENSES)
    }

    /**
     * needed for autocomplete/filtered combo box
     * @param: syllable part of a word
     */
    fun setFilter(syllable: String) {
        selectedItem = syllable
        log.debug("setFilter(syllable=$syllable)")
        val oldSize = filteredList.size
        val normalized = syllable.lowercase()
        filteredList.clear()
        filteredList.addAll(OFFENSES.filter {normalized in it.lowercase() })
        val newSize = filteredList.size
        log.debug("newSize = $newSize")
        //EventQueue.invokeLater {
            if (newSize != 0 || oldSize != 0) fireContentsChanged(this, 0, min(oldSize, newSize) - 1)
            if (newSize > oldSize) fireIntervalAdded(this, oldSize, newSize - 1)
            if (newSize < oldSize) fireIntervalRemoved(this, newSize, oldSize - 1)
        //}
    }

    override fun getSize(): Int {
        return filteredList.size
    }

    override fun getElementAt(index: Int): String {
        return filteredList[index]
    }

    companion object {
        fun longestText(): String {
            return OFFENSES.maxBy { it.length }
        }
        /**
         * Anzeige-Reihenfolge in der ComboBox wie hier in diesem Array.
         */
        val OFFENSES = arrayOf(
            "",
            "Sonstiges Vergehen (siehe Hinweis)",
            "Halten/Parken auf unbeschilderten Radweg",
            "Halten/Parken auf Fußgängerüberweg",
            "Halten/Parken auf beschilderten Radweg",
            "Halten/Parken auf gemeinsamen Geh- und Radweg",
            "Halten/Parken auf Fahrradstraße",
            "Halten/Parken auf Schutzstreifen",
            "Halten/Parken auf Gehweg",
            "Halten/Parken in verkehrsberuhigten Bereich außerhalb zum Parken gekennzeichneter Fläche",
            "Halten/Parken in Fußgängerzone",
            "Halten/Parken weniger als 5 Meter vor Fußgängerüberweg (Zebrastreifen)",
            "Halten/Parken weniger als 8 Meter vor Kreuzung/Einmündung, obwohl rechts neben der Fahrbahn ein Radweg baulich angelegt ist",
            "Parken weniger als 5 Meter vor/hinter Kreuzung/Einmündung",
            "Halten/Parken im absoluten Haltverbot",
            "Parken im eingeschränkten Haltverbot",
            "Halten/Parken auf Sperrfläche",
            "Halten/Parken an einer unübersichtlichen Stelle",
            "Halten/Parken in scharfer Kurve",
            "Halten/Parken in zweiter Reihe",
            "Halten/Parken näher als 10 Meter vor einem Lichtzeichen",
            "Halten/Parken vor oder in amtlich gekennzeichneter Feuerwehrzufahrt",
            "Halten/Parken verbotswidrig im Bereich eines Taxenstandes",
            "Parken verbotswidrig und verhinderte dadurch die Benutzung gekennzeichneter Parkflächen",
            "Parken im Bereich einer Grundstücksein/ausfahrt",
            "Parken auf einer schmalen Fahrbahn gegenüber einer Grundstücksein/ausfahrt",
            "Parken vor einer Bordsteinabsenkung",
            "Parken verbotswidrig auf der linken Fahrbahnseite",
            "Parken verbotswidrig auf dem linken Seitenstreifen",
            "Parken nicht am rechten Fahrbahnrand",
            "Parken im Fahrraum von Schienenfahrzeugen",
            "Parken links von einer Fahrbahnbegrenzung",
            "Parken in einem Verkehrsbereich, der durch Verkehrszeichen gesperrt war",
            "Parken auf einem durch Richtungspfeile gekennzeichneten Fahrbahnteil",
            "Parken innerhalb einer Grenzmarkierung für ein Haltverbot",
            "Parken näher als 10 m vor Andreaskreuz",
            "Parken näher als 10 m vor Vorfahrt-gewähren-Zeichen und verdeckte dieses",
            "Parken innerhalb eines Kreisverkehrs",
            "Parken in einem Abstand von weniger als 15 m von einem Haltestellenschild",
            "Parken auf Grenzmarkierung für ein Parkverbot",
            "Halten/Parken bei zulässigem Gehwegparken nicht auf dem Gehweg",
            "Halten/Parken auf Sonderfahrstreifen für Omnibusse des Linienverkehrs",
            "Parken auf gekennzeichnetem Behindertenparkplatz",
            "Parken mit Verbrenner vor Elektroladesäule",
            "Halten/Parken auf Grünfläche",
            "Halten/Parken auf einer Autobahn/Schnellstraße",
            "Parken auf Gehweg mit zulässigem Gesamtgewicht über 2,8 t"
        )
    }
}