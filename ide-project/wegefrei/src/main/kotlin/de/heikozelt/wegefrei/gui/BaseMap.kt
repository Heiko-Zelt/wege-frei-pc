package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
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
    private val photoMarkers = LinkedList<PhotoMarker>()
    private var offenseMarker: OffenseMarker? = null
    private var selectedPhotos = noticeFrame.getSelectedPhotos()

    init {
        log.debug("init")
        val info = OSMTileFactoryInfo()
        tileFactory = DefaultTileFactory(info)
        overlayPainter = painter
    }

    private fun updatePainterWaypoints() {
        val markers = mutableSetOf<Marker>()
        markers.addAll(photoMarkers)
        val aM = offenseMarker
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

        // todo nicht immer alles notwendig
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

        // todo nicht immer alles notwendig
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
        log.debug("replacedPhotoSelection(photos.size=${photos.size}")
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

        updatePainterWaypoints()
        fitToMarkers()
    }

    /**
     * todo: Wann werden width und height initialisiert? Vorher revalidate() aufrufen?
     */
    fun fitToMarkers() {
        log.debug("fitToMarkers()")
        // 2 identische Geo-Positionen können keine Bounding Box mit Ausdehnung bilden
        // daher Set statt List
        val fitPoints = HashSet<GeoPosition>()
        for (marker in photoMarkers) {
            fitPoints.add(marker.position)
        }
        offenseMarker?.let {
            fitPoints.add(it.position)
        }

        fitPoints.forEach {
            log.debug("fitPoint: $it")
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
                log.debug("zoom vor zoomToBestFit(): $zoom")
                // todo Prio 2: Bug in JXMapViewer2 beheben, 2 mal gleiche Latitude/Longitude
                log.debug("width: $width, height: $height" )
                zoomToBestFit(fitPoints, 0.5)
                log.debug("zoom nach zoomToBestFit(): $zoom")
            }
        }
    }

    /**
     * Die Methode setzt den AddressMarker neu, oder entfernt ihn.
     * Sie wird für die MaxiMap von NoticeFrame aufgerufen.
     * Für MiniMap indirekt von NoticeFormFields.
     */
    fun setOffensePosition(offensePosition: GeoPosition?) {
        if(offensePosition == null) {
            offenseMarker?.let {
                remove(it.getLabel())
                offenseMarker = null
            }
        } else {
            val oM = offenseMarker
            if (oM == null) {
                offenseMarker = OffenseMarker(offensePosition)
                offenseMarker?.let {
                    add(it.getLabel(), 0)
                }
            } else {
                oM.position = offensePosition
            }
        }
        updatePainterWaypoints()
        fitToMarkers()
        revalidate()
        repaint()
    }
}