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

    fun getFullName(): String {
        return "$givenName $surname"
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