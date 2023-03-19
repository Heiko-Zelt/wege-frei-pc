package de.heikozelt.wegefrei.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class NoticeEntityTest {

    @Test
    fun getAddress_null() {
        val n = NoticeEntity()
        assertNull(n.getAddress())
    }

    @Test
    fun getAddress_perfect() {
       val n = NoticeEntity()
       n.street = "Bahnhofstraße 1"
       n.zipCode = "12345"
       n.town = "Musterstadt"
       assertEquals("Bahnhofstraße 1, 12345 Musterstadt", n.getAddress())
    }

    @Test
    fun getAddress_only_street() {
        val n = NoticeEntity()
        n.street = "Bahnhofstraße 1"
        assertEquals("Bahnhofstraße 1", n.getAddress())
    }

    @Test
    fun getAddress_only_zipCode() {
        val n = NoticeEntity()
        n.zipCode = "12345"
        assertEquals("12345", n.getAddress())
    }

    @Test
    fun getAddress_only_town() {
        val n = NoticeEntity()
        n.town = "Musterstadt"
        assertEquals("Musterstadt", n.getAddress())
    }

    @Test
    fun getAddress_street_and_zipCode() {
        val n = NoticeEntity()
        n.zipCode = "12345"
        n.town = "Musterstadt"
        assertEquals("12345 Musterstadt", n.getAddress())
    }

    @Test
    fun getAddress_street_and_town() {
        val n = NoticeEntity()
        n.street = "Bahnhofstraße 1"
        n.town = "Musterstadt"
        assertEquals("Bahnhofstraße 1, Musterstadt", n.getAddress())
    }

    @Test
    fun getAddress_town_and_zipCode() {
        val n = NoticeEntity()
        n.zipCode = "12345"
        n.town = "Musterstadt"
        assertEquals("12345 Musterstadt", n.getAddress())
    }
}