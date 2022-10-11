package de.heikozelt.wegefrei.docfilters

/**
 * Erlaubt die Eingabe einer Uhrzeit.
 * Alle anderen Zeichen werden verworfen.
 * z.B. "23:59"
 */
class TimeDocFilter: CharPredicateDocFilter({ it.isDigit() || it == ':' })

/*
    DocumentFilter() {
    private val log = KotlinLogging.logger {}

    @Throws(BadLocationException::class)
    override fun insertString(
        fb: FilterBypass?,
        offset: Int,
        string: String,
        attr: AttributeSet?
    ) {
        log.debug("insert")
        val newString = string.filter{ it.isDigit() || (it == ':') }
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
        log.debug("replace")
        log.debug("text before replace: >>>$text<<<")
        val newString = text.filter{ it.isDigit() || (it == ':') }
        fb?.replace(offset, length, newString, attrs)
        log.debug("text after replace: >>>$text<<<")
    }
}

*/