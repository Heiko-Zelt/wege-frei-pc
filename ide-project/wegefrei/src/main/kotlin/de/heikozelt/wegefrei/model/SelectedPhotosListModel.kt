package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.PhotoEntity
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.lang.Math.min
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.swing.AbstractListModel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener


/**
 */
class SelectedPhotosListModel(
    photoLoader: PhotoLoader
) : AbstractListModel<Photo?>(), PhotoLoaderObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val selectedPhotos = TreeSet<Photo>()

    init {
        photoLoader.registerObserver(this)
    }

    fun add(photo: Photo) {
        log.debug("add(photo: path=${photo.getPath()}, pos.latitude=${photo.getGeoPosition()?.latitude})")
        selectedPhotos.add(photo)
        val index = indexOf(photo)
        fireIntervalAdded(this, index, index, listOf(photo))
    }

    fun remove(photo: Photo) {
        val index = indexOf(photo)
        selectedPhotos.remove(photo)
        fireIntervalRemoved(this, index, index, listOf(photo))
    }

    override fun getSize(): Int {
        return selectedPhotos.size
    }

    override fun doneLoadingFile(photo: Photo) {
        val index = selectedPhotos.indexOf(photo)
        if(index != -1) {
            this.fireContentsChanged(this, index, index)
        }
    }

    override fun doneLoadingEntity(photo: Photo) {
        val index = selectedPhotos.indexOf(photo)
        if(index != -1) {
            this.fireContentsChanged(this, index, index)
        }
    }

    /**
     * Problem: blockiert, die Methode wird im EDT (Thread: "AWT-EventQueue-0") ausgeführt!
     * Lösung: Erst mal einen leeren Rahmen (mit Dateinamen) anzeigen.
     *
     * Fotos cachen?
     */
    override fun getElementAt(index: Int): Photo? {
        log.debug("getElementAt(index=$index)")

        val iter = selectedPhotos.iterator()
        var photo: Photo? = null
        for (i in 0..index) {
            photo = iter.next()
        }
        return photo
    }

    private fun indexOf(photo: Photo): Int {
        val iter = selectedPhotos.iterator()
        var i = 0
        while (iter.hasNext()) {
            if (photo == iter.next()) {
                return i
            }
            i++
        }
        return -1
    }

    /**
     *
     */
    fun setSelectedPhotos(selectedPhotos: TreeSet<Photo>) {
        log.debug("setSelectedPhotos(size=${selectedPhotos.size})")
        val oldSize = this.selectedPhotos.size
        this.selectedPhotos.clear()
        this.selectedPhotos.addAll(selectedPhotos)
        fireContentsChanged(this, 0, min(oldSize, selectedPhotos.size))
        if (selectedPhotos.size > oldSize) {
            fireIntervalAdded(this, oldSize, selectedPhotos.size - 1)
        }
        if (selectedPhotos.size < oldSize) {
            fireIntervalRemoved(this, selectedPhotos.size, oldSize - 1)
        }
    }

    fun getSelectedPhotos(): TreeSet<Photo> {
        return selectedPhotos
    }

    /**
     * Die Methode ist ähnlich GeoBounds(positions).getCenter().
     * Es wird allerdings nicht die Mitte der Bounding-Box zurückgeliefert,
     * sondern jeweils der Durchschnittswert der Breiten- und Längengrade.
     * Ein einzelner Ausreißer hat also weniger Gewicht.
     */
    fun getAveragePosition(): GeoPosition? {
        val latitudes = mutableListOf<Double>()
        val longitudes = mutableListOf<Double>()
        selectedPhotos.forEach { photo ->
            photo.getLatitude()?.let { lat ->
                photo.getLongitude()?.let { lon ->
                    latitudes.add(lat)
                    longitudes.add(lon)
                }
            }
        }
        return if (latitudes.size == 0) {
            null
        } else {
            val newLatitude = latitudes.average()
            val newLongitude = longitudes.average()
            GeoPosition(newLatitude, newLongitude)
        }
    }

    /**
     * Die Methode liefert zu einem Foto mit dem angegebenen Index den entsprechenden Index des Markers.
     * Wenn alle Fotos eine Geo-Position haben, dann ist der Marker-Index gleich dem Foto-Index.
     * Ansonsten kann der Marker-Index kleiner sein als der Foto-Index.
     * Ist der Foto-Index größer als der Index des letzten Fotos, dann wird ein Marker-Index zurückgeliefert,
     * der um eins größer ist als der letzte Marker-Index.
     */
    fun calculateMarkerIndex(photoIndex: Int): Int {
        var markerIndex = 0
        val photoIter = selectedPhotos.iterator()
        var i = 0
        while (photoIter.hasNext() && i < photoIndex) {
            i++
            if (photoIter.next().getGeoPosition() != null) {
                markerIndex++
            }
        }
        return markerIndex
    }

    /**
     * from all photos with a date, get the earliest date
     */
    fun getStartTime(): ZonedDateTime? {
        val firstPhotoWithDate = selectedPhotos.find { it.getDateTime() != null }
        return firstPhotoWithDate?.getDateTime()
    }

    /**
     * from all photos with a date, get the latest date
     */
    fun getEndTime(): ZonedDateTime? {
        val reversedList = selectedPhotos.reversed()
        val lastPhotoWithDate = reversedList.find { it.getDateTime() != null }
        return lastPhotoWithDate?.getDateTime()
    }

    /**
     * in Minutes
     */
    fun getDuration(): Int? {
        val start = getStartTime()
        val end = getEndTime()
        return if (start == null || end == null) {
            null
        } else {
            val unit = ChronoUnit.MINUTES
            unit.between(start, end).toInt()
        }
    }

    private fun fireIntervalAdded(source: Any, index0: Int, index1: Int, photos: List<Photo>) {
        val listeners = listenerList.listenerList
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === ListDataListener::class.java) {
                val e = SelectedPhotosListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1, photos)
                (listeners[i + 1] as ListDataListener).intervalAdded(e)
            }
            i -= 2
        }
    }

    private fun fireIntervalRemoved(source: Any, index0: Int, index1: Int, photos: List<Photo>) {
        val listeners = listenerList.listenerList
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === ListDataListener::class.java) {
                val e = SelectedPhotosListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1, photos)
                (listeners[i + 1] as ListDataListener).intervalRemoved(e)
            }
            i -= 2
        }
    }

    /**
     * is called before saving the notice to the database
     */
    fun getPhotoEntities(): MutableSet<PhotoEntity> {
        val photoEntities = mutableSetOf<PhotoEntity>()
        selectedPhotos.forEach { photo ->
            if(photo.getPhotoEntity() == null) {
                if(photo.getPhotoFile() != null) {
                    photo.copyMetaDataFromFileToEntity()
                }
            }
            photo.getPhotoEntity()?.let {
                photoEntities.add(it)
            }
        }
        return photoEntities
    }
}