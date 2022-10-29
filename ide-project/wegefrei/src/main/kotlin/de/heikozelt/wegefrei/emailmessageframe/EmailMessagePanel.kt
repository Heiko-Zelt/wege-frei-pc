package de.heikozelt.wegefrei.emailmessageframe

import de.heikozelt.wegefrei.model.EmailMessage
import java.awt.Dimension
import javax.swing.GroupLayout
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * is part of email message frame
 * Layout:
 * <pre>
 * from:    ---
 * to:      ---
 * subject: ---
 * ------------
 * </pre>
 */
class EmailMessagePanel : JPanel() {
    private val fromField = JLabel()
    private val toField = JLabel()
    private val subjectField = JLabel()
    private val contentField = JLabel()

    init {
        contentField.preferredSize = Dimension(350, 300)
        val fromLabel = JLabel("von:")
        val toLabel = JLabel("an:")
        val subjectLabel = JLabel("Betreff:")
        val lay = GroupLayout(this)
        lay.autoCreateGaps = true
        lay.autoCreateContainerGaps = true
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    lay.createSequentialGroup()
                        .addGroup(
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(fromLabel)
                                .addComponent(toLabel)
                                .addComponent(subjectLabel)
                        )
                        .addGroup(
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(fromField)
                                .addComponent(toField)
                                .addComponent(subjectField)
                        )
                )
                .addComponent(contentField)
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(fromLabel)
                        .addComponent(fromField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(toLabel)
                        .addComponent(toField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(subjectLabel)
                        .addComponent(subjectField)
                )
                .addComponent(contentField)
        )
        layout = lay
    }

    fun loadData(emailMessage: EmailMessage) {
        fromField.text = "${emailMessage.fromName} <${emailMessage.fromAddress}>"
        toField.text = "${emailMessage.toName} <${emailMessage.toAddress}>"
        subjectField.text = emailMessage.subject
        contentField.text = emailMessage.content
        //revalidate()
        //repaint()
    }
}