package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.emailmessageframe.EmailMessageDialog
import de.heikozelt.wegefrei.gui.SmtpAuthenticator
import de.heikozelt.wegefrei.json.EmailServerConfig
import de.heikozelt.wegefrei.json.Tls
import de.heikozelt.wegefrei.model.EmailMessage
import org.slf4j.LoggerFactory
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.MimeMessage
import javax.swing.JOptionPane

/**
 * use case
 * <ol>
 *   <li>Displays the email message</li>
 *   <li>Asks user for confirmation</li>
 *   <li>Asks for password (and remembers it)</li>
 *   <li>Displays status bar/label</li>
 *   <li>Sends email to SMTP server</li>
 *   <li>Calls back if successful or not</li>
 * </ol>
 * The email body must be HTML code.
 * The email may have photos attached.
 * todo Prio 3: Einstellungen speichern nach erfolgreichem Test?
 * todo Prio 2: Passwort merken, aber wie jederzeit änderbar?
 * Sobald der E-Mail-Versand einmal gescheitert ist, Passwort wieder vergessen.
 */
class EmailUserAgent {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var emailServerConfig: EmailServerConfig? = null

    fun setEmailServerConfig(config: EmailServerConfig) {
        this.emailServerConfig = config
    }

    /**
     * Erst die E-Mail-Nachricht anzeigen und Anwender fragen.
     */
    fun sendMailAfterConfirmation(emailMessage: EmailMessage) {
        val emailMessageDialog = EmailMessageDialog(this)
        emailMessageDialog.setEmailMessage(emailMessage)
    }

    /**
     * Jetzt aber wirklich absenden (und eventuell nach Passwort fragen).
     */
    fun sendMailDirectly(emailMessage: EmailMessage, doneCallback: (Boolean) -> Unit) {
        emailServerConfig?.let { serverConfig ->
            val authenticator = SmtpAuthenticator()
            authenticator.setUserName(serverConfig.smtpUserName)
            val passwordEntered = authenticator.askForPassword()
            if (!passwordEntered) {
                doneCallback(false)
                return
            }
            log.debug("auth: ${authenticator.passwordAuthentication}")
            log.debug("userName: ${authenticator.passwordAuthentication.userName}")
            //log.debug("password: ${authenticator.passwordAuthentication.password}") don't log passwords

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

            val session = Session.getInstance(props, authenticator)
            log.debug("session: $session")

            val msg = MimeMessage(session)
            msg.addHeader("User-Agent", MAIL_USER_AGENT)
            msg.setFrom(emailMessage.from.asInternetAddress())
            emailMessage.to.forEach{ msg.addRecipient(Message.RecipientType.TO, it.asInternetAddress()) }
            emailMessage.cc?.forEach{ msg.addRecipient(Message.RecipientType.CC, it.asInternetAddress()) }
            msg.setSubject(emailMessage.subject, "UTF-8")
            msg.setContent(emailMessage.content, "text/html; charset=utf-8")

            try {
                Transport.send(msg)
                doneCallback(true)
                JOptionPane.showMessageDialog(
                    null,
                    "Die E-Mail-Nachricht wurde erfolgreich versendet.",
                    "E-Mail abgeschickt",
                    JOptionPane.INFORMATION_MESSAGE
                )
            } catch (ex: MessagingException) {
                log.debug("exception while sending test message", ex)
                log.debug("exception: ${ex.message}")
                ex.cause?.let {
                    log.debug("cause: ${it.message}")
                }

                doneCallback(false)

                var errorMessage = "Es ist ein Fehler aufgetreten:\n\n${ex.message}"
                ex.cause?.let {
                    errorMessage += "\n\n{$ex.cause.message}"
                }
                JOptionPane.showMessageDialog(
                    null,
                    errorMessage,
                    "Test-E-Mail senden",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }?: run {
            log.error("emailServerConfig is null")
        }
    }

    companion object {
        const val MAIL_USER_AGENT = "Wege frei! https://github.com/Heiko-Zelt/wege-frei-pc"
    }
}