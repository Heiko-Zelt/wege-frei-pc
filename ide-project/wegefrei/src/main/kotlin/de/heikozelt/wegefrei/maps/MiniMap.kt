package de.heikozelt.wegefrei.maps

import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import org.slf4j.LoggerFactory
import java.awt.Dimension

/**
 * Die kleine Miniatur-Karte wird direkt im Formular angezeigt.
 * Sie kann nicht direkt benutzt werden.
 * Ein Klick auf die Mini-Karte öffnet die große Karte.
 * Dann erhält die Mini-Karte eine hervorgehobenen Rahmen.
 */
class MiniMap(
    private val noticeFrame: NoticeFrame
) : BaseMap(noticeFrame), SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var borderVisible = false

    init {
        log.debug("init")
        border = NORMAL_BORDER
        preferredSize = Dimension(130, 130)
        minimumSize = preferredSize
        maximumSize = preferredSize
        addMouseListener(MiniMapMouseListener(noticeFrame))
    }

    /**
     * Rahmen hervorheben oder nicht
     */
    fun displayBorder(visible: Boolean) {
        if (visible && !borderVisible) {
            border = HIGHLIGHT_BORDER
            revalidate()
            borderVisible = true
        } else if (!visible && borderVisible) {
            border = NORMAL_BORDER
            revalidate()
            borderVisible = false
        }
    }
}