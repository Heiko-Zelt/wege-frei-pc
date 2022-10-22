package de.heikozelt.wegefrei.maps

import org.slf4j.LoggerFactory
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

/**
 * MouseAdapter implements:
 * <ul>
 *   <li>MouseListener with mouseClicked(e) and</li>
 *   <li>MouseMotionListener with mouseDragged(e)</li>
 * </ul>
 */
class OffenseMarkerMouseListener(private val maxiMap: MaxiMap) : MouseAdapter() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * in Pixel ab der ViewPort-Ecke
     */
    private var startPoint: Point? = null

    override fun mousePressed(e: MouseEvent?) {
        if (e == null) return
        log.debug("mousePressed(point=${e.point})")
        startPoint = e.point
        /*
        // relative Position innerhalb des Labels
        // e.point
        maxiMap.getOffnsMarker()?.let { marker ->
            marker?.getLabel()?.let { label ->
                startPoint = label.location
                log.debug("startPoint: $startPoint")
            }
        }
         */
    }

    /**
     * Maus-Zieh-Ereignisse werden nur erfasst,
     * wenn der Event-Dispatcher-Thread (EDT) nicht beschäftigt ist.
     * Je nachdem, gibt es also mehr oder weniger Maus-Zieh-Events. :-)
     *
     * e.point bezieht sich auf die Ecke des Labels.
     * e.point ändert sich, wenn das Label verschoben wird.
     * Wenn Mauszeiger und Label sich gleich schnell bewegen,
     * dann bleibt e.point konstant.
     */
    override fun mouseDragged(e: MouseEvent?) {
        if (e == null) return
        log.debug("mouseDragged(point=${e.point}")

        startPoint?.let { sPoint ->
            val deltaX = e.x - sPoint.x
            val deltaY = e.y - sPoint.y
            log.debug("delta x=$deltaX, y=$deltaY")
            maxiMap.getOffnsMarker()?.let { marker ->
                val label = marker.getLabel()
                val newPoint = Point(label.x + deltaX + label.width / 2, label.y + deltaY + label.height)
                log.debug("label x=${label.x}, y=${label.y} ---> $newPoint")
                //label.setLocation(label.x + deltaX, label.y + deltaY)

                marker.position = maxiMap.pixelToGeo(newPoint)
                maxiMap.repaint()
            }
        }
    }
}