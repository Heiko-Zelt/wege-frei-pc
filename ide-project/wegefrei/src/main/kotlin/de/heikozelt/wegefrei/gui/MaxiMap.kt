package de.heikozelt.wegefrei.gui

import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter
import org.slf4j.LoggerFactory

/**
 * Große Karte.
 * Mausrad zum rein- und rauszoomen.
 * Maus ziehen, um Ausschnitt zu ändern.
 * Der Adress-Marker kann manuell geändert werden.
 */
class MaxiMap(private val noticeFrame: NoticeFrame): BaseMap(noticeFrame) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        log.debug("init")
        border = Styles.NO_BORDER
        val mm = PanMouseInputListener(this)
        val mw = ZoomMouseWheelListenerCenter(this)
        addMouseListener(mm)
        addMouseMotionListener(mm)
        addMouseWheelListener(mw)
    }
}