package de.heikozelt.wegefrei.email.useragent

import org.slf4j.LoggerFactory
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.swing.JOptionPane

class SmtpAuthenticator: Authenticator() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var userName: String? = null
    private var password: String? = null

    /**
     * remember if the password was accepted last time trying to send an email message
     */
    private var passwordAccepted = false

    public override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(userName, password)
    }

    fun setUserName(userName: String) {
        this.userName = userName
    }

    fun maybeAskForPassword(): Boolean {
        return if(passwordAccepted) {
            true
        } else {
            askForPassword()
        }
    }

    /**
     * Asks for a password and remembers it.
     * 3 Cases:
     * <ul>
     *   <li>Perfect: User enters a password and clicks on ok button -> true</li>
     *   <li>User clicks on close or cancel -> false</li>
     *   <li>User doesn't enter password, but clicks ok -> false</li>
     * </ul>
     * @return true, if user provided a password
     */
    fun askForPassword(): Boolean {
        val panel = PasswordPanel()
        val options = arrayOf("Abbrechen", "OK")
        val result = JOptionPane.showOptionDialog(null, panel, "SMTP-Server-Authentifizierung",
            JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[1]);
        return if(result == 1) {
            val pw = panel.getPassword()
            if(pw.isEmpty()) {
                log.debug("empty password given")
                false
            } else {
                log.debug("password entered")
                password = pw
                true
            }
        } else {
          log.debug("user canceled password input")
          false
        }
    }

    /**
     * gives feedback, if the password was accepted by the SMTP server.
     * If it was accepted, don't aks for it again.
     * If it was not accepted, ask again next time.
     */
    fun passwordFeedback(accepted: Boolean) {
        passwordAccepted = accepted
    }

}