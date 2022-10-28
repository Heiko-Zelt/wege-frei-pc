package de.heikozelt.wegefrei.jobs

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles
import org.slf4j.LoggerFactory
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.SwingWorker

/**
 * Läd ein Foto-Image aus dem Dateisystem (falls noch nicht geladen),
 * generiert ein Thumbnail-Image und passt das Label an.
 */
class SelectedThumbnailWorker(
    private val photosDir: String,
    private val photo: Photo,
    private val label: JLabel)
: SwingWorker<ImageIcon?, ImageIcon?>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var thumbnailX = 0
    private var thumbnailY = 0
    private var thumbnailWidth = 0
    private var thumbnailHeight = 0
    private var thumbnailImage: Image? = null
    private var icon: ImageIcon? = null

    private fun calculateThumbnail() {
        //Thread.sleep(5000)
        photo.getImage(photosDir)?.let { image ->
            // Thumbnail-Größe auf die Längere der beiden Bild-Seiten anpassen
            if (image.height > image.width) {
                val scaleFactor = Styles.THUMBNAIL_SIZE.toFloat() / image.height
                thumbnailHeight = Styles.THUMBNAIL_SIZE
                thumbnailWidth = (scaleFactor * image.width).toInt()
                thumbnailX = (Styles.THUMBNAIL_SIZE - thumbnailWidth) / 2 + 1
            } else {
                val scaleFactor = Styles.THUMBNAIL_SIZE.toFloat() / image.width
                thumbnailHeight = (scaleFactor * image.height).toInt()
                thumbnailWidth = Styles.THUMBNAIL_SIZE
                thumbnailY = (Styles.THUMBNAIL_SIZE - thumbnailHeight) / 2 + 1
            }
        }
    }

    private fun makeThumbnailImage() {
        photo.getImage(photosDir)?.let { image ->
            thumbnailImage = image.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_SMOOTH)
        }
    }

    /**
     * This is done in own Thread
     */
    override fun doInBackground(): ImageIcon? {
        calculateThumbnail()
        makeThumbnailImage()
        icon = if(thumbnailImage == null) {
            null
        } else {
            ImageIcon(thumbnailImage)
        }
        return icon
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        if(thumbnailImage != null) {
            label.text = null
            label.icon = ImageIcon(thumbnailImage)
            //log.debug("setBounds($thumbnailX, $thumbnailY, $thumbnailWidth, $thumbnailHeight)")
            label.setBounds(thumbnailX, thumbnailY, thumbnailWidth, thumbnailHeight)
        }
    }

}