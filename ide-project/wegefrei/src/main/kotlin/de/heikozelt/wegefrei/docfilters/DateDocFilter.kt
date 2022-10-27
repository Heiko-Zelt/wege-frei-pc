package de.heikozelt.wegefrei.docfilters

/**
 * Erlaubt die Eingabe eines Datums bestehend aus Ziffern und Punkten.
 * z.B. "31.12.1999"
 */
class DateDocFilter: CharPredicateDocFilter( { it.isDigit() || it == '.' })