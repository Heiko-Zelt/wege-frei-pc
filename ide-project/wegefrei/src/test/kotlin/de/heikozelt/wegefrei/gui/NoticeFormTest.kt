package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.NoticeFormFields.Companion.trimmedOrNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class NoticeFormTest {
    @Test
    fun trimmedOrNull_null() {
       assertNull(trimmedOrNull(null))
    }

    @Test
    fun trimmedOrNull_empty() {
        assertNull(trimmedOrNull(""))
    }

    @Test
    fun trimmedOrNull_single_space() {
        assertNull(trimmedOrNull(" "))
    }

    @Test
    fun trimmedOrNull_single_newline() {
        assertNull(trimmedOrNull("\n"))
    }

    @Test
    fun trimmedOrNull_single_tab() {
        assertNull(trimmedOrNull("\t"))
    }

    @Test
    fun trimmedOrNull_spaces_before_and_after() {
        assertEquals("Hello", trimmedOrNull(" Hello "))
    }

    @Test
    fun trimmedOrNull_newline_after() {
        assertEquals("Hello", trimmedOrNull("Hello\n"))
    }

    @Test
    fun trimmedOrNull_tab_before() {
        assertEquals("Hello", trimmedOrNull("\tHello"))
    }
}