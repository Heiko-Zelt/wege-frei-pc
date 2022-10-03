package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.ZOOM_PANEL_BACKGROUND
import mu.KotlinLogging
import java.awt.Color
import java.awt.Image
import javax.swing.*

class MaxiSelectedPhotoPanel(private val mainFrame: MainFrame, private val photo: Photo): JPanel() {

    private val log = KotlinLogging.logger {}

    init {
        background = ZOOM_PANEL_BACKGROUND
        border = NO_BORDER
        layout = BoxLayout(this, BoxLayout.Y_AXIS);

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

        val removeButton = JButton("-")
        removeButton.alignmentX = CENTER_ALIGNMENT
        removeButton.addActionListener {
           mainFrame?.unselectPhoto(photo)
        }

        add(removeButton)
    }

    fun getPhoto(): Photo {
        return photo
    }
}