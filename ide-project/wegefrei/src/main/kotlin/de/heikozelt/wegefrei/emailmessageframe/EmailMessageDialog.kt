package de.heikozelt.wegefrei.emailmessageframe

import de.heikozelt.wegefrei.EmailUserAgent
import de.heikozelt.wegefrei.model.EmailMessage
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.WindowConstants

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
class EmailMessageDialog(private val emailUserAgent: EmailUserAgent): JFrame() {

    private val emailMessageButtonsBar = EmailMessageButtonsBar(this)
    private val emailMessagePanel = EmailMessagePanel()
    private var emailMessage: EmailMessage? = null

    init {
        title = "E-Mail-Nachricht"
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        layout = BorderLayout()
        val scrollPane = JScrollPane(emailMessagePanel)
        size = Dimension(400, 300)
        add(scrollPane, BorderLayout.CENTER)
        add(emailMessageButtonsBar, BorderLayout.SOUTH)
        isVisible = true
    }

    fun loadData(emailMessage: EmailMessage) {
        this.emailMessage = emailMessage
        emailMessagePanel.loadData(emailMessage)
    }

    fun send() {
        emailMessage?.let {
            emailUserAgent.sendMailDirectly(it)
        }
    }
}