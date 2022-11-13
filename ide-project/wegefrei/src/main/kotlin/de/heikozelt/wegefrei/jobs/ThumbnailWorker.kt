package de.heikozelt.wegefrei.jobs

import de.heikozelt.wegefrei.entities.PhotoEntity
import de.heikozelt.wegefrei.gui.Styles
import org.slf4j.LoggerFactory
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.FilteredImageSource
import javax.swing.GrayFilter
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.SwingWorker

/**
 * Läd ein Foto-Image aus dem Dateisystem (falls noch nicht geladen),
 * generiert ein Thumbnail-Image und passt das Label an.
 */
class ThumbnailWorker(
    private val photoEntity: PhotoEntity,
    private val active: Boolean,
    private val label: JLabel)
: SwingWorker<ImageIcon?, ImageIcon?>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var thumbnailX = 0
    private var thumbnailY = 0
    private var thumbnailWidth = 0
    private var thumbnailHeight = 0
    private var thumbnailImage: Image? = null
    private var icon: ImageIcon? = null

    init{
        log.debug("init()")
    }

    private fun calculateThumbnail() {
        log.debug("calculateThumbnail()")
        //Thread.sleep(5000)
        photoEntity.getImage()?.let { image ->
            log.debug("got image of photo")
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
        log.debug("thumbnail: with: $thumbnailWidth, height: $thumbnailHeight")
    }

    private fun makeThumbnailImage() {
        photoEntity.getImage()?.let { image ->
            thumbnailImage = image.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_SMOOTH)
            if (!active) {
                val filter = GrayFilter(true, 50)
                val producer = FilteredImageSource(thumbnailImage?.source, filter)
                val grayImg = Toolkit.getDefaultToolkit().createImage(producer)
                thumbnailImage = grayImg
            }
        }
    }

    /**
     * This is done in own Thread.
     * loading of image from disk.
     */
    override fun doInBackground(): ImageIcon? {
        log.debug("doInBackground()")
        calculateThumbnail()
        makeThumbnailImage()
        icon = if(thumbnailImage == null) {
            null
        } else {
            ImageIcon(thumbnailImage) // doppelt!!!
        }
        return icon
    }

    /**
     * This is done in the Swing Event Dispatcher Thread.
     * Just update the user interface.
     * fireTableRowsUpdated(rowIndex, rowIndex)
     * ListModell.fireContentsChanged(Object source, int index0, int index1)
     */
    override fun done() {
        log.debug("done()")
        icon?.let {
            log.debug("setting icon...")
            label.text = null
            label.icon = it
            log.debug("setBounds($thumbnailX, $thumbnailY, $thumbnailWidth, $thumbnailHeight)")
            label.setBounds(thumbnailX, thumbnailY, thumbnailWidth, thumbnailHeight)
        }
    }

}