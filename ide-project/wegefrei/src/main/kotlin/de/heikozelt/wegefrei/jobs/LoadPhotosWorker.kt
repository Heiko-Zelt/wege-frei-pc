package de.heikozelt.wegefrei.jobs

import de.heikozelt.wegefrei.ImageFilenameFilter
import de.heikozelt.wegefrei.entities.PhotoEntity
import de.heikozelt.wegefrei.model.BrowserListModel
import org.slf4j.LoggerFactory
import java.io.File
import javax.swing.SwingWorker

/**
 * Läd die Fotos (nur Metadaten) aus der Datenbank
 * und übermittelt sie an das AllPhotosPanel.
 * Werden die Daten in einem Rutsch aus der Datenbank geladen?
 * oder wäre auch ein Cursor / Stream-Verarbeitung möglich?
 */
class LoadPhotosWorker(private val browserListModel: BrowserListModel)
: SwingWorker<Set<PhotoEntity>, PhotoEntity>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    /**
     * not needed because of chunk processing
     */
    private var photoEntities = mutableSetOf<PhotoEntity>()

    /**
     * This is done in own Thread
     * <ol>
     *     <li>phase: read all filenames (and sort them)</li>
     *     <li>phase: Load photos</li>
     * </ol>
     */
    override fun doInBackground(): Set<PhotoEntity>? {
        log.debug("doInBackground()")
        browserListModel.getDirectoryPath()?.let { path ->
            val dir = File(path.toString())
            if (!dir.isDirectory) {
                 log.error("$path ist kein Verzeichnis.")
                 return emptySet()
            }
            val unsortedFilenamesList = dir.list(ImageFilenameFilter()) ?: return emptySet()
            val sortedFilenamesList = unsortedFilenamesList.sorted()
            sortedFilenamesList.forEach {
                val photoEntity = PhotoEntity.fromPath(path, it)
                photoEntities.add(photoEntity)
                publish(photoEntity)
            }
            return photoEntities
        }
        log.error("photos directory is null")
        return emptySet()
    }

    /**
     * give chunks of result to the list model
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun process(chunks: List<PhotoEntity>) {
        //allPhotosListModel.appendPhotos(chunks)
    }
}