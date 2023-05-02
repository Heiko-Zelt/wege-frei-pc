package de.heikozelt.wegefrei.model

/**
 * Bidirektionale Abbildung von Zeilennummer zu Primärschlüssel und umgekehrt.
 * Der Typ des Primärschlüssels ist generisch.
 * Die IDs sind absteigend sortiert. Neueste/höchste fortlaufende Nummer oben.
 */
class RowNumIdMapDesc<T> {
    // sehr einfache Datenstruktur, triviale Implementierung
    private val ids = mutableListOf<T>()

    /**
     * Voraussetzung: newIds sind absteigend sortiert
     */
    fun replaceAll(newIds: List<T>) {
        ids.clear()
        ids.addAll(newIds)
    }

    /**
     * fügt eine ID am Anfang ein
     */
    fun push(id: T) {
        ids.add(0, id)
    }

    /**
     * löscht einen Eintrag
     */
    fun delete(rowNum: Int) {
        ids.removeAt(rowNum)
    }

    fun idByRowNum(rowNum: Int): T {
        return ids[rowNum]
    }

    fun rowNumById(id: T): Int {
        // Optimierung denkbar, HashMap oder binäre Suche statt full list scan
        return ids.indexOf(id)
    }

    fun getSize(): Int {
        return ids.size
    }
}