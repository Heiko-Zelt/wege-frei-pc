package de.heikozelt.wegefrei.maps

import org.slf4j.LoggerFactory
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class OffenseMarkerMouseListener: MouseAdapter() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    override fun mousePressed(e: MouseEvent?) {
        if(e == null) return
        log.debug("mousePressed(x=${e.x}, y=${e.y})")
    }

    /**
     * Maus-Zieh-Ereignisse werden nur erfasst,
     * wenn der Event-Dispatcher-Thread (EDT) nicht beschäftigt ist.
     * Je nachdem, gibt es also mehr oder weniger Maus-Zieh-Events. :-)
     */
    override fun mouseDragged(e: MouseEvent?) {
        if(e == null) return
        log.debug("mouseDragged(x=${e.x}, y=${e.y})")

    }

}