package de.heikozelt.wegefrei.emailmessageframe

import de.heikozelt.wegefrei.gui.Styles.Companion.BUTTONS_BAR_BORDER
import org.slf4j.LoggerFactory
import java.awt.Window
import javax.swing.*

class EmailMessageButtonsBar(private val emailMessageDialog: EmailMessageDialog) : JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val sendButton = JButton("Senden")

    init {
        val cancelButton = JButton("Abbrechen")
        cancelButton.addActionListener {
            val win: Window? = SwingUtilities.getWindowAncestor(this)
            win?.dispose()
        }
        sendButton.addActionListener {
            emailMessageDialog.send()
        }

        val lay = GroupLayout(this)
        lay.autoCreateGaps = true;
        //lay.autoCreateContainerGaps = true;
        log.debug("border before: $border")
        border = BUTTONS_BAR_BORDER
        log.debug("border after: $border")

        // left to right
        lay.setHorizontalGroup(
            lay.createSequentialGroup()
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE)
                .addComponent(sendButton)
                .addComponent(cancelButton)
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(sendButton)
                .addComponent(cancelButton)
        )
        lay.linkSize(SwingConstants.HORIZONTAL, sendButton, cancelButton);
        layout = lay

    }
}