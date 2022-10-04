package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NORMAL_BORDER
import mu.KotlinLogging
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter
import org.jxmapviewer.viewer.*
import java.awt.Dimension
import java.awt.event.MouseListener
import javax.swing.event.MouseInputAdapter

class MiniMap(private val mainFrame: MainFrame): JXMapViewer() {

    private val log = KotlinLogging.logger {}

    private var borderVisible = false

    init {
        border = NORMAL_BORDER

        val info = OSMTileFactoryInfo()
        tileFactory = DefaultTileFactory(info)
        //val mm = PanMouseInputListener(this)
        //val mw = ZoomMouseWheelListenerCenter(this)
        //addMouseListener(mm)
        //addMouseMotionListener(mm)
        //addMouseWheelListener (mw)

        addMouseListener(MiniMapMouseListener(mainFrame))

        val frankfurt = GeoPosition(50.11, 8.68)
        val wiesbaden = GeoPosition(50, 5, 0, 8, 14, 0)
        val mainz = GeoPosition(50, 0, 0, 8, 16, 0)

        val fitPoints = HashSet<GeoPosition>()
        fitPoints.add(frankfurt)
        fitPoints.add(wiesbaden)
        fitPoints.add(mainz)
        log.debug("zoom: " + zoom)
        size = Dimension(100,100)
        zoomToBestFit(fitPoints, 0.9)
        log.debug("zoom: " + zoom)

        //map.zoom = 11 // kleine Zahl = Details, große Zahl = Übersicht
        //map.addressLocation = frankfurt

        val waypoints = HashSet<Waypoint>()
        waypoints.add(DefaultWaypoint(frankfurt))
        waypoints.add(DefaultWaypoint(wiesbaden))
        waypoints.add(DefaultWaypoint(mainz))
        val painter = WaypointPainter<Waypoint>()
        painter.waypoints = waypoints
        overlayPainter = painter
        preferredSize = Dimension(200, 200)
    }

    fun displayBorder(visible: Boolean) {
        if(visible && !borderVisible) {
            border = MainFrame.HIGHLIGHT_BORDER
            revalidate()
            borderVisible = true
        } else if(!visible && borderVisible) {
            border = NORMAL_BORDER
            revalidate()
            borderVisible = false
        }
    }

    fun addMarker(photo: Photo) {
        val pos = photo.getGeoPosition()
        if(pos != null) {
            // todo: add marker
        }
    }
}