package de.heikozelt.wegefrei.adapter.cologne

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CologneAdapterTest {

    @Test
    fun convertLicensePlate_normal() {
        assertEquals("MZ-XY1234", CologneAdapter.convertLicensePlate("MZ XY 1234"))
    }

    @Test
    fun convertLicensePlate_cologne() {
        assertEquals("MZ-XY1234", CologneAdapter.convertLicensePlate("MZ-XY1234"))
    }

    @Test
    fun convertLicensePlate_polish() {
        assertEquals("WY 12345", CologneAdapter.convertLicensePlate("WY 12345"))
    }

    @Test
    fun convertLicensePlate_french() {
        assertEquals("AB-123-BC", CologneAdapter.convertLicensePlate("AB-123-BC"))
    }


}