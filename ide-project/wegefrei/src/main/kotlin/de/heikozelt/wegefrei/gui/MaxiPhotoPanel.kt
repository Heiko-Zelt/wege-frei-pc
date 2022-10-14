package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.ZOOM_PANEL_BACKGROUND
import org.slf4j.LoggerFactory
import java.awt.Image
import javax.swing.*

class MaxiPhotoPanel(private val noticeFrame: NoticeFrame, private val photo: Photo): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val label: JLabel

    private val addButton: JButton

    private fun makeThumbnailImage(): Image? {
        return photo.getImage()?.getScaledInstance(600, 400, Image.SCALE_SMOOTH)
    }

    init {
        background = ZOOM_PANEL_BACKGROUND
        border = NO_BORDER
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        //val file = File(PHOTO_DIR, photo.filename)
        //val photo = readPhotoMetadata(file)
        //val img = ImageIO.read(file)

        val thumbnailImage = makeThumbnailImage()
        label = if(thumbnailImage == null) {
            JLabel("not loaded")
        } else {
            JLabel(ImageIcon(thumbnailImage))
        }
        label.toolTipText = "<html>${photo.filename}<br>${photo.getDateFormatted()}<br>${photo.latitude}, ${photo.longitude}</html>"
        label.alignmentX = CENTER_ALIGNMENT
        add(label)

        addButton = JButton("+")
        addButton.alignmentX = CENTER_ALIGNMENT
        addButton.addActionListener {
           noticeFrame.selectPhoto(photo)
        }
        add(addButton)
    }

    fun getPhoto(): Photo {
        return photo
    }

}