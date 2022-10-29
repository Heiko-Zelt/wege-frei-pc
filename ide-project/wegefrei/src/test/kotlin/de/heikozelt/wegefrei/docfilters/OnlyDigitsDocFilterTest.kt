package de.heikozelt.wegefrei.docfilters

import de.heikozelt.wegefrei.gui.CharPredicateDocFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.swing.text.PlainDocument
import javax.swing.text.SimpleAttributeSet

class OnlyDigitsDocFilterTest {

    @Test
    fun test1() {
        val doc = PlainDocument()
        doc.documentFilter = CharPredicateDocFilter.onlyDigitsDocFilter
        doc.insertString(0, "a1b2c3", SimpleAttributeSet())
        val line = doc.getParagraphElement(0)
        val start = line.startOffset
        val end = line.endOffset
        val length = end - start
        val text = doc.getText(start, length)
        assertEquals("123\n", text)

        //doc.dump(System.out)
    }
}