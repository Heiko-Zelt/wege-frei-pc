package de.heikozelt.wegefrei.email.useragent

import org.slf4j.LoggerFactory
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
class EmailSender(private val outbox: Outbox<Int>, private val agent: EmailUserAgent) : Thread("EmailSender") {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    override fun run() {
        log.debug("run()")
        while (true) {
            var message: EmailMessage<Int>? = null
            try {
                message = outbox.next() ?: break
                agent.sendMail(message)
                log.debug("send successful. sentTime: ${message.getSentTime()}, messageID: ${message.getMessageID()}")
                message.getSentTime()?.let { sTime ->
                    message.getMessageID()?.let { mID ->
                        outbox.sentSuccessfulCallback(message.externalID, sTime, mID)
                    }
                }
                sleep(5000)
            } catch (ex: Exception) {
                log.debug("sending email failed: ", ex)
                outbox.sendFailedCallback(message?.externalID, ex)
                EventQueue.invokeLater {
                    // todo Popup mit Fehlermeldung und Button Fortfahren/Weiter versuchen
                    log.debug("ask user, if she want's to try again or cancel")
                }
                // Thread beenden und auf Benutzereingabe warten
                log.debug("exit send loop because an exception occurred")
                break
            }
        }
        log.debug("run() finished")
    }
}