package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json

/**
 * Part of Settings
 */
data class Witness (
    @Json(name= "given_name")
    var givenName: String = "",

    @Json(name= "surname")
    var surname: String = "",

    @Json(name= "email_address")
    var emailAddress: String = "",

    var street: String = "",

    @Json(name= "zip_code")
    var zipCode: String = "",

    var town: String = "",

    @Json(name= "telephone_number")
    var telephoneNumber: String = ""
): Cloneable {

    /**
     * Liefert eine Liste mit Validierungs-Fehlermeldungen oder eine leere Liste, wenn alles ok.
     */
    fun validate(): MutableList<String>{
        val validationErrors = mutableListOf<String>()
        if (givenName.isBlank()) validationErrors.add("Dein Vorname als Zeug_in fehlt.")
        if (surname.isBlank()) validationErrors.add("Dein Nachname als Zeug_in fehlt.")
        if (emailAddress.isBlank()) validationErrors.add("Deine E-Mail-Adresse als Zeug_in fehlt.")
        if (street.isBlank()) validationErrors.add("Bei deiner Anschrift als Zeug_in fehlt die Straße und Hausnummer.")
        if (zipCode.isBlank()) validationErrors.add("Bei deiner Anschrift als Zeug_in fehlt die Postleitzahl (PLZ).")
        if (town.isBlank()) validationErrors.add("Dein Wohnort als Zeug_in fehlt.")
        if(validationErrors.size == 6) { // gar keine persönlichen Zeugen-Daten angegeben
            validationErrors.clear()
            validationErrors.add("Bitte gib Deine Zeugen-Daten an.")
        }
        return validationErrors
    }

    fun getFullName(): String {
        return "$givenName $surname"
    }

    /**
     * Es gibt 3 Arten von Adressen: Zeuge, NominatimResponse und Notice
     */
    fun getAddress(): String? {
        return if(street.isBlank() && zipCode.isBlank() && town.isBlank() ) {
            null
        } else {
            "$street, $zipCode $town"
        }
    }

    /**
     * Deep copy.
     * There is no difference to copy().
     * String values are not copied because they are immutable.
     */
    public override fun clone(): Witness {
        return Witness(
            givenName,
            surname,
            emailAddress,
            street,
            zipCode,
            town,
            telephoneNumber
        )
    }
}