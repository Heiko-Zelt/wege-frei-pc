package de.heikozelt.wegefrei.gui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import javax.swing.JTextField


class JTextFieldTest {

    /**
     * textField.setText(String text) interpretiert null als Leerstring
     */
    @Test
    fun jTextField_setText_null() {
        val textField = JTextField()
        textField.text = "Hallo"
        textField.text = null
        assertNotNull(textField.text)
        assertEquals("", textField.text)
    }
}