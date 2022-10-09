package de.heikozelt.wegefrei.gui

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MiniMapMouseListener(private val noticeFrame: NoticeFrame): MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        noticeFrame.showMaxiMap()
    }

    override fun mousePressed(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
}