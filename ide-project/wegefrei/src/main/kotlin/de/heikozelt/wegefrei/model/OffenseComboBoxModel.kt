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
            "Halten/Parken auf unbeschildertem Radweg",
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
            "Halten/Parken weniger als 5 Meter vor/hinter Kreuzung/Einmündung",
            "Halten/Parken im absoluten Haltverbot",
            "Parken im eingeschränkten Haltverbot",
            "Halten/Parken auf Sperrfläche",
            "Halten/Parken in scharfer Kurve",
            "Halten/Parken in zweiter Reihe",
            "Halten/Parken näher als 10 Meter vor einem Lichtzeichen",
            "Halten/Parken vor oder in amtlich gekennzeichneter Feuerwehrzufahrt",
            "Halten/Parken im Bereich eines Taxenstandes",
            "Halten/Parken verbotswidrig und verhinderte dadurch die Benutzung gekennzeichneter Parkflächen",
            "Halten/Parken im Bereich Grundstückseinfahrt bzw. ausfahrt",
            "Halten/Parken auf schmaler Fahrbahn gegenüber Grundstückseinfahrt bzw. ausfahrt",
            "Halten/Parken vor Bordsteinabsenkung",
            "Halten/Parken auf linker Fahrbahnseite/linken Seitenstreifen"
            //todo Prio 3: Katalog der Vergehen vervollständigen
        )
    }
}