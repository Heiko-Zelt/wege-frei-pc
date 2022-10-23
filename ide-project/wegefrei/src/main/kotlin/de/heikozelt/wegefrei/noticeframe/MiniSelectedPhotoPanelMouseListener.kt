package de.heikozelt.wegefrei.noticeframe

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class MiniSelectedPhotoPanelMouseListener(private val noticeFrame: NoticeFrame, private val miniSelectedPhotoPanel: MiniSelectedPhotoPanel): MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
        // todo: Bug: Doppelklick führt manchmal zu Fehlern
        if(e.clickCount == 1) { // einfacher Klick, nur zoomen
            noticeFrame.showSelectedPhoto(miniSelectedPhotoPanel)
        } else { // Doppelklick, zoomen und auswählen
            miniSelectedPhotoPanel.unselectPhoto()
        }
    }
}