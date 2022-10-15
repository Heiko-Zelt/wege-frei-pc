package de.heikozelt.wegefrei.gui

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.swing.JTextField


class DateVerifiersTest {

    private val source = JTextField()
    private val target = JTextField()
    private val verifier = Verifiers.dateVerifier

    @Test
    fun verify_correct_date1() {
        source.text = "01.02.2021"
        assertTrue(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_correct_date2() {
        source.text = "1.2.2021"
        assertTrue(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_correct_date3() {
        source.text = "31.12.2000"
        assertTrue(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_date1() {
        source.text = "32.02.2021"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_date2() {
        source.text = "01.02.1921"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_date3() {
        source.text = "01.13.2021"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_date4() {
        source.text = "0112.2021"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }


}