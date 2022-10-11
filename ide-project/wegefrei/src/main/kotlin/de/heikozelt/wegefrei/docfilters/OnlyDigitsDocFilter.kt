package de.heikozelt.wegefrei.docfilters

/**
 * Erlaubt die Eingabe einer Ganzzahl bestehend aus Ziffern.
 * Alle anderen Zeichen werden ignoriert.
 */
class OnlyDigitsDocFilter: CharPredicateDocFilter({ it.isDigit() })

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
        val newString = string.filter{ it.isDigit() }
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
        val newString = text.filter{ it.isDigit() }
        fb?.replace(offset, length, newString, attrs)
        log.debug("text after replace: >>>$text<<<")
    }
}

 */