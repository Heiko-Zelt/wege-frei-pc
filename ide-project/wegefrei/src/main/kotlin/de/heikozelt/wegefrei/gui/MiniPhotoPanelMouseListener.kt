package de.heikozelt.wegefrei.gui

import org.slf4j.LoggerFactory
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class MiniPhotoPanelMouseListener(private val noticeFrame: NoticeFrame, private val miniPhotoPanel: MiniPhotoPanel): MouseAdapter() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    override fun mouseClicked(e: MouseEvent) {
        // todo: Bug: Doppelklick führt manchmal zu Fehlern
        if(e.clickCount == 1) {
            log.debug("einfacher Klick, nur zoomen")
            noticeFrame.showPhoto(miniPhotoPanel)
        } else { //
            log.debug("Doppelklick, zoomen und auswählen")
            miniPhotoPanel.selectPhoto()
        }
    }
}