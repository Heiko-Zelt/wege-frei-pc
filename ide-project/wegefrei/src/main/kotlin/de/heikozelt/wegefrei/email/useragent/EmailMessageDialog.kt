package de.heikozelt.wegefrei.email.useragent

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
 * Buttons: (Senden) (Abbrechen)
 * </pre>
 */
class EmailMessageDialog(confirmedCallback: () -> Unit) : JFrame() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val emailMessagePanel = EmailMessagePanel()
    private var emailMessage: EmailMessage? = null
    private val sendButton = JButton("Senden")
    private val cancelButton = JButton("Abbrechen")

    init {
        val scrollPane = JScrollPane(emailMessagePanel)
        sendButton.addActionListener {
            confirmedCallback()
            this.dispose()
        }
        cancelButton.addActionListener { this.dispose() }

        title = "E-Mail-Nachricht"
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        val lay = GroupLayout(contentPane)
        lay.autoCreateGaps = false
        lay.autoCreateContainerGaps = false
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane)
                .addGap(4)
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE)
                        .addComponent(sendButton)
                        .addComponent(cancelButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(scrollPane)
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(sendButton)
                        .addComponent(cancelButton)
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
}