package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json

/**
 * Part of Settings
 */
class Witness (
    var email: String = "",

    var street: String = "",

    @Json(name= "zip_code")
    var zipCode: String = "",

    var town: String = "",

    @Json(name= "telephone_number")
    var telephoneNumber: String = ""
)