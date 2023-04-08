package de.heikozelt.wegefrei.gui

import org.slf4j.LoggerFactory
import java.util.*
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter

/**
 * Konvertiert die Text-Eingabe in einem JTextField in Großbuchstaben.
 * z.B. beim Kfz-Kennzeichen
 */
class UppercaseDocumentFilter: DocumentFilter() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    @Throws(BadLocationException::class)
    override fun insertString(
        fb: FilterBypass?,
        offset: Int,
        string: String,
        attr: AttributeSet?
    ) {
        log.debug("insert")
        fb?.insertString(offset, string.uppercase(Locale.getDefault()), attr)
    }

    @Throws(BadLocationException::class)
    override fun replace(
        fb: FilterBypass?,
        offset: Int,
        length: Int,
        text: String,
        attrs: AttributeSet?
    ) {
        //log.debug("replace")
        //log.debug("text before replace: >>>$text<<<")
        fb?.replace(offset, length, text.uppercase(Locale.getDefault()), attrs)
        //log.debug("text after replace: >>>$text<<<")
    }

}