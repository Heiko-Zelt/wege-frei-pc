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
     * asks for a password and remembers it.
     * @return true, if user provided a password
     */
    fun askForPassword(): Boolean {
        val panel = PasswordPanel()
        val options = arrayOf("OK", "Abbrechen")
        val result = JOptionPane.showOptionDialog(null, panel, "SMTP-Server-Authentifizierung",
            JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[1]);
        when(result) {
            0 -> log.debug("password entered")
            else -> return false
        }
        password = panel.getPassword()
        return true
    }

    fun passwordFeedback(accepted: Boolean) {
        passwordAccepted = accepted
    }

}