package de.heikozelt.wegefrei.maps

import de.heikozelt.wegefrei.gui.NoticeFrame
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class MiniMapMouseListener(private val noticeFrame: NoticeFrame): MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
        noticeFrame.showMaxiMap()
    }
}