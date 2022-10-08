package de.heikozelt.wegefrei.gui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class NoticeFormTest {
    @Test
    fun trimmedOrNull_null() {
       assertNull(NoticeForm.trimmedOrNull(null))
    }

    @Test
    fun trimmedOrNull_empty() {
        assertNull(NoticeForm.trimmedOrNull(""))
    }

    @Test
    fun trimmedOrNull_single_space() {
        assertNull(NoticeForm.trimmedOrNull(" "))
    }

    @Test
    fun trimmedOrNull_single_newline() {
        assertNull(NoticeForm.trimmedOrNull("\n"))
    }

    @Test
    fun trimmedOrNull_single_tab() {
        assertNull(NoticeForm.trimmedOrNull("\t"))
    }

    @Test
    fun trimmedOrNull_spaces_before_and_after() {
        assertEquals("Hello", NoticeForm.trimmedOrNull(" Hello "))
    }

    @Test
    fun trimmedOrNull_newline_after() {
        assertEquals("Hello", NoticeForm.trimmedOrNull("Hello\n"))
    }

    @Test
    fun trimmedOrNull_tab_before() {
        assertEquals("Hello", NoticeForm.trimmedOrNull("\tHello"))
    }
}