package de.heikozelt.wegefrei.jobs

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.noticeframe.AllPhotosPanel
import org.slf4j.LoggerFactory
import javax.swing.SwingWorker

/**
 * Läd die Fotos (nur Metadaten) aus der Datenbank
 * und übermittelt sie an das AllPhotosPanel.
 * Werden die Daten in einem Rutsch aus der Datenbank geladen?
 * oder wäre auch ein Cursor / Stream-Verarbeitung möglich?
 */
class LoadPhotosWorker(
    private val databaseRepo: DatabaseRepo,
    private val firstPhotoFilename: String,
    private val allPhotosPanel: AllPhotosPanel
)
: SwingWorker<Set<Photo>, Set<Photo>>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var photos: Set<Photo>? = null

    /**
     * This is done in own Thread
     */
    override fun doInBackground(): Set<Photo>? {
        log.info("doInBackground()")
        photos = databaseRepo.getPhotos(firstPhotoFilename, 20)
        log.debug("number of photos in database: ${photos?.size}")
        return photos
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        photos?.let {
            for (photo in it) {
                allPhotosPanel.appendPhoto(photo)
            }
        }
    }

}