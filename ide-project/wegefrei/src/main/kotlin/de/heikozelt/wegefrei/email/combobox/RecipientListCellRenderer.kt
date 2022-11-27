package de.heikozelt.wegefrei.email.combobox

import de.heikozelt.wegefrei.email.EmailAddressEntity
import org.slf4j.LoggerFactory
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.border.Border
import javax.swing.border.EmptyBorder

class RecipientListCellRenderer: JLabel(), ListCellRenderer<EmailAddressEntity?> {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var noFocusBorder: Border = EmptyBorder(1, 1, 1, 1)

    override fun getListCellRendererComponent(
        list: JList<out EmailAddressEntity>?,
        value: EmailAddressEntity?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        isOpaque = true
        list?.let {
            background = if (isSelected) {
                list.selectionBackground
            } else {
                list.background
            }
            foreground = if (isSelected) {
                list.selectionForeground
            } else {
                list.foreground
            }
            //isEnabled = list.isEnabled;
            //font = list.font;
        }

        /*
        border = if (cellHasFocus) {
            UIManager.getBorder("List.focusCellHighlightBorder")
        }  else {
            noFocusBorder
        }
         */

        value?.let {
            text = it.asShortText()
        }
        return this
    }
}