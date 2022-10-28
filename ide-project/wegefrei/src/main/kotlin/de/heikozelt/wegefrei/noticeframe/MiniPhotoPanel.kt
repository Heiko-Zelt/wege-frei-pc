package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.SELECT_BUTTON_SIZE
import de.heikozelt.wegefrei.gui.Styles.Companion.THUMBNAIL_SIZE
import de.heikozelt.wegefrei.jobs.ThumbnailWorker
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants


class MiniPhotoPanel(private val photosDir: String, private val noticeFrame: NoticeFrame, private val photo: Photo, private var active: Boolean) :
    JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val thumbnailLabel = JLabel("not loaded", SwingConstants.CENTER)
    private val mouseListener: MiniPhotoPanelMouseListener
    private val button = JButton("+")
    private var borderVisible = false

    /**
     * macht aus dem Foto-Image ein Thumbnail-Image.
     * scalieren und ggf. grau machen
     */

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
        //thumbnailLabel.isEnabled = active
        //thumbnailLabel.addMouseListener(mouseListener)
        if(active) {
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
        val worker = ThumbnailWorker(photosDir, photo, active, thumbnailLabel)
        worker.execute()
    }

    fun selectPhoto() {
        noticeFrame.selectPhoto(photo)
    }

    fun getPhoto(): Photo {
        return photo
    }

    fun isActive(): Boolean {
        return active
    }

    fun activate() {
        log.debug("activate()")
        active = true

        val worker = ThumbnailWorker(photosDir, photo, active, thumbnailLabel)
        worker.execute()

        button.isEnabled = true
        //thumbnailLabel.isEnabled = true
        thumbnailLabel.addMouseListener(mouseListener)
    }

    fun deactivate() {
        log.debug("deactivate()")
        active = false

        val worker = ThumbnailWorker(photosDir, photo, active, thumbnailLabel)
        worker.execute()

        button.isEnabled = false
        //thumbnailLabel.isEnabled = false
        thumbnailLabel.removeMouseListener(mouseListener)
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
}