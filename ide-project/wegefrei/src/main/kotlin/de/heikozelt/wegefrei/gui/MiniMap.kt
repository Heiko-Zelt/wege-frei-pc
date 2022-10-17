package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

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
 */
class MiniMap(
    private val noticeFrame: NoticeFrame
) : JXMapViewer(), SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var borderVisible = false
    private val painter = MarkerPainter()
    private val photoMarkers = LinkedList<PhotoMarker>()
    private var addressMarker: AddressMarker? = null
    private var selectedPhotos = noticeFrame.getSelectedPhotos()

    init {
        log.debug("init")
        border = NORMAL_BORDER
        val info = OSMTileFactoryInfo()
        tileFactory = DefaultTileFactory(info)
        addMouseListener(MiniMapMouseAdapter(noticeFrame))
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
            } ?:run {
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

    fun displayBorder(visible: Boolean) {
        if (visible && !borderVisible) {
            border = HIGHLIGHT_BORDER
            revalidate()
            borderVisible = true
        } else if (!visible && borderVisible) {
            border = NORMAL_BORDER
            revalidate()
            borderVisible = false
        }
    }

    /**
     * Observer-Methode
     * index zählt intern ab 0, in der Marker-Darstellung aber ab "1"
     */
    override fun selectedPhoto(index: Int, photo: Photo) {
        log.debug("addedPhoto()")
        val pos = photo.getGeoPosition()
        if (pos != null) {
            log.debug("add waypoint")
            val marker = PhotoMarker(index, pos)
            photoMarkers.add(index, marker)

            log.debug("number of photo markers: " + photoMarkers.size)
            val compIndex = componentCount - index
            add(marker.getLabel(), compIndex)
            for (i in index + 1 until photoMarkers.size) {
                photoMarkers[i].updateText(i)
            }
            updateAddressLocation()
            updatePainterWaypoints()
            fitToMarkers()
            revalidate()
            repaint()
        }
        for (comp in components) {
            log.debug("comp: $comp")
        }
    }

    /**
     * Observer-Methode
     */
    override fun unselectedPhoto(index: Int, photo: Photo) {
        log.debug("remove waypoint")
        remove(photoMarkers[index].getLabel())
        photoMarkers.removeAt(index)
        log.debug("number of photo markers: " + photoMarkers.size)
        for (i in index until photoMarkers.size) {
            photoMarkers[i].updateText(i)
        }
        updateAddressLocation()
        updatePainterWaypoints()
        fitToMarkers()
        revalidate()
        repaint()
        for (comp in components) {
            log.debug("comp: $comp")
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

        // neue Foto-Markers zur Liste hinzufügen
        val photosIter = selectedPhotos.getPhotos().iterator()
        var i = 0
        while(photosIter.hasNext()) {
            log.debug("add photo marker #$i")
            photoMarkers.add(PhotoMarker(i, photosIter.next().getGeoPosition()))
            i++
        }

        // neue Foto-Markers zum Container hinzufügen
        // (in umgekehrter Reihenfolge, wegen Z-Order)
        val iterator = photoMarkers.descendingIterator()
        while (iterator.hasNext()) { // wird nie durchlaufen
            log.debug("add to panel")
            add(iterator.next().getLabel())
        }

        // ggf. Adress-Location anpassen und Karten-Bereich neu justieren
        updateAddressLocation()
        updatePainterWaypoints()
        fitToMarkers()
    }

    private fun fitToMarkers() {
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

    /**
     * setzt die Address-Position automatisch auf den Mittelpunkt
     * der Foto-Geo-Koordinaten
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