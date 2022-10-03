package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.ZOOM_PANEL_BACKGROUND
import mu.KotlinLogging
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter
import org.jxmapviewer.viewer.*
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel

class MaxiMap: JPanel() {

    private val log = KotlinLogging.logger {}

    init {
        background = ZOOM_PANEL_BACKGROUND
        border = NO_BORDER
        layout = GridBagLayout();
        val constraints = GridBagConstraints()
        constraints.anchor = GridBagConstraints.CENTER
        constraints.fill = GridBagConstraints.BOTH
        constraints.gridwidth = 2
        constraints.gridx = 0
        constraints.gridy = 0

        val map = JXMapViewer()
        val info = OSMTileFactoryInfo()
        map.tileFactory = DefaultTileFactory(info)
        val mm = PanMouseInputListener(map)
        val mw = ZoomMouseWheelListenerCenter(map)
        map.addMouseListener(mm)
        map.addMouseMotionListener(mm)
        map.addMouseWheelListener (mw)

        val frankfurt = GeoPosition(50.11, 8.68)
        val wiesbaden = GeoPosition(50, 5, 0, 8, 14, 0)
        val mainz = GeoPosition(50, 0, 0, 8, 16, 0)

        val fitPoints = HashSet<GeoPosition>()
        fitPoints.add(frankfurt)
        fitPoints.add(wiesbaden)
        fitPoints.add(mainz)
        log.debug("zoom: " + map.zoom)
        map.size = Dimension(600,600)
        map.zoomToBestFit(fitPoints, 0.9)
        log.debug("zoom: " + map.zoom)

        //map.zoom = 11 // kleine Zahl = Details, große Zahl = Übersicht
        //map.addressLocation = frankfurt

        val waypoints = HashSet<Waypoint>()
        waypoints.add(DefaultWaypoint(frankfurt))
        waypoints.add(DefaultWaypoint(wiesbaden))
        waypoints.add(DefaultWaypoint(mainz))
        val painter = WaypointPainter<Waypoint>()
        painter.waypoints = waypoints
        map.overlayPainter = painter
        map.preferredSize = Dimension(600, 600)

        constraints.weighty= 1.0
        add(map, constraints)

        constraints.gridwidth = 1
        constraints.weighty= 0.1
        constraints.gridy = 1
        add(JButton("anpassen"), constraints)

        constraints.gridx = 1
        add(JButton("zentrieren"), constraints)
    }
}