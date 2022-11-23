package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.gui.Styles
import org.slf4j.LoggerFactory
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.image.FilteredImageSource
import javax.swing.GrayFilter
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.SwingConstants


/**
 * Part of MiniPhotoPanel.
 * It's only used temporarily for list cell rendering.
 * There are no changes and no event listeners.
 * JLabel enthält Icon enthält Image
 */
class ThumbnailLabel(thumbnailImage: BufferedImage?, active: Boolean, selected: Boolean) : JLabel("new", SwingConstants.CENTER) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        log.debug("init()")
        if (thumbnailImage == null) {
            text = "not loaded"
            horizontalAlignment = SwingConstants.CENTER

        } else {
            val thumbnailX = (Styles.THUMBNAIL_SIZE - thumbnailImage.width) / 2
            val thumbnailY = (Styles.THUMBNAIL_SIZE - thumbnailImage.height) / 2
            val img = if(active) {
                log.debug("normal thumbnail")
                gray(thumbnailImage)
            } else {
                log.debug("gray thumbnail")
                thumbnailImage
            }
            text = null
            icon = ImageIcon(img)
            setBounds(thumbnailX, thumbnailY, thumbnailImage.width, thumbnailImage.height)
        }
        border = if(selected) {
            Styles.HIGHLIGHT_BORDER
        } else {
            Styles.NORMAL_BORDER
        }
    }

    private fun gray(inputImage: Image): Image {
        val filter = GrayFilter(true, 50)
        val producer = FilteredImageSource(inputImage.source, filter)
        return Toolkit.getDefaultToolkit().createImage(producer)
    }

    /*
    private fun gray(inputImage: BufferedImage): Image {
        val image = BufferedImage(
            inputImage.width, inputImage.height,
            BufferedImage.TYPE_BYTE_GRAY
        )
        val g = image.graphics
        g.drawImage(inputImage, 0, 0, null)
        g.dispose()
        return image
    }
    */
}