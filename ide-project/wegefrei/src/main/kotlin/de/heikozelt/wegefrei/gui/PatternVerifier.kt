package de.heikozelt.wegefrei.gui

import java.util.regex.Pattern
import javax.swing.InputVerifier
import javax.swing.JComponent
import javax.swing.JTextField

/**
 * todo: Prio 2: Verifiers und PatternVerifier in eine Klasse vereinen
 * todo: Prio 3: ermöglichen mehrere E-Mail-Adressen durch Komma getrennt anzugeben
 * Aufbau einer E-Mail-Adresse: "irgendwer@irgendeine_domain"
 */
open class PatternVerifier(patternStr: String) : InputVerifier() {

    private var pattern: Pattern = Pattern.compile(patternStr)

    override fun verify(input: JComponent?): Boolean {
        if(input is JTextField) {
            val m = pattern.matcher(input.text)
            return m.matches()
        } else {
            throw (IllegalArgumentException())
        }
    }
}
