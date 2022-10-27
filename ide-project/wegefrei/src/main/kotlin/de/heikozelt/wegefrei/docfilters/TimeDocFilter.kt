package de.heikozelt.wegefrei.docfilters

/**
 * Erlaubt die Eingabe einer Uhrzeit.
 * Alle anderen Zeichen werden verworfen.
 * z.B. "23:59"
 */
class TimeDocFilter: CharPredicateDocFilter({ it.isDigit() || it == ':' })