package de.heikozelt.wegefrei.gui

import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter

/**
 * Erlaubt die Eingabe bestimmter Zeichen.
 * Die Zeichen werden als Character-Predicate angegeben.
 * Alle anderen Zeichen werden ignoriert.
 * z.B. val docFilter = CharPredicateDocFilter{ it.isDigit() }
 */
open class CharPredicateDocFilter(private val predicate: (Char) -> Boolean): DocumentFilter() {
    @Throws(BadLocationException::class)
    override fun insertString(
        fb: FilterBypass?,
        offset: Int,
        string: String,
        attr: AttributeSet?
    ) {
        val newString = string.filter(predicate)
        fb?.insertString(offset, newString, attr)
    }

    @Throws(BadLocationException::class)
    override fun replace(
        fb: FilterBypass?,
        offset: Int,
        length: Int,
        text: String,
        attrs: AttributeSet?
    ) {
        val newString = text.filter(predicate)
        fb?.replace(offset, length, newString, attrs)
    }

    companion object {
        /**
         * Erlaubt die Eingabe einer Ganzzahl bestehend aus Ziffern.
         * Alle anderen Zeichen werden ignoriert.
         */
        val onlyDigitsDocFilter = CharPredicateDocFilter { it.isDigit() }

        /**
         * Erlaubt die Eingabe eines Datums bestehend aus Ziffern und Punkten.
         * z.B. "31.12.1999"
         */
        val dateDocFilter = CharPredicateDocFilter { it.isDigit() || it == '.' }

        /**
         * Erlaubt die Eingabe einer Uhrzeit.
         * Alle anderen Zeichen werden verworfen.
         * z.B. "23:59"
         */
        val timeDocFilter = CharPredicateDocFilter { it.isDigit() || it == ':' }
    }
}