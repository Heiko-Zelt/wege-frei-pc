package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.Styles.Companion.FORM_BACKGROUND
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import mu.KotlinLogging
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * todo: Button Meldung löschen
 */
class NoticeForm(private val noticeFrame: NoticeFrame) : JPanel() {

    private val log = KotlinLogging.logger {}
    private val noticeFormFields = NoticeFormFields(noticeFrame)
    private val noticeFormToolBar = NoticeFormToolBar(noticeFrame)

    init {
        layout = BorderLayout()
        background = FORM_BACKGROUND
        border = NO_BORDER

        add(noticeFormFields, BorderLayout.CENTER)
        add(noticeFormToolBar, BorderLayout.SOUTH)

        isVisible = true
    }

    fun getNoticeFormFields(): NoticeFormFields {
        return noticeFormFields
    }
}