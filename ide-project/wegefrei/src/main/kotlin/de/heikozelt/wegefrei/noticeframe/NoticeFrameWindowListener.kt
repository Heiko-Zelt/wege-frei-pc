package de.heikozelt.wegefrei.noticeframe

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class NoticeFrameWindowListener(private val noticeFrame: NoticeFrame): WindowAdapter() {
    override fun windowClosing(e: WindowEvent?) {
        // super.windowClosing(e) calling empty super method doesn't make any sense
        noticeFrame.saveAndClose()
    }
}