package de.heikozelt.wegefrei.gui

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.swing.JTextField


class TimeVerifierTest {

    private val source = JTextField()
    private val target = JTextField()
    private val verifier = PatternVerifier.timeVerifier

    @Test
    fun verify_correct_time1() {
        source.text = "1:2:3"
        assertTrue(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_correct_time2() {
        source.text = "23:59:59"
        assertTrue(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_correct_time3() {
        source.text = "23:0:00"
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

    @Test
    fun verify_wrong_time5() {
        source.text = "01:02:"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_time6() {
        source.text = "01:02:000"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_time7() {
        source.text = "01:02"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_time8() {
        source.text = "1:2"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

    @Test
    fun verify_wrong_time9() {
        source.text = "23:59"
        assertFalse(verifier.shouldYieldFocus(source, target))
    }

}