package de.heikozelt.wegefrei.gui

class Verifiers {

    companion object {
        /**
         * "01.01.2021" oder "1.1.2021" oder Leerstring
         */
        val dateVerifier = PatternVerifier("^(([012]?[1-9]|3[01])\\.(0?[1-9]|1[012])\\.(20\\d\\d))?$")

        /**
         * "01:01" oder "1:1" oder Leerstring
         */
        val timeVerifier = PatternVerifier("^(([01]?[0-9]|2[0-3]):([0-5]?\\d))?$")

        /**
         * todo: Prio 3: ermöglichen mehrere E-Mail-Adressen durch Komma getrennt anzugeben
         * HU-Jahr 4-stellig, entweder 19xx oder 20xx oder Leerstring.
         */
        val eMailVerifier = PatternVerifier("^(.+@.+)?$")

        /**
         * HU-Jahr 4-stellig, entweder 19xx oder 20xx oder Leerstring.
         */
        val inspectionYearVerifier = PatternVerifier("^(19\\d\\d|20\\d\\d)?$")

        /**
         * 1-12 oder 01-12 oder Leerstring
         */
        val inspectionMonthVerifier = PatternVerifier("^(0?[1-9]|1[012])?$")

        /**
         * someone@somewhere or blank
         */
        val emailAddressVerifier = PatternVerifier("^(.+@.+)|[ //t//n]*$")
    }
}