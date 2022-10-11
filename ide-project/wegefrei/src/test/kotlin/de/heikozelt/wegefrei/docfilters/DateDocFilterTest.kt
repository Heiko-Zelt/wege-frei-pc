package de.heikozelt.wegefrei.docfilters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.swing.text.PlainDocument
import javax.swing.text.SimpleAttributeSet

class DateDocFilterTest {

    @Test
    fun test1() {
        val doc = PlainDocument()
        doc.documentFilter = DateDocFilter()
        doc.insertString(0, "a31.01.1999 ", SimpleAttributeSet())
        val line = doc.getParagraphElement(0)
        val start = line.startOffset
        val end = line.endOffset
        val length = end - start
        val text = doc.getText(start, length)
        assertEquals("31.01.1999\n", text)

        //doc.dump(System.out)
    }
}