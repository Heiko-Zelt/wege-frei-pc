package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json

/**
 * Teil von NominatimResponse
 */
class NominatimAddress(
    @Json(name = "house_number")
    val houseNumber: String? = null,
    val road: String? = null,
    val city: String? = null,
    val town: String? = null,
    val country: String? = null,
    val postcode: String? = null
) {

    /**
     * @return city or town name whatever is present
     */
    fun getCityOrTown(): String? {
        return city ?: town
    }

    /**
     * @return the street or street and house number if present
     */
    fun getStreetAndHouseNumber(): String? {
        return if (road == null) {
            return null
        } else {
            var street = road
            houseNumber?.let {
                street += " $it"
            }
            street
        }
    }
}