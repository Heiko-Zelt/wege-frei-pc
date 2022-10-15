package de.heikozelt.wegefrei.gui

/**
 * HU-Jahr 4-stellig, entweder 19xx oder 20xx oder Leerstring.
 */
class InspectionYearVerifier : PatternVerifier("^(19\\d\\d|20\\d\\d)?$")
