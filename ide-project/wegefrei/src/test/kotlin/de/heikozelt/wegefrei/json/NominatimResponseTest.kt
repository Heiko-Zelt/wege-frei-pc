package de.heikozelt.wegefrei.json

import com.beust.klaxon.Klaxon
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class NominatimResponseTest {

    @Test
    fun address_with_city() {
        val path = "src/test/resources/nominatimResponse_with_city.json"
        val file = File(path)
        val text = file.readText()
        val nominatimResponse = Klaxon().parse<NominatimResponse>(text)
        assertEquals("Rheinstraße 42", nominatimResponse?.nominatimAddress?.getStreetAndHouseNumber())
        assertEquals("65185", nominatimResponse?.nominatimAddress?.postcode)
        assertEquals("Wiesbaden", nominatimResponse?.nominatimAddress?.getCityOrTown())
    }

    @Test
    fun address_with_town_and_without_housenumber() {
        val path = "src/test/resources/nominatimResponse_with_town.json"
        val file = File(path)
        val text = file.readText()
        val nominatimResponse = Klaxon().parse<NominatimResponse>(text)
        assertEquals("Schulstraße", nominatimResponse?.nominatimAddress?.getStreetAndHouseNumber())
        assertEquals("65479", nominatimResponse?.nominatimAddress?.postcode)
        assertEquals("Raunheim", nominatimResponse?.nominatimAddress?.getCityOrTown())
    }
}