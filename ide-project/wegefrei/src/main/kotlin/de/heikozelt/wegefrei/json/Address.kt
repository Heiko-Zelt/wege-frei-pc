package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json

/**
 * Teil von NominatimResponse
 */
class Address(
    @Json(name="house_number") val houseNumber: String?,
    val road: String?,
    val city: String?,
    val country: String?,
    val postcode: String?
)