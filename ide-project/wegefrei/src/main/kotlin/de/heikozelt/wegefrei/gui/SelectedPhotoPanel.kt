package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import java.awt.Container
import java.awt.Image
import javax.swing.*

class SelectedPhotoPanel(private val mainFrame: MainFrame, private val photo: Photo): JPanel() {

    private val log = KotlinLogging.logger {}

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS);

        //val file = File(PHOTO_DIR, filename)
        //val photo = readPhotoMetadata(file)
        //val img = ImageIO.read(file)
        val scaledImg = photo.getImage()?.getScaledInstance(150,100, Image.SCALE_SMOOTH)
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
           mainFrame?.unselectPhoto(this)
        }

        add(removeButton)
    }

    fun getPhoto(): Photo {
        return photo
    }
}