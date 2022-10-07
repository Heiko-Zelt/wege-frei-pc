package de.heikozelt.wegefrei.gui

import java.util.*
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter

/**
 * Konvertiert die Text-Eingabe in einem JTextField in Großbuchstaben.
 * z.B. beim Kfz-Kennzeichen
 */
class UppercaseDocumentFilter: DocumentFilter() {
    @Throws(BadLocationException::class)
    override fun insertString(
        fb: FilterBypass?, offset: Int,
        string: String, attr: AttributeSet?
    ) {
        super.insertString(fb, offset, string.uppercase(Locale.getDefault()), attr)
    }

    @Throws(BadLocationException::class)
    override fun replace(
        fb: FilterBypass?, offset: Int, length: Int,
        text: String, attrs: AttributeSet?
    ) {
        super.insertString(fb, offset, text.uppercase(Locale.getDefault()), attrs)
    }
}