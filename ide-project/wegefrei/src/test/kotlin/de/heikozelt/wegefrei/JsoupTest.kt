package de.heikozelt.wegefrei

import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class JsoupTest {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    @Test
    fun prettyHTML1() {
        val html = "<html><body><h1>hello world</h1></body></html>"
        log.debug("original: $html")
        val doc = Jsoup.parse(html) // pretty print HTML
        val pretty = doc.toString()
        doc.outputSettings().indentAmount(2)
        log.debug(pretty)
        val expected = """
            <html>
              <head></head>
              <body>
                <h1>hello world</h1>
              </body>
            </html>""".trimIndent()
        assertEquals(expected, doc.toString())
    }

    @Test
    fun prettyHTML2() {
        val html = "<html><body><h1>hello\n" +
                "\nworld</h1>\n\n" +
                "<p>text</p></body></html>"
        log.debug("original: $html")
        val doc = Jsoup.parse(html,) // pretty print HTML
        val pretty = doc.toString()
        doc.outputSettings().indentAmount(2)
        log.debug(pretty)
        val expected = """
            <html>
              <head></head>
              <body>
                <h1>hello world</h1>
                <p>text</p>
              </body>
            </html>""".trimIndent()
        assertEquals(expected, doc.toString())
    }
}