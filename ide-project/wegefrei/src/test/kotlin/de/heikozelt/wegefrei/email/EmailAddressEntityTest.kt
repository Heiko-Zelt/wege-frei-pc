package de.heikozelt.wegefrei.email

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class EmailAddressEntityTest {

    @Test
    fun testEquals1() {
        val e1 = EmailAddressEntity("junit@test.de")
        val e2 = EmailAddressEntity("junit@test.de")
        assertEquals(e1, e2)
    }

    @Test
    fun testEquals2() {
        val e1 = EmailAddressEntity("junit@test.de", "Martin Müller")
        val e2 = EmailAddressEntity("junit@test.de", "Martin Müller")
        assertEquals(e1, e2)
    }

    @Test
    fun testEquals3() {
        val e1 = EmailAddressEntity("junit@test.de", "Martin Müller")
        assertEquals(e1, e1)
    }

    @Test
    fun testNotEquals1() {
        val e1 = EmailAddressEntity("junit1@test.de")
        val e2 = EmailAddressEntity("junit2@test.de")
        assertNotEquals(e1, e2)
    }

    @Test
    fun testNotEquals2() {
        val e1 = EmailAddressEntity("junit@test.de", "Martin Meier")
        val e2 = EmailAddressEntity("junit@test.de", "Martin Müller")
        assertNotEquals(e1, e2)
    }

    @Test
    fun testNotEquals3() {
        val e1 = EmailAddressEntity("junit@test.de")
        val e2 = EmailAddressEntity("junit@test.de", "Martin Müller")
        assertNotEquals(e1, e2)
    }

    @Test
    fun testNotEquals4() {
        val e1 = EmailAddressEntity("junit@test.de", "Martin Meier")
        val e2 = EmailAddressEntity("junit@test.de")
        assertNotEquals(e1, e2)
    }

    @Test
    fun testNotEquals5() {
        val e1 = EmailAddressEntity("junit@test.de", "Martin Meier")
        val e2: EmailAddressEntity? = null
        assertNotEquals(e1, e2)
    }

    @Test
    fun testNotEquals6() {
        val e1 = EmailAddressEntity("junit@test.de")
        val e2: EmailAddressEntity? = null
        assertNotEquals(e1, e2)
    }
}