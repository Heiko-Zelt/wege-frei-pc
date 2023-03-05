package de.heikozelt.wegefrei.email.useragent

import de.heikozelt.wegefrei.json.Tls
import org.slf4j.LoggerFactory
import java.util.*
import javax.mail.AuthenticationFailedException
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport

/**
 * A simple mail user agent (MUA)
 * use case
 * <ol>
 *   <li>Displays the email message</li>
 *   <li>Asks user for confirmation</li>
 *   <li>Asks for password (and remembers it)</li>
 *   <li>Displays status bar/label</li>
 *   <li>Sends email to SMTP server</li>
 *   <li>Calls application back if successful or not</li>
 * </ol>
 * The email body must be HTML code.
 * The email may have photos attached.
 * todo Prio 3: Einstellungen speichern nach erfolgreichem Test?
 * todo Prio 2: Passwort merken, aber wie jederzeit änderbar?
 * Sobald der E-Mail-Versand einmal gescheitert ist, Passwort wieder vergessen.
 * todo Prio 2: Lesebstätigung (DSN) anfordern
 */
class EmailUserAgent {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var emailServerConfig: EmailServerConfig? = null
    private var authenticator: SmtpAuthenticator? = null

    fun setEmailServerConfig(config: EmailServerConfig) {
        this.emailServerConfig = config
        authenticator = SmtpAuthenticator()
        authenticator?.setUserName(config.smtpUserName)
    }

    /**
     * Jetzt aber wirklich absenden (und eventuell nach Passwort fragen).
     * Bei Fehler: Exception (z.B. Netzwerkverbindung oder Benutzer hat abgebrochen)
     */
    fun sendMail(emailMessage: EmailMessage<Int>) {
        log.debug("sendMailDirectly()")
        emailServerConfig?.let { serverConfig ->
            authenticator?.let { auth ->
                val passwordEntered = auth.maybeAskForPassword()
                if (!passwordEntered) {
                    throw NoPasswordException()
                }
                log.debug("auth: ${auth.passwordAuthentication}")
                log.debug("userName: ${auth.passwordAuthentication.userName}")
                //log.debug("password: ${authenticator.passwordAuthentication.password}") don't log passwords!

                val props = Properties()
                props["mail.smtp.auth"] = true
                props["mail.smtp.host"] = serverConfig.smtpHost
                props["mail.smtp.port"] = serverConfig.smtpPort
                val tls = serverConfig.tls
                log.debug("TLS: $tls")
                when (tls) {
                    Tls.PLAIN -> props["mail.smtp.ssl.enable"] = false
                    Tls.START_TLS -> props["mail.smtp.starttls.required"] = true
                    Tls.TLS -> {
                        props["mail.smtp.ssl.enable"] = true
                        props["mail.smtp.ssl.protocols"] = "TLSv1 TLSv1.1 TLSv1.2 TLSv1.3"
                    }
                }

                // todo close session, finally or try with?
                val session = Session.getInstance(props, authenticator)
                log.debug("session: $session")
                emailMessage.buildMimeMessage(session)
                val msg = emailMessage.getMimeMessage()
                try {
                    // "Note that send is a static method that creates and manages its own connection."
                    Transport.send(msg)
                    auth.passwordFeedback(true)
                    return // successful
                    /*
                    JOptionPane.showMessageDialog(
                        null,
                        "Die E-Mail-Nachricht wurde erfolgreich versendet.",
                        "E-Mail abgeschickt",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                     */
                } catch (ex: MessagingException) {
                    log.debug("exception while sending message", ex)
                    log.debug("exception: ${ex.message}")
                    ex.cause?.let {
                        log.debug("cause: ${it.message}")
                    }


                    // javax.mail.AuthenticationFailedException: 535 5.7.8 Authentication failed: wrong user/password
                    if(ex is AuthenticationFailedException) {
                        auth.passwordFeedback(false)
                        throw ex
                    }
/*
                    var errorMessage = "Es ist ein Fehler aufgetreten:\n\n${ex.message}"
                    ex.cause?.let {
                        errorMessage += "\n\n{$ex.cause.message}"
                    }
                    JOptionPane.showMessageDialog(
                        null,
                        errorMessage,
                        "E-Mail senden",
                        JOptionPane.ERROR_MESSAGE
                    )
 */
                }
            }
        }?: run {
            log.error("emailServerConfig is null")
        }
    }

    companion object {
        const val MAIL_USER_AGENT = "Wege frei! https://github.com/Heiko-Zelt/wege-frei-pc"
    }
}