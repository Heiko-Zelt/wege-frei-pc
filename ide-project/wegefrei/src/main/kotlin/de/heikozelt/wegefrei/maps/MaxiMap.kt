package de.heikozelt.wegefrei.maps

import de.heikozelt.wegefrei.gui.NoticeFrame
import de.heikozelt.wegefrei.gui.Styles
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory

/**
 * Große Karte.
 * Mausrad zum rein- und rauszoomen.
 * Maus ziehen, um Ausschnitt zu ändern.
 * Der Adress-Marker kann manuell geändert werden.
 */
class MaxiMap(private val noticeFrame: NoticeFrame): BaseMap(noticeFrame) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val mickey = OffenseMarkerMouseListener(this)

    init {
        log.debug("init")
        border = Styles.NO_BORDER
        val mm = PanMouseInputListener(this)
        val mw = ZoomMouseWheelListenerCenter(this)
        addMouseListener(mm)
        addMouseMotionListener(mm)
        addMouseWheelListener(mw)
    }

    override fun setOffensePosition(offensePosition: GeoPosition?) {
        log.debug("maxi.setOffensePosition(${offensePosition.toString()}")
        super.setOffensePosition(offensePosition)

        // only in MaxiMap, not in MiniMap
        // could as well be solved with a factory method and polymorphism
        getOffenseMarker()?.getLabel()?.let {
            log.debug("add mouse listeners")
            it.addMouseListener(mickey)
            it.addMouseMotionListener(mickey)
        }
    }

    /**
     * todo Prio 3: Editing should be disabled by default, and there should be a method to enable it.
     */
    fun disableDragAndDrop() {
        getOffenseMarker()?.getLabel()?.let {
            log.debug("remove mouse listeners")
            it.removeMouseListener(mickey)
            it.removeMouseMotionListener(mickey)
        }
    }

    fun changedOffenseMarker() {
        getOffenseMarker()?.let {
            noticeFrame.setOffensePosition(it.position)
        }
    }

}