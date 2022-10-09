package de.heikozelt.wegefrei.gui

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MiniPhotoPanelMouseListener(private val noticeFrame: NoticeFrame, private val miniPhotoPanel: MiniPhotoPanel): MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        noticeFrame.showPhoto(miniPhotoPanel)
    }

    override fun mousePressed(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
}