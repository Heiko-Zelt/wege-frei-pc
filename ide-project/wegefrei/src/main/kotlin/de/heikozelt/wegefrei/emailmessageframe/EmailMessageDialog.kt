package de.heikozelt.wegefrei.emailmessageframe

import de.heikozelt.wegefrei.model.EmailMessage
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.WindowConstants

/**
 * Displays an email message and asks for confirmation. Send- or Cancel-Button.
 * This dialog window should be displayed before sending an email or moving a notice into the outbox.
 */
class EmailMessageDialog: JFrame() {

    private val emailMessageButtonsBar = EmailMessageButtonsBar()
    private val emailMessagePanel = EmailMessagePanel()

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
        emailMessagePanel.loadData(emailMessage)
    }

    fun setNextAction(action: (ActionEvent) -> Unit) {
        emailMessageButtonsBar.setSendAction(action)
    }
}