package de.heikozelt.wegefrei.gui

import org.slf4j.LoggerFactory
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MiniPhotoPanelMouseListener(private val noticeFrame: NoticeFrame, private val miniPhotoPanel: MiniPhotoPanel): MouseListener {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    override fun mouseClicked(e: MouseEvent) {
        if(e.clickCount == 1) {
            log.debug("einfacher Klick, nur zoomen")
            noticeFrame.showPhoto(miniPhotoPanel)
        } else { //
            log.debug("Doppelklick, zoomen und auswählen")
            miniPhotoPanel.selectPhoto()
        }
    }

    override fun mousePressed(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
}