package de.heikozelt.wegefrei.gui

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.swing.JTextField


class TimeVerifiersTest {

    private val source = JTextField()
    private val target = JTextField()
    private val verifier = Verifiers.timeVerifier

    @Test
    fun verify_correct_time1() {
        source.text = "01:02"
        assertTrue(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_correct_time2() {
        source.text = "1:2"
        assertTrue(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_correct_time3() {
        source.text = "23:59"
        assertTrue(verifier.shouldYieldFocus(source, target))
    }

    /**
     * "00:00" instead of "24:00"
     */
    @Test
    fun verify_wrong_time1() {
        source.text = "24:00"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_time2() {
        source.text = "00:60"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_time3() {
        source.text = "001:59"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_time4() {
        source.text = "01:023"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }


}