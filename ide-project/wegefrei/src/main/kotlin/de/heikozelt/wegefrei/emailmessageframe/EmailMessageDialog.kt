package de.heikozelt.wegefrei.emailmessageframe

import de.heikozelt.wegefrei.EmailUserAgent
import de.heikozelt.wegefrei.model.EmailMessage
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
 */
class EmailMessageDialog(private val emailUserAgent: EmailUserAgent) : JFrame() {

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
        size = Dimension(400, 300)
        val lay = GroupLayout(contentPane)
        lay.autoCreateGaps = true
        lay.autoCreateContainerGaps = true
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane)
                .addComponent(statusLabel)
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
        isVisible = true
    }

    fun loadData(emailMessage: EmailMessage) {
        this.emailMessage = emailMessage
        emailMessagePanel.loadData(emailMessage)
    }

    private fun send() {
        emailMessage?.let { eMessage ->
            statusLabel.text = "Wird gesendet..."
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