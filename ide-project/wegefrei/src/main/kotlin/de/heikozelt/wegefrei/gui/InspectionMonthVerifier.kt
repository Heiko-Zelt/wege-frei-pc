package de.heikozelt.wegefrei.gui

/**
 * todo: Prio 3: ermöglichen mehrere E-Mail-Adressen durch Komma getrennt anzugeben
 * HU-Jahr 4-stellig, entweder 19xx oder 20xx oder Leerstring.
 */
class InspectionMonthVerifier : PatternVerifier("^(0?[1-9]|1[012])?$")
