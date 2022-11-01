package de.heikozelt.wegefrei.mua

import org.slf4j.LoggerFactory
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
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    // GUI components
    private val fromField = JLabel()
    private val tosField = JLabel()
    private val ccsLabel = JLabel("in Kopie:")
    private val ccsField = JLabel()
    private val subjectField = JLabel()
    private val contentField = JLabel()

    init {
        log.debug("init")

        // GUI components
        val fromLabel = JLabel("von:")
        val tosLabel = JLabel("an:")
        val subjectLabel = JLabel("Betreff:")

        // layout:
        contentField.preferredSize = Dimension(350, 300)
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
                                .addComponent(tosLabel)
                                .addComponent(ccsLabel)
                                .addComponent(subjectLabel)
                        )
                        .addGroup(
                            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(fromField)
                                .addComponent(tosField)
                                .addComponent(ccsField)
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
                        .addComponent(tosLabel)
                        .addComponent(tosField)
                )
                .addGroup(
                    lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(ccsLabel)
                        .addComponent(ccsField)
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

    /**
     * todo CC und Anlagen anzeigen
     */
    fun setEmailMessage(emailMessage: EmailMessage) {
        fromField.text = emailMessage.from.asText()
        // todo Prio 3: ggf. mehrere Empfänger und CC anzeigen
        tosField.text = emailMessage.tos.first().asText()
        val hasCc = emailMessage.ccs.isNotEmpty()
        ccsLabel.isVisible = hasCc
        if(hasCc) {
            ccsField.text = emailMessage.ccs.first().asText()
        }
        subjectField.text = emailMessage.subject
        contentField.text = emailMessage.coverLetter
    }
}