package de.heikozelt.wegefrei.gui

import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.viewer.WaypointPainter
import java.awt.Graphics2D

class MarkerPainter: WaypointPainter<Marker>() {

    /**
     * berechnet nur die Position der Marker/Buttons
     */
    override fun doPaint(g: Graphics2D?, jxMapViewer: JXMapViewer, width: Int, height: Int) {
        for (waypoint in waypoints) {
            val point = jxMapViewer.tileFactory.geoToPixel(waypoint.position, jxMapViewer.zoom)
            val rectangle = jxMapViewer.viewportBounds
            val x = (point.x - rectangle.getX()).toInt()
            val y = (point.y - rectangle.getY()).toInt()
            val label = waypoint.getLabel()
            val labelX = x - label.width / 2 // zentriert
            val labelY = if(waypoint is OffenseMarker) {
                y - label.height // Unterkante
            } else { // is PhotoMarker
                y - label.height / 2 // mittig
            }
            label.setLocation(labelX, labelY)
        }
    }
}