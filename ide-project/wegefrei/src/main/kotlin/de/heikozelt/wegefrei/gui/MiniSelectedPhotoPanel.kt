package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.SELECTED_PHOTOS_BACKGROUND
import org.slf4j.LoggerFactory
import java.awt.Image
import javax.swing.*

class MiniSelectedPhotoPanel(private val noticeFrame: NoticeFrame, private val photo: Photo): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val thumbnailLabel: JLabel
    private var borderVisible = false

    init {
        background = SELECTED_PHOTOS_BACKGROUND
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        //val file = File(PHOTO_DIR, filename)
        //val photo = readPhotoMetadata(file)
        //val img = ImageIO.read(file)
        val scaledImg = photo.getImage()?.getScaledInstance(150,100, Image.SCALE_SMOOTH)
        thumbnailLabel = if(scaledImg == null) {
            JLabel("not loaded")
        } else {
            JLabel(ImageIcon(scaledImg))
        }

        /*
        thumbnailLabel.text = "1"
        thumbnailLabel.horizontalTextPosition = JLabel.CENTER;
        thumbnailLabel.verticalTextPosition = JLabel.CENTER;
         */

        thumbnailLabel.toolTipText = "<html>${photo.filename}<br>${photo.getDateFormatted()}<br>${photo.latitude}, ${photo.longitude}</html>"
        thumbnailLabel.alignmentX = CENTER_ALIGNMENT
        thumbnailLabel.border = NORMAL_BORDER
        thumbnailLabel.addMouseListener(MiniSelectedPhotoPanelMouseListener(noticeFrame, this))
        add(thumbnailLabel)

        val removeButton = JButton("-")
        removeButton.alignmentX = CENTER_ALIGNMENT
        removeButton.addActionListener { unselectPhoto() }

        add(removeButton)
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