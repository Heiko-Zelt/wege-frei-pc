package de.heikozelt.wegefrei.delivery.cologne

import de.heikozelt.wegefrei.delivery.webform.cologne.Veedel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VeedelTest {

    @Test
    fun normalizeString_space_slash_space() {
        assertEquals("hallo welt", Veedel.normalizeString("Hallo / Welt"))
    }

    @Test
    fun normalizeString_comma() {
        assertEquals("hallo welt", Veedel.normalizeString("Hallo, Welt"))
    }

    @Test
    fun normalizeString_nothing_special() {
        assertEquals("hallo welt", Veedel.normalizeString("Hallo Welt"))
    }

    @Test
    fun convertQuarter_space_lowercase() {
        assertEquals("Altstadt/Nord", Veedel.convertQuarter("altstadt nord"))
    }

    @Test
    fun convertQuarter_umlaut() {
        assertEquals("Müngersdorf", Veedel.convertQuarter("Müngersdorf"))
    }

    @Test
    fun convertQuarter_does_not_exist() {
        assertEquals("Erbenheim", Veedel.convertQuarter("Erbenheim"))
    }

    @Test
    fun convertQuarter_space_slash_space() {
        assertEquals("Altstadt/Nord", Veedel.convertQuarter("Altstadt / Nord"))
    }
}