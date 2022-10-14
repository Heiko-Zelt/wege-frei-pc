package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class MiniSelectedPhotoPanel(private val noticeFrame: NoticeFrame, private val photo: Photo): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val thumbnailLabel: JLabel
    private val button = JButton("-")
    private var borderVisible = false
    private var thumbnailX = 0
    private var thumbnailY = 0
    private var thumbnailWidth = 0
    private var thumbnailHeight = 0
    private var thumbnailImage: Image? = null

    private fun calculateThumbnail() {
        photo.getImage()?.let {image ->
            // Thumbnail-Größe auf die Längere der beiden Bild-Seiten anpassen
            if(image.height > image.width) {
                val scaleFactor =  Styles.THUMBNAIL_SIZE.toFloat() / image.height
                thumbnailHeight = Styles.THUMBNAIL_SIZE
                thumbnailWidth =  (scaleFactor * image.width).toInt()
                thumbnailX = (Styles.THUMBNAIL_SIZE - thumbnailWidth) / 2 + 1
            } else {
                val scaleFactor =  Styles.THUMBNAIL_SIZE.toFloat() / image.width
                thumbnailHeight = (scaleFactor * image.height).toInt()
                thumbnailWidth = Styles.THUMBNAIL_SIZE
                thumbnailY = (Styles.THUMBNAIL_SIZE - thumbnailHeight) / 2 + 1
            }
        }
    }

    private fun makeThumbnailImage() {
        photo.getImage()?.let { image ->
            thumbnailImage = image.getScaledInstance(150, 100, Image.SCALE_SMOOTH)
        }
    }

    init {
        layout = null
        //background = SELECTED_PHOTOS_BACKGROUND

        preferredSize = Dimension(Styles.THUMBNAIL_SIZE + 2, Styles.THUMBNAIL_SIZE + 2)
        minimumSize = preferredSize
        maximumSize = preferredSize

        calculateThumbnail()
        makeThumbnailImage()

        thumbnailLabel = if(thumbnailImage == null) {
            JLabel("not loaded")
        } else {
            JLabel(ImageIcon(thumbnailImage))
        }

        log.debug("setBounds($thumbnailX, $thumbnailY, $thumbnailWidth, $thumbnailHeight)")
        thumbnailLabel.setBounds(thumbnailX, thumbnailY, thumbnailWidth, thumbnailHeight)

        thumbnailLabel.toolTipText = "<html>${photo.filename}<br>${photo.getDateFormatted()}<br>${photo.latitude}, ${photo.longitude}</html>"
        thumbnailLabel.border = NORMAL_BORDER
        thumbnailLabel.addMouseListener(MiniSelectedPhotoPanelMouseListener(noticeFrame, this))

        // + 1 wegen Border
        val buttonXY = Styles.THUMBNAIL_SIZE - Styles.SELECT_BUTTON_SIZE + 1
        button.setBounds(buttonXY, buttonXY, Styles.SELECT_BUTTON_SIZE, Styles.SELECT_BUTTON_SIZE)
        button.addActionListener { unselectPhoto() }

        add(button)
        add(thumbnailLabel)
    }

    fun unselectPhoto() {
        noticeFrame.unselectPhoto(photo)
    }

    fun displayBorder(visible: Boolean) {
        if(visible && !borderVisible) {
            thumbnailLabel.border = HIGHLIGHT_BORDER
            thumbnailLabel.revalidate()
            borderVisible = true
        } else if(!visible && borderVisible) {
            thumbnailLabel.border = NORMAL_BORDER
            thumbnailLabel.revalidate()
            borderVisible = false
        }
    }

    fun getPhoto(): Photo {
        return photo
    }
}