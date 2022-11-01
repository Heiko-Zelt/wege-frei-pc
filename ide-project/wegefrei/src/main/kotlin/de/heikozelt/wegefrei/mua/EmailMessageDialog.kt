package de.heikozelt.wegefrei.mua

import org.slf4j.LoggerFactory
import java.awt.Dimension
import javax.swing.*

/**
 * Displays an email message and asks for confirmation. Send- or Cancel-Button.
 * This dialog window should be displayed before sending an email or moving a notice into the outbox.
 *
 * Layout:
 * <pre>
 * | from:    ---
 * | to:      ---
 * | subject: ---
 * | ------------
 * | ------------
 * Status: nichts oder "Wird gesendet..." oder "Wurde gesendet."
 * Buttons: (Senden) (Abbrechen) oder (Ok)
 * </pre>
 * todo Prio 3: Ergebnis des Test-E-Mail-Versands durch einen Punkt anzeigen
 * grün = erfolgreich, rot = Fehler, gelb = Benutzer hat abgebrochen/kein Passwort angegeben
 * siehe: https://www.compart.com/en/unicode/U+2B24
 */
class EmailMessageDialog(private val emailUserAgent: EmailUserAgent) : JFrame() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val emailMessagePanel = EmailMessagePanel()
    private val statusLabel = JLabel()
    private var emailMessage: EmailMessage? = null
    private val sendButton = JButton("Senden")
    private val cancelButton = JButton("Abbrechen")
    private val okButton = JButton("Ok")

    init {
        val scrollPane = JScrollPane(emailMessagePanel)
        sendButton.addActionListener { send() }
        cancelButton.addActionListener { this.dispose() }
        okButton.isVisible = false
        okButton.addActionListener { this.dispose() }

        title = "E-Mail-Nachricht"
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        val lay = GroupLayout(contentPane)
        lay.autoCreateGaps = false
        lay.autoCreateContainerGaps = false
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane)
                .addGroup(
                    lay.createSequentialGroup()
                        .addGap(4)
                        .addComponent(statusLabel)
                )

                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE)
                        .addComponent(sendButton)
                        .addComponent(cancelButton)
                        .addComponent(okButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(scrollPane)
                .addComponent(statusLabel)
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(sendButton)
                        .addComponent(cancelButton)
                        .addComponent(okButton)
                )
        )
        lay.linkSize(SwingConstants.HORIZONTAL, sendButton, cancelButton)
        layout = lay
        minimumSize = Dimension(250, 250)
        size = Dimension(400, 300)
        isVisible = true
    }

    fun setEmailMessage(emailMessage: EmailMessage) {
        log.debug("setEmailMessage(subject=${emailMessage.subject})")
        this.emailMessage = emailMessage
        emailMessagePanel.setEmailMessage(emailMessage)
    }

    private fun send() {
        log.debug("send()")
        emailMessage?.let { eMessage ->
            statusLabel.text = "wird gesendet..."
            log.debug("subject=${eMessage.subject} wird gesendet")
            sendButton.isEnabled = false
            emailUserAgent.sendMailDirectly(eMessage) { success -> done(success) }
        }
    }

    private fun done(success: Boolean) {
        if(success) {
            statusLabel.text = "Erfolgreich gesendet."
            okButton.isVisible = true
            sendButton.isVisible = false
            cancelButton.isVisible = false
        } else {
            statusLabel.text = "Beim Senden ist ein Fehler aufgetreten."
            sendButton.isEnabled = true
        }
    }
}