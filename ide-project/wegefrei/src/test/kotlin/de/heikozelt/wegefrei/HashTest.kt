package de.heikozelt.wegefrei

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HashTest {

    @Test
    fun hex() {
        val bytes = "ABC".toByteArray()
        val hx = hex(bytes)
        assertEquals("414243", hx)
    }

    @Test
    fun sha1() {
        val bytes = sha1("src/test/resources/feuerwehrzufahrt1.jpg")
        val hex = hex(bytes)
        assertEquals("8d0d656f3e383b5641e7e5f9c618520557c910ff", hex)
    }
}