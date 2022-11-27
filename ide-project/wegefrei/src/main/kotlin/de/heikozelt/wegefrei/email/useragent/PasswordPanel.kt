package de.heikozelt.wegefrei.email.useragent

import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField

/**
 * used by JOptionPane.showOptionDialog() in SmtpAuthenticator
 */
class PasswordPanel: JPanel() {
    private val passwordField = JPasswordField(12)

    init {
        val label = JLabel("E-Mail-Passwort:")
        add(label)
        add(passwordField)
    }

    fun getPassword(): String {
        // JPasswordField.getPassword() returns an array of characters instead of a string.
        return String(passwordField.password).trim()
    }
}