package de.heikozelt.wegefrei

import org.slf4j.LoggerFactory
import javax.swing.GroupLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel

/**
 * Shows a dialog window like JOptionPane.showOptionDialog(),
 * but doesn't block the calling thread.
 * The window doesn't contain an icon.
 */
class OptionFrame(title: String, message: String): JFrame(title) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val lay = GroupLayout(this.contentPane)
    private val buttonsHorizontalSequentialGroup: GroupLayout.SequentialGroup? = lay.createSequentialGroup()
    private val buttonsVerticalParallelGroup: GroupLayout.ParallelGroup? = lay.createParallelGroup()

    init {
        val messageLabel = JLabel(message)
        lay.autoCreateGaps = true
        lay.autoCreateContainerGaps = true
        //minimumSize = Dimension(400, 100)

        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(messageLabel)
                .addGroup(buttonsHorizontalSequentialGroup)
        )

        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(messageLabel)
                .addGroup(buttonsVerticalParallelGroup)
        )
        contentPane.layout = lay
    }

    // todo Prio 1: add layout
    fun addOption(buttonText: String, eventHandler: () -> Unit) {
        log.debug("add option: $buttonText")
        val button = JButton(buttonText)
        button.addActionListener() {
            log.debug("handle event: $buttonText")
            isVisible = false
            dispose()
            eventHandler()
        }
        buttonsHorizontalSequentialGroup?.addComponent(button)
        buttonsVerticalParallelGroup?.addComponent(button)
    }

    override fun setVisible(b: Boolean) {
        if(b) pack()
        super.setVisible(b)
    }
}