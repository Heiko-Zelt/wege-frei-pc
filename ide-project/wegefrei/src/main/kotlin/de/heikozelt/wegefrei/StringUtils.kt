package de.heikozelt.wegefrei

/**
 * used to compare Strings insensitive to case and diacritical marks
 * important to find "Škoda" in combo box,
 * or umlaute if not using a German keyboard
 */
fun latinLower(s: String): String {
    return s.lowercase()
        .replace('ä', 'a') // Kässbohrer, Fußgänger
        .replace('ö', 'o')
        .replace('ü', 'u') // grün, Einmündung
        .replace('ë', 'e') // Citroën
        .replace('š', 's') // Škoda
}