package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.emailmessageframe.EmailMessageDialog
import de.heikozelt.wegefrei.gui.SmtpAuthenticator
import de.heikozelt.wegefrei.json.EmailServerConfig
import de.heikozelt.wegefrei.json.Tls
import de.heikozelt.wegefrei.model.EmailMessage
import de.heikozelt.wegefrei.settingsframe.SettingsFormFields
import org.slf4j.LoggerFactory
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.swing.JOptionPane

/**
 * use case
 * <ol>
 *   <li>Displays the email message</li>
 *   <li>Asks user for confirmation</li>
 *   <li>Asks for password (and remembers it)</li>
 *   <li>Displays status bar</li>
 *   <li>Sends email to SMTP server</li>
 *   <li>Calls back if successful or not</li>
 * </ol>
 * The E-Mail-Body must be HTML code.
 * todo Prio 1: Statusbalken
 * todo Prio 3: Einstellungen speichern nach erfolgreichem Test?
 * todo Prio 2: Bei Passwort-Eingabe-Fenster: Klick auf Abbrechen soll auch EMailDialog schließen
 * todo Prio 2: Bei E-Mail erfolgreich versendet Popup: Klick auf ok soll auch EMailDialog schließen
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
        emailMessageDialog.loadData(emailMessage)
    }

    /**
     * Jetzt aber wirklich absenden (und eventuell nach Passwort fragen).
     */
    fun sendMailDirectly(emailMessage: EmailMessage) {
        emailServerConfig?.let { serverConfig ->
            emailMessage?.let { eMessage ->
                val authenticator = SmtpAuthenticator()
                authenticator.setUserName(serverConfig.smtpUserName)
                val passwordEntered = authenticator.askForPassword()
                if (!passwordEntered) {
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
                        props["mail.smtp.ssl.protocols"] = "TLSv1 TLSv1.1 TLSv1.2"
                    }
                }

                val session = Session.getInstance(props, authenticator)
                log.debug("session: $session")

                val msg = MimeMessage(session)
                msg.addHeader("User-Agent", SettingsFormFields.MAIL_USER_AGENT);
                msg.setFrom(InternetAddress(eMessage.fromAddress, eMessage.fromName))
                msg.setRecipient(Message.RecipientType.TO, InternetAddress(eMessage.toAddress, eMessage.toName))
                msg.setSubject(eMessage.subject, "UTF-8");
                msg.setContent(eMessage.content, "text/html; charset=utf-8")

                try {
                    Transport.send(msg)
                    JOptionPane.showMessageDialog(
                        null,
                        "Die E-Mail-Nachricht wurde erfolgreich versendet.\n\nBitte prüfe deinen Post-Eingang!",
                        "E-Mail abgeschickt",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                } catch (ex: MessagingException) {
                    log.debug("exception while sending test message", ex)
                    log.debug("exception: ${ex.message}")
                    ex.cause?.let {
                        log.debug("cause: ${it.message}")
                    }
                    var errorMessage = "Es ist ein Fehler aufgetreten:\n\n${ex.message}"
                    ex.cause?.let {
                        errorMessage += "\n\n{$ex.cause.message}"
                    }

                    val result = JOptionPane.showMessageDialog(
                        null,
                        errorMessage,
                        "Test-E-Mail senden",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }
    }
}