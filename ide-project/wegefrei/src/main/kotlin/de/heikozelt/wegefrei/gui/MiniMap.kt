package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NORMAL_BORDER
import mu.KotlinLogging
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.*
import java.awt.Dimension
import java.util.*
import kotlin.collections.HashSet

class MiniMap(private val mainFrame: MainFrame): JXMapViewer() {

    private val log = KotlinLogging.logger {}

    private var borderVisible = false

    //val waypoints = HashSet<Marker>()
    private val photoMarkers = LinkedList<PhotoMarker>()
    private val addressMarker: AddressMarker
    private val painter = MarkerPainter()

    init {
        border = NORMAL_BORDER

        val info = OSMTileFactoryInfo()
        tileFactory = DefaultTileFactory(info)

        addMouseListener(MiniMapMouseListener(mainFrame))

        val frankfurt = GeoPosition(50.11, 8.68)
        val wiesbaden = GeoPosition(50, 5, 0, 8, 14, 0)
        val mainz = GeoPosition(50, 0, 0, 8, 16, 0)
        val posi = GeoPosition(50.1, 8.5)

        //map.zoom = 11 // kleine Zahl = Details, große Zahl = Übersicht
        //map.addressLocation = frankfurt

        addressMarker = AddressMarker(posi)
        photoMarkers.add(PhotoMarker(0, frankfurt))
        photoMarkers.add(PhotoMarker(1, wiesbaden))
        photoMarkers.add(PhotoMarker(2, mainz))

        updatePainterWaypoints()
        overlayPainter = painter

        add(addressMarker.getLabel())
        val iterator = photoMarkers.descendingIterator()
        while(iterator.hasNext()) {
            add(iterator.next().getLabel())
        }

        size = Dimension(150,150)
        preferredSize = Dimension(150, 150)

        fitToMarkers()

        for(comp in components) {
            log.debug("comp: $comp")
        }
    }

    private fun updatePainterWaypoints() {
        val markers = mutableSetOf<Marker>()
        markers.addAll(photoMarkers)
        markers.add(addressMarker)
        painter.waypoints = markers
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

    /**
     * index zählt intern ab 0, in der Marker-Darstellung aber ab 1
     */
    fun addMarker(index: Int, photo: Photo) {
        val pos = photo.getGeoPosition()
        if(pos != null) {
            log.debug("add waypoint")
            val marker = PhotoMarker(index, pos)
            photoMarkers.add(index, marker)
            log.debug("number of photomarkers: " + photoMarkers.size)
            add(marker.getLabel(), photoMarkers.size - index)
            for(i in index + 1 until photoMarkers.size) {
                photoMarkers[i].updateText(i)
            }
            updatePainterWaypoints()
            updateAddressLocation()
            fitToMarkers()
            revalidate()
            //repaint()
        }
        for(comp in components) {
            log.debug("comp: $comp")
        }
    }

    fun removeMarker(index: Int) {
        log.debug("remove waypoint")
        remove(photoMarkers[index].getLabel())
        photoMarkers.removeAt(index)
        log.debug("number of photo markers: " + photoMarkers.size)
        for(i in index until photoMarkers.size) {
           photoMarkers[i].updateText(i)
        }
        updatePainterWaypoints()
        updateAddressLocation()
        fitToMarkers()
        revalidate()
        repaint()
        for(comp in components) {
            log.debug("comp: $comp")
        }
    }

    private fun fitToMarkers() {
        if(photoMarkers.size > 1) {
            log.debug("zoom: $zoom")
            val fitPoints = HashSet<GeoPosition>()
            for(marker in photoMarkers) {
                fitPoints.add(marker.position)
            }
            fitPoints.add(addressMarker.position)
            zoomToBestFit(fitPoints, 0.8)
            log.debug("zoom: $zoom")
        } else {
            // todo standard zoom level
        }
    }

    private fun updateAddressLocation() {
        if(photoMarkers.size == 0) {
            //todo adress marker löschen?
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
            addressMarker.position = GeoPosition(newLatitude, newLongitude)
        }
    }
}