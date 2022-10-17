package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.jobs.SelectedThumbnailWorker
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class MiniSelectedPhotoPanel(private val noticeFrame: NoticeFrame, private val photo: Photo, private var index: Int): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val thumbnailLabel = JLabel("not loaded", SwingConstants.CENTER)
    private val button = JButton("-")
    private val label = JLabel()
    private var borderVisible = false

    init {
        layout = null
        // + 2 wegen Border
        preferredSize = Dimension(Styles.THUMBNAIL_SIZE + 2, Styles.THUMBNAIL_SIZE + 2)
        minimumSize = preferredSize
        maximumSize = preferredSize

        //background = Color.green

        thumbnailLabel.toolTipText = photo.getToolTipText()
        thumbnailLabel.setBounds(0, 0, Styles.THUMBNAIL_SIZE, Styles.THUMBNAIL_SIZE)
        thumbnailLabel.border = NORMAL_BORDER
        thumbnailLabel.addMouseListener(MiniSelectedPhotoPanelMouseAdapter(noticeFrame, this))

        // + 1 wegen Border
        button.addActionListener { unselectPhoto() }
        val buttonXY = Styles.THUMBNAIL_SIZE - Styles.SELECT_BUTTON_SIZE + 1
        button.setBounds(buttonXY, buttonXY, Styles.SELECT_BUTTON_SIZE, Styles.SELECT_BUTTON_SIZE)
        button.margin = Insets(0, 0, 0, 0)


        updateText(index)
        label.border = NORMAL_BORDER
        label.background = Styles.PHOTO_MARKER_BACKGROUND
        label.isOpaque = true
        val labelSize = label.preferredSize
        label.setBounds(0, 0, labelSize.width, labelSize.height)

        add(label)
        add(button)
        add(thumbnailLabel)

        // Loading the image from the filesystem and resizing it is time-consuming. So, do it later...
        val worker = SelectedThumbnailWorker(photo, thumbnailLabel)
        worker.execute()
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

    fun updateText(index: Int) {
        label.text = " ${index + 1} "
    }
}