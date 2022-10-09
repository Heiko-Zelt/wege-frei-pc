package de.heikozelt.wegefrei.gui

import org.jxmapviewer.viewer.DefaultWaypoint
import org.jxmapviewer.viewer.GeoPosition
import javax.swing.JLabel


open class Marker(coord: GeoPosition?) : DefaultWaypoint(coord) {

    protected val lbl = JLabel()

    init {
        lbl.isVisible = true
    }

    fun getLabel(): JLabel {
        return lbl
    }
}