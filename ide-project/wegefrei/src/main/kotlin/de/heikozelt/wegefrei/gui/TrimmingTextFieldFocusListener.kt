package de.heikozelt.wegefrei.gui

import org.slf4j.LoggerFactory
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JTextField

class TrimmingTextFieldFocusListener: FocusListener {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    override fun focusGained(e: FocusEvent?) {
        // do noting
    }

    // entfernt Whitespaces am Anfang und Ende der Eingabe
    // z.B. wichtig um Zahlen oder Datum/Uhrzeit zu parsen
    // und aus Schönheits-Gründen
    override fun focusLost(e: FocusEvent?) {
        val src = e?.source
        if(src is JTextField) {
            log.debug("text before trim: >>>" + src.text + "<<<")
            src.text = src.text.trim()
            log.debug("text after trim: >>>" + src.text + "<<<")
        }
    }
}