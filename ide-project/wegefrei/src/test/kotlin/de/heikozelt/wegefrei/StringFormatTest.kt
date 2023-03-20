package de.heikozelt.wegefrei

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.math.PI

class StringFormatTest {

    @Test
    fun formatFloatGerman() {
        val str = "%.5f".format(Locale.GERMAN, PI)
        assertEquals("3,14159", str) // comma
    }

    /**
     * Use English formatted floating point number for Open Street Map Nominatim HTTP request.
     */
    @Test
    fun formatFloatEnglish() {
        val str = "%.5f".format(Locale.ENGLISH, PI)
        assertEquals("3.14159", str) // period
    }
}