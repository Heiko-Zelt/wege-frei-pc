package de.heikozelt.wegefrei.gui

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MiniPhotoPanelMouseListener(private val mainFrame: MainFrame, private val miniPhotoPanel: MiniPhotoPanel): MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        mainFrame?.showPhoto(miniPhotoPanel)
    }

    override fun mousePressed(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
}