package de.heikozelt.wegefrei.email.useragent

import java.awt.EventQueue

/**
 * This thread sends email messages as long as there are message in the outbox.
 * If an error occurs the user is asked to cancel or continue.
 * Problem: Hintergrund-Thread muss bei Fehlern ein Popup-Fenster öffnen!
 * Lösung:
 * <ol>
 *   <li>Popup öffnen</li>
 *   <li>Thread beenden</li>
 *   <li>Wenn Benutzerin auf "Fortfahren/Weiter versuchen" klickt</li>
 *   <li>Thread erneut starten</li>
 * </ol>
 */
class EmailSender(private val outbox: Outbox<Int>, private val agent: EmailUserAgent): Thread("EmailSender") {

    /**
     * wahr, wenn der Thread gerade läuft oder Popup mit Fehlermeldung angezeigt wird
     */
    private var isSending = false

    override fun run() {
        isSending = true
        while(true) {
            val message = outbox.next() ?: break
            try {
              val success = agent.sendMailDirectly(message)
              outbox.sendCallback(message, success)
            } catch (ex: Exception) {
                EventQueue.invokeLater {
                    // todo Popup mit Fehlermeldung und Button Fortfahren/Weiter versuchen
                }
                // Thread beenden und auf Benutzereingabe warten
                return
            }
        }
        // erfolgreich beendet
        isSending = false
    }
}