package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.Image
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class MiniSelectedPhotoPanel(private val noticeFrame: NoticeFrame, private val photo: Photo): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val thumbnailLabel = JLabel("not loaded")
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
        // + 2 wegen Border
        preferredSize = Dimension(Styles.THUMBNAIL_SIZE + 2, Styles.THUMBNAIL_SIZE + 2)
        minimumSize = preferredSize
        maximumSize = preferredSize

        thumbnailLabel.toolTipText = photo.getToolTipText()
        thumbnailLabel.border = NORMAL_BORDER
        thumbnailLabel.addMouseListener(MiniSelectedPhotoPanelMouseListener(noticeFrame, this))

        // + 1 wegen Border
        button.addActionListener { unselectPhoto() }
        val buttonXY = Styles.THUMBNAIL_SIZE - Styles.SELECT_BUTTON_SIZE + 1
        button.setBounds(buttonXY, buttonXY, Styles.SELECT_BUTTON_SIZE, Styles.SELECT_BUTTON_SIZE)
        button.margin = Insets(0, 0, 0, 0)

        add(button)
        add(thumbnailLabel)

        // Loading the image from the filesystem and resizing it is time-consuming. So, do it later...
        val worker = SelectedThumbnailWorker(photo, thumbnailLabel)
        worker.execute()
        /*
        EventQueue.invokeLater {
            calculateThumbnail()
            makeThumbnailImage()
            thumbnailLabel.text = null
            thumbnailLabel.icon = ImageIcon(thumbnailImage)
            log.debug("setBounds($thumbnailX, $thumbnailY, $thumbnailWidth, $thumbnailHeight)")
            thumbnailLabel.setBounds(thumbnailX, thumbnailY, thumbnailWidth, thumbnailHeight)
        }
        */
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