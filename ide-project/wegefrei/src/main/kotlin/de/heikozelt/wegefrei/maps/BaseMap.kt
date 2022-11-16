package de.heikozelt.wegefrei.maps

import de.heikozelt.wegefrei.model.Photo
import de.heikozelt.wegefrei.model.SelectedPhotosListModel
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.Graphics
import java.awt.Point
import java.util.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

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
 */
open class BaseMap(
    private val noticeFrame: NoticeFrame,
    private val selectedPhotosListModel: SelectedPhotosListModel
) : JXMapViewer(), ListDataListener {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val photoMarkers = LinkedList<PhotoMarker>()
    private var offenseMarker: OffenseMarker? = null


    init {
        log.debug("init")
        val info = OSMTileFactoryInfo()
        tileFactory = DefaultTileFactory(info)
    }

    fun pixelToGeo(point: Point): GeoPosition {
        val x = (point.x + viewportBounds.getX()).toInt()
        val y = (point.y + viewportBounds.getY()).toInt()
        val position = tileFactory.pixelToGeo(Point(x, y), zoom)
        return position
    }

    fun geoToPixel(position: GeoPosition): Point {
        val point = tileFactory.geoToPixel(position, zoom)
        val x = (point.x - viewportBounds.getX()).toInt()
        val y = (point.y - viewportBounds.getY()).toInt()
        return Point(x, y)
    }

    // instead of overlayPainter
    override fun paint(g: Graphics) {
        log.debug("paint()")
        for (photoMarker in photoMarkers) {
            val point = geoToPixel(photoMarker.position)
            val label = photoMarker.getLabel()
            val labelX = point.x - label.width / 2 // zentriert
            val labelY = point.y - label.height / 2 // mittig
            label.setLocation(labelX, labelY)
        }
        offenseMarker?.let {
            val point = geoToPixel(it.position)
            val label = it.getLabel()
            val labelX = point.x - label.width / 2 // zentriert
            val labelY = point.y - label.height // Unterkante
            label.setLocation(labelX, labelY)
        }
        super.paint(g)
    }

    /*
     kein Rückgabewert und kein Seiten-Effekt!
    private fun updatePainterWaypoints() {
        val markers = mutableSetOf<Marker>()
        markers.addAll(photoMarkers)
        offenseMarker?.let { markers.add(it) }
    }
     */

    override fun intervalAdded(e: ListDataEvent?) {
        e?.let {event ->
            val index = event.index0
            val source = event.source
            if(source is SelectedPhotosListModel) {
                val photo = source.getElementAt(index)
                photo?.let {
                    selectedPhoto(index, photo)
                }
            }
        }
    }

    /**
     * Observer-Methode
     * index zählt intern ab 0, in der Marker-Darstellung aber ab "1"
     */
    fun selectedPhoto(index: Int, photo: Photo) {
        log.debug("selectedPhoto(index=$index)")

        // Marker hinzufügen, falls Geo-Position vorhanden ist
        val markerIndex = selectedPhotosListModel.calculateMarkerIndex(index)
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
        //updatePainterWaypoints()
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

    override fun intervalRemoved(e: ListDataEvent?) {
        e?.let {event ->
            val index = event.index0
            unselectedPhoto(index)
        }
    }

    /**
     * Observer-Methode
     */
    fun unselectedPhoto(index: Int) {
        log.debug("unselectedPhoto(index=$index)")

        val markerIndex = selectedPhotosListModel.calculateMarkerIndex(index)
        log.debug("markerIndex: $markerIndex")

        // Marker und dessen Label entfernen, falls er/es existiert hat
        val photoMarker = photoMarkers.find { it.getPhotoIndex() == index }
        photoMarker?.let {
            photoMarkers.remove(it)
            remove(it.getLabel())
        }

        log.debug("number of photo markers: " + photoMarkers.size)
        // Bei allen darauffolgenden Photos/Markers die angezeigte Nummer um eins erniedrigen
        for (i in markerIndex until photoMarkers.size) {
            photoMarkers[i].decrementPhotoIndex()
        }

        // todo nicht immer alles notwendig
        //updatePainterWaypoints()
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

    override fun contentsChanged(e: ListDataEvent?) {
        e?.let {event ->
            val source = event.source
            if(source is SelectedPhotosListModel) {
               replacedPhotoSelection(source.getSelectedPhotos())
            }
        }
    }

    /**
     * alle Foto-Markers müssen ersetzt werden
     */
    fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        log.debug("replacedPhotoSelection(photos.size=${photos.size})")
        // alle bestehenden Foto-Marker entfernen
        // (aber den ggf. existieren Adress-Marker behalten)
        // todo bug: Cannot read field "next" because "this.next" is null
        for (photoMarker in photoMarkers) {
            remove(photoMarker.getLabel())
        }
        photoMarkers.clear()

        for ((i, photo) in photos.withIndex()) {
            val position = photo.getGeoPosition()
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

        //updatePainterWaypoints()
        fitToMarkers()
        revalidate()
        repaint()
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
                // todo Prio 3: Bug in JXMapViewer2 behoben, 2 mal gleiche Latitude/Longitude, Wann wird pull request commited?
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
    open fun setOffensePosition(offensePosition: GeoPosition?) {
        log.debug("base.setOffensePosition(${offensePosition.toString()}")
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
        //updatePainterWaypoints()
        fitToMarkers()
        revalidate()
        repaint()
    }

    fun getOffenseMarker(): OffenseMarker? {
        return offenseMarker
    }
}