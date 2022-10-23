package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.ZOOM_PANEL_BACKGROUND
import org.slf4j.LoggerFactory
import java.awt.Image
import java.awt.Insets
import javax.swing.*

class MaxiSelectedPhotoPanel(private val noticeFrame: NoticeFrame, private val photo: Photo): JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        background = ZOOM_PANEL_BACKGROUND
        border = NO_BORDER
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        //val file = File(PHOTO_DIR, filename)
        //val photo = readPhotoMetadata(file)
        //val img = ImageIO.read(file)
        val scaledImg = photo.getImage()?.getScaledInstance(600,400, Image.SCALE_SMOOTH)
        val label = if(scaledImg == null) {
            JLabel("not loaded")
        } else {
            JLabel(ImageIcon(scaledImg))
        }
        label.toolTipText = "<html>${photo.filename}<br>${photo.getDateFormatted()}<br>${photo.latitude}, ${photo.longitude}</html>"
        label.alignmentX = CENTER_ALIGNMENT
        add(label)

        val button = JButton("-")
        button.margin = Insets(0, 0, 0, 0)
        button.alignmentX = CENTER_ALIGNMENT
        button.addActionListener {
            noticeFrame.unselectPhoto(photo)
        }

        add(button)
    }

    fun getPhoto(): Photo {
        return photo
    }
}