package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.Styles.Companion.FORM_BACKGROUND
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

/**
 * Meldungs-Formular mit Eingabe-Feldern und Buttonleiste
 */
class NoticeForm(private val noticeFrame: NoticeFrame) : JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val noticeFormFields = NoticeFormFields(noticeFrame)
    private val noticeFormButtonBar = NoticeFormButtonsBar(noticeFrame)
    private var noticeFormFieldsScrollPane = JScrollPane(noticeFormFields)

    init {
        layout = BorderLayout()
        background = FORM_BACKGROUND
        border = NO_BORDER

        add(noticeFormFieldsScrollPane, BorderLayout.CENTER)
        add(noticeFormButtonBar, BorderLayout.SOUTH)

        isVisible = true
    }

    fun getNoticeFormFields(): NoticeFormFields {
        return noticeFormFields
    }

    fun disableFormFields() {
        noticeFormFields.disableFormFields()
    }
}