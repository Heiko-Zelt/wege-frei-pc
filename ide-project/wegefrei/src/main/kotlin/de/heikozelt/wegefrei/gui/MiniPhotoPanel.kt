package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.SELECT_BUTTON_SIZE
import de.heikozelt.wegefrei.gui.Styles.Companion.THUMBNAIL_SIZE
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel


class MiniPhotoPanel(private val noticeFrame: NoticeFrame, private val photo: Photo, private var active: Boolean) :
    JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val thumbnailLabel = JLabel("not loaded")
    private val mouseListener: MiniPhotoPanelMouseListener
    private val button = JButton("+")
    private var borderVisible = false

    /**
     * macht aus dem Photo-Image ein Thumbnail-Image.
     * scalieren und ggf. grau machen
     */


    fun buildToolTipText() {
        val text: String
        photo.apply {
            val lines = mutableListOf<String>()
            if (filename != null) {
                lines.add(filename)
            }
            if (date != null) {
                lines.add(getDateFormatted())
            }
            if (latitude != null && longitude != null) {
                lines.add("$latitude, $longitude")
            }
            text = "<html>${lines.joinToString("<br>")}</html>"
        }
        thumbnailLabel.toolTipText = text
    }

    init {
        layout = null
        // + 2 wegen Border
        preferredSize = Dimension(THUMBNAIL_SIZE + 2, THUMBNAIL_SIZE + 2)
        minimumSize = preferredSize
        maximumSize = preferredSize

        thumbnailLabel.toolTipText = photo.getToolTipText()
        thumbnailLabel.setBounds(0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE)
        thumbnailLabel.border = NORMAL_BORDER

        mouseListener = MiniPhotoPanelMouseListener(noticeFrame, this)
        if (active) {
            thumbnailLabel.addMouseListener(mouseListener)
        }

        button.isEnabled = active
        button.addActionListener { selectPhoto() }
        // + 1 wegen Border
        val buttonXY = THUMBNAIL_SIZE - SELECT_BUTTON_SIZE + 1
        button.setBounds(buttonXY, buttonXY, SELECT_BUTTON_SIZE, SELECT_BUTTON_SIZE)
        button.margin = Insets(0, 0, 0, 0)

        add(button)
        add(thumbnailLabel)

        // Loading the image from the filesystem and resizing it is time-consuming. So, do it later...
        val worker = ThumbnailWorker(photo, active, thumbnailLabel)
        worker.execute()
    }

    fun selectPhoto() {
        noticeFrame.selectPhoto(photo)
    }

    fun getPhoto(): Photo {
        return photo
    }

    fun activate() {
        active = true

        /*
        makeThumbnailImage()
        if (thumbnailImage != null) {
            thumbnailLabel.icon = ImageIcon(thumbnailImage)
        }
        */
        val worker = ThumbnailWorker(photo, active, thumbnailLabel)
        worker.execute()

        button.isEnabled = true
        thumbnailLabel.addMouseListener(mouseListener)
        log.debug("activate")
    }

    fun deactivate() {
        active = false

        /*
        makeThumbnailImage()
        if (thumbnailImage != null) {
            thumbnailLabel.icon = ImageIcon(thumbnailImage)
            thumbnailLabel.removeMouseListener(mouseListener)
        }
         */

        val worker = ThumbnailWorker(photo, active, thumbnailLabel)
        worker.execute()

        button.isEnabled = false
        log.debug("deactivate")
    }

    fun displayBorder(visible: Boolean) {
        if (visible && !borderVisible) {
            thumbnailLabel.border = HIGHLIGHT_BORDER
            thumbnailLabel.revalidate()
            borderVisible = true
        } else if (!visible && borderVisible) {
            thumbnailLabel.border = NORMAL_BORDER
            thumbnailLabel.revalidate()
            borderVisible = false
        }
    }

    companion object {
        val eq = EventQueue::class
    }
}