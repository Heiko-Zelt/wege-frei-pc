package de.heikozelt.wegefrei.maps

import org.jxmapviewer.viewer.DefaultWaypoint
import org.jxmapviewer.viewer.GeoPosition
import javax.swing.JLabel

/**
 * A DefaultWaypoint has just a position.
 * This class adds a label.
 */
open class Marker(coord: GeoPosition?) : DefaultWaypoint(coord) {

    protected val lbl = JLabel()

    init {
        lbl.isVisible = true
    }

    fun getLabel(): JLabel {
        return lbl
    }
}