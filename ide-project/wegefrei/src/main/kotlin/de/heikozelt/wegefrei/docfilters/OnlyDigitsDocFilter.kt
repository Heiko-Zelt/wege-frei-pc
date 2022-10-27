package de.heikozelt.wegefrei.docfilters

/**
 * Erlaubt die Eingabe einer Ganzzahl bestehend aus Ziffern.
 * Alle anderen Zeichen werden ignoriert.
 */
class OnlyDigitsDocFilter: CharPredicateDocFilter({ it.isDigit() })