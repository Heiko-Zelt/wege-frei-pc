package de.heikozelt.wegefrei.gui

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class MiniMapMouseAdapter(private val noticeFrame: NoticeFrame): MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
        noticeFrame.showMaxiMap()
    }
}