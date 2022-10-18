package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.sqrt

class MaxiMap(private val noticeFrame: NoticeFrame): BaseMap(noticeFrame) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        border = Styles.NO_BORDER
        val mm = PanMouseInputListener(this)
        val mw = ZoomMouseWheelListenerCenter(this)
        addMouseListener(mm)
        addMouseMotionListener(mm)
        addMouseWheelListener (mw)
    }

    override fun selectedPhoto(index: Int, photo: Photo) {
        super.selectedPhoto(index, photo)
        updateAddressLocation()
    }

    override fun unselectedPhoto(index: Int, photo: Photo) {
        super.unselectedPhoto(index, photo)
        updateAddressLocation()
    }

    /*
    init {
        val info = OSMTileFactoryInfo()
        tileFactory = DefaultTileFactory(info)
        val mm = PanMouseInputListener(this)
        val mw = ZoomMouseWheelListenerCenter(this)
        addMouseListener(mm)
        addMouseMotionListener(mm)
        addMouseWheelListener (mw)

        val frankfurt = GeoPosition(50.11, 8.68)
        val wiesbaden = GeoPosition(50, 5, 0, 8, 14, 0)
        val mainz = GeoPosition(50, 0, 0, 8, 16, 0)

        val fitPoints = HashSet<GeoPosition>()
        fitPoints.add(frankfurt)
        fitPoints.add(wiesbaden)
        fitPoints.add(mainz)
        log.debug("zoom: " + zoom)
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
    }
     */

    /**
     * setzt die Address-Position automatisch auf den Mittelpunkt
     * der Foto-Geo-Koordinaten
     * MaxiMap ist verantwortlich die Addresse-Position zu ändern
     */
    private fun updateAddressLocation() {
        if (photoMarkers.size == 0) {
            // todo: address marker entfernen, oder nicht?
        } else {
            val latitudes = mutableListOf<Double>()
            for (marker in photoMarkers)
                latitudes.add(marker.position.latitude)
            val newLatitude = latitudes.average()
            log.debug("newLatitude: $newLatitude")

            val longitudes = mutableListOf<Double>()
            for (marker in photoMarkers)
                longitudes.add(marker.position.longitude)
            val newLongitude = longitudes.average()
            log.debug("newLongitude: $newLongitude")

            val oldPosition = addressMarker?.position
            val newPosition = GeoPosition(newLatitude, newLongitude)
            val aM = addressMarker
            if (aM == null) {
                addressMarker = AddressMarker(newPosition)
                addressMarker?.let {
                    add(it.getLabel(), 0)
                }
            } else {
                aM.position = newPosition
            }

            // aus Performance-Gründen:
            // bei nur minimalen Abweichungen keine neu Addresse suchen
            if (oldPosition == null || distance(oldPosition, newPosition) > NEARBY_DEGREES) {
                log.info("findAddress()")
                noticeFrame.findAddress(newPosition)
            }

        }
    }

    /**
     * Berechnet die Distanz zwischen 2 Punkten nach dem Satz vom Pythagoras
     * Die Erdkrümmung wird nicht berücksichtigt
     * todo: die Erdkrümmung berücksichtigen
     */
    private fun distance(positionA: GeoPosition, positionB: GeoPosition): Double {
        // a = betrag von ( A.longitudeA - B.longitude )
        // b = betrag von ( A.latitude - B.latitude )
        // c = wurzel aus ( a im quadrat + b im quadrat)
        val a = abs(positionA.longitude - positionB.longitude)
        val b = abs(positionA.latitude - positionA.latitude)
        val c = sqrt(a * a + b * b)
        val num1 = " %.7f".format(NEARBY_DEGREES)
        val num2 = " %.7f".format(c)
        log.debug("Schwellwert: $num1, distance = $num2")
        return c
    }

    companion object {
        private const val EARTH_CIRCUMFERENCE = 40_075_000.0 // meters
        private const val WHOLE_CIRCLE = 360.0 // degrees
        private const val NEARBY_METERS = 6.0 // meters
        const val NEARBY_DEGREES = NEARBY_METERS * WHOLE_CIRCLE / EARTH_CIRCUMFERENCE
    }

}