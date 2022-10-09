package de.heikozelt.wegefrei.gui

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MiniSelectedPhotoPanelMouseListener(private val noticeFrame: NoticeFrame, private val miniSelectedPhotoPanel: MiniSelectedPhotoPanel): MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        noticeFrame.showSelectedPhoto(miniSelectedPhotoPanel)
    }

    override fun mousePressed(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
}