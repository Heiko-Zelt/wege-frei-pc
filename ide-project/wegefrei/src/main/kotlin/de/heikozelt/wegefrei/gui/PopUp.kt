package de.heikozelt.wegefrei.gui

import javax.swing.JOptionPane


fun showValidationErrors(errors: List<String>) {
    val message = errors.joinToString("<br>", "<html>", "</html>")
    JOptionPane.showMessageDialog(
        null,
        message,
        "Validierungsfehler",
        JOptionPane.INFORMATION_MESSAGE
    )
}