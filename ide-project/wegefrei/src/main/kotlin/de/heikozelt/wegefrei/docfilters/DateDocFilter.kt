package de.heikozelt.wegefrei.docfilters

/**
 * Erlaubt die Eingabe eines Datums bestehend aus Ziffern und Punkten.
 * z.B. "31.12.1999"
 */
class DateDocFilter: CharPredicateDocFilter( { it.isDigit() || it == '.' })
/*
{
    private val log = KotlinLogging.logger {}

    @Throws(BadLocationException::class)
    override fun insertString(
        fb: FilterBypass?,
        offset: Int,
        string: String,
        attr: AttributeSet?
    ) {
        log.debug("insert")
        val newString = string.filter{ it.isDigit() || (it == '.') }
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
        val newString = text.filter{ it.isDigit() || (it == '.') }
        fb?.replace(offset, length, newString, attrs)
        log.debug("text after replace: >>>$text<<<")
    }

}
 */