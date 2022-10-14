package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.SELECT_BUTTON_SIZE
import de.heikozelt.wegefrei.gui.Styles.Companion.THUMBNAIL_SIZE
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.FilteredImageSource
import javax.swing.*

class MiniPhotoPanel(private val noticeFrame: NoticeFrame, private val photo: Photo, private var active: Boolean): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val thumbnailLabel: JLabel
    private val mouseListener: MiniPhotoPanelMouseListener
    private val button = JButton("+")
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
                val scaleFactor =  THUMBNAIL_SIZE.toFloat() / image.height
                thumbnailHeight = THUMBNAIL_SIZE
                thumbnailWidth =  (scaleFactor * image.width).toInt()
                thumbnailX = (THUMBNAIL_SIZE - thumbnailWidth) / 2 + 1
            } else {
                val scaleFactor =  THUMBNAIL_SIZE.toFloat() / image.width
                thumbnailHeight = (scaleFactor * image.height).toInt()
                thumbnailWidth = THUMBNAIL_SIZE
                thumbnailY = (THUMBNAIL_SIZE - thumbnailHeight) / 2 + 1
            }
        }
    }

    /**
     * macht aus dem Photo-Image ein Thumbnail-Image.
     * scalieren und ggf. grau machen
     */
    private fun makeThumbnailImage() {
        photo.getImage()?.let { image ->
            thumbnailImage = image.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_SMOOTH)
            if (!active) {
                val filter = GrayFilter(true, 50)
                val producer = FilteredImageSource(thumbnailImage?.source, filter)
                val grayImg = Toolkit.getDefaultToolkit().createImage(producer)
                thumbnailImage = grayImg
            }
        }
    }

    init {
        layout = null
        //background = Styles.PHOTO_SQUARE_BACKGROUND
        // + 2 wegen Border
        preferredSize = Dimension(THUMBNAIL_SIZE + 2, THUMBNAIL_SIZE + 2)
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
        mouseListener = MiniPhotoPanelMouseListener(noticeFrame, this)
        if(active) {
            thumbnailLabel.addMouseListener(mouseListener)
        }

        button.isEnabled = active
        button.addActionListener { selectPhoto() }
        // + 1 wegen Border
        val buttonXY = THUMBNAIL_SIZE - SELECT_BUTTON_SIZE + 1
        button.setBounds(buttonXY, buttonXY, SELECT_BUTTON_SIZE, SELECT_BUTTON_SIZE)

        add(button)
        add(thumbnailLabel)
    }

    fun selectPhoto() {
        noticeFrame.selectPhoto(photo)
    }

    fun getPhoto(): Photo {
        return photo
    }

    fun activate() {
        active = true
        makeThumbnailImage()
        if(thumbnailImage != null) {
            thumbnailLabel.icon = ImageIcon(thumbnailImage)
        }
        button.isEnabled = true
        thumbnailLabel.addMouseListener(mouseListener)
        log.debug("activate")
    }

    fun deactivate() {
        active = false
        makeThumbnailImage()
        if(thumbnailImage != null) {
            thumbnailLabel.icon = ImageIcon(thumbnailImage)
            thumbnailLabel.removeMouseListener(mouseListener)
        }
        button.isEnabled = false
        log.debug("deactivate")
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
}