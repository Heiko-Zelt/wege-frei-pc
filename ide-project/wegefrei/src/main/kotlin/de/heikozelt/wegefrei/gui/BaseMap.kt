package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.util.*

/**
 * Beispiele für components:
 * { } leer
 * { photoMarker(0) } merkwürdiger Fall
 * { addressMarker } keine Geo-Location in Foto? manuell gesetzt?
 * { photoMarker(2), photoMarker(1), photoMarker(0) } merkwürdiger Fall
 * { addressMarker, photoMarker(2), photoMarker(1), photoMarker(0) } Perfekter Use Case
 * Sie werden in umgekehrter Reihenfolge gezeichnet.
 * Letzter Marker unten, erster Marker ganz obendrauf.
 *
 * Die Karte ist Anfangs beim Konstruktor-Aufruf komplett leer.
 * Erst mit load()-Data wird ggf. ein Adress-Marker gesetzt und ggf. Foto-Markers hinzugefügt.
 * Die Foto-Markers werden indirekt über das Observer-Pattern hinzugefügt oder entfernt.
 *
 * todo: Prio 1: Gemeinsamkeiten von MiniMap und MaxiMap in BaseMap-Klasse extrahieren
 */
open class BaseMap(
    private val noticeFrame: NoticeFrame
) : JXMapViewer(), SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val painter = MarkerPainter()
    protected val photoMarkers = LinkedList<PhotoMarker>()
    protected var addressMarker: AddressMarker? = null
    protected var selectedPhotos = noticeFrame.getSelectedPhotos()

    init {
        log.debug("init")
        val info = OSMTileFactoryInfo()
        tileFactory = DefaultTileFactory(info)
        addMouseListener(MiniMapMouseListener(noticeFrame))
        overlayPainter = painter
        size = Dimension(150, 150)
        preferredSize = Dimension(150, 150)
    }

    /**
     * Der Adress-Marker wird gesetzt, falls vorhanden.
     * Die Foto-Markers werden indirekt über Observer-Pattern aktualisiert.
     */
    fun setAddrLocation(addressLocation: GeoPosition?) {
        log.debug("setAddrLocation")

        if (addressLocation == null) {
            addressMarker?.let {
                remove(it.getLabel())
                addressMarker = null
            }
        } else {
            addressMarker?.let {
                it.position = addressLocation
            } ?: run {
                val newAddressMarker = AddressMarker(addressLocation)
                add(newAddressMarker.getLabel(), 0)
                addressMarker = newAddressMarker
            }
        }

        updatePainterWaypoints()
        fitToMarkers()
    }

    private fun updatePainterWaypoints() {
        val markers = mutableSetOf<Marker>()
        markers.addAll(photoMarkers)
        val aM = addressMarker
        if (aM != null) {
            markers.add(aM)
        }
        painter.waypoints = markers
    }

    /**
     * Observer-Methode
     * index zählt intern ab 0, in der Marker-Darstellung aber ab "1"
     */
    override fun selectedPhoto(index: Int, photo: Photo) {
        log.debug("selectedPhoto(index=$index)")

        // Marker hinzufügen, falls Geo-Position vorhanden ist
        val markerIndex = selectedPhotos.calculateMarkerIndex(index)
        log.debug("markerIndex: $markerIndex")

        // Bei allen darauffolgenden Photos/Markers die angezeigte Nummer um eins erhöhen
        for (i in markerIndex until photoMarkers.size) {
            log.debug("increment marker $i")
            photoMarkers[i].incrementPhotoIndex()
        }

        val pos = photo.getGeoPosition()
        if (pos != null) {
            log.debug("add waypoint")
            val marker = PhotoMarker(index, pos)
            photoMarkers.add(markerIndex, marker)

            log.debug("number of photo markers: " + photoMarkers.size)
            val compIndex = componentCount - markerIndex
            add(marker.getLabel(), compIndex)
        }

        //updateAddressLocation()
        updatePainterWaypoints()
        fitToMarkers()
        revalidate()
        repaint()

        for (comp in components) {
            log.debug("comp: $comp")
        }
        for (m in photoMarkers) {
            log.debug("photoMarker: ${m.getLabel().text}")
        }
    }



    /**
     * Observer-Methode
     */
    override fun unselectedPhoto(index: Int, photo: Photo) {
        log.debug("unselectedPhoto(index=$index)")

        val markerIndex = selectedPhotos.calculateMarkerIndex(index)
        log.debug("markerIndex: $markerIndex")

        // Marker und dessen Label entfernen, falls er/es existiert hat
        if (photo.getGeoPosition() != null) {
            remove(photoMarkers[markerIndex].getLabel())
            photoMarkers.removeAt(markerIndex)
        }

        log.debug("number of photo markers: " + photoMarkers.size)
        // Bei allen darauffolgenden Photos/Markers die angezeigte Nummer um eins erniedrigen
        for (i in markerIndex until photoMarkers.size) {
            photoMarkers[i].decrementPhotoIndex()
        }
        //updateAddressLocation()
        updatePainterWaypoints()
        fitToMarkers()
        revalidate()
        repaint()
        for (comp in components) {
            log.debug("comp: $comp")
        }
        for (m in photoMarkers) {
            log.debug("photoMarker: ${m.getLabel().text}")
        }
    }

    /**
     * alle Foto-Markers müssen ersetzt werden
     */
    override fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        // alle bestehenden Foto-Marker entfernen
        // (aber den ggf. existieren Adress-Marker behalten)
        for (photoMarker in photoMarkers) {
            remove(photoMarker.getLabel())
        }
        photoMarkers.clear()

        for ((i, photo) in photos.withIndex()) {
            var position = photo.getGeoPosition()
            if (position != null) {
                log.debug("add photo marker #$i")
                photoMarkers.add(PhotoMarker(i, position))
            }
        }

        // neue Foto-Markers zum Container hinzufügen
        // (in umgekehrter Reihenfolge, wegen Z-Order)
        val iterator = photoMarkers.descendingIterator()
        while (iterator.hasNext()) { // wird nie durchlaufen
            log.debug("add to panel")
            add(iterator.next().getLabel())
        }

        // ggf. Adress-Location anpassen und Karten-Bereich neu justieren
        // todo adress-location nur updaten, wenn Foto hinzugefügt oder entfernt wird, nicht hier
        //updateAddressLocation()
        updatePainterWaypoints()
        fitToMarkers()
    }

    private fun fitToMarkers() {
        log.debug("fitToMarkers()")
        // 2 identische Geo-Positionen können keine Bounding Box mit Ausdehnung bilden
        // daher Set statt List
        val fitPoints = HashSet<GeoPosition>()
        for (marker in photoMarkers) {
            fitPoints.add(marker.position)
        }
        val aM = addressMarker
        if (aM != null) {
            fitPoints.add(aM.position)
        }

        fitPoints.forEach {
            log.debug("fitPoint: $")
        }

        when (fitPoints.size) {
            0 -> {
                zoom = 15 // kleine Zahl = Details, große Zahl = Übersicht
                addressLocation = GeoPosition(50.11, 8.68) // Frankfurt
            }

            1 -> {
                zoom = 3 // kleine Zahl = Details, große Zahl = Übersicht
                addressLocation = fitPoints.first()
            }

            else -> {
                log.debug("zoom: $zoom")
                zoomToBestFit(fitPoints, 0.4)
                log.debug("zoom: $zoom")
            }
        }
    }

}