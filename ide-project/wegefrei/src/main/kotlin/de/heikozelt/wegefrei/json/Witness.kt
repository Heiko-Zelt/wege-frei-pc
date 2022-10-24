package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json

/**
 * Part of Settings
 */
class Witness (
    val email: String = "",
    val street: String = "",
    @Json(name= "zip_code")
    val zipCode: String = "",
    val town: String = "",
    @Json(name= "telephone_number")
    val telephoneNumber: String = ""
)