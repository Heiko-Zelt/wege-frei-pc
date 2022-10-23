package de.heikozelt.wegefrei.jobs

import de.heikozelt.wegefrei.DatabaseService
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.noticeframe.AllPhotosPanel
import org.slf4j.LoggerFactory
import javax.swing.SwingWorker

/**
 * Läd die Fotos aus der Datenbank
 * und übermittelt sie an das AllPhotosPanel.
 */
class LoadPhotosWorker(
    private val databaseService: DatabaseService,
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
        photos = databaseService.getPhotos(firstPhotoFilename, 20)
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