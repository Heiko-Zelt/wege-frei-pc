package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.ZOOM_PANEL_BACKGROUND
import mu.KotlinLogging
import java.awt.Color
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.FilteredImageSource
import javax.swing.*

class MaxiPhotoPanel(private val mainFrame: MainFrame, private val photo: Photo): JPanel() {

    private val log = KotlinLogging.logger {}

    private val label: JLabel

    private val addButton: JButton

    private fun makeThumbnailImage(): Image? {
        return photo.getImage()?.getScaledInstance(600, 400, Image.SCALE_SMOOTH)
    }

    init {
        background = ZOOM_PANEL_BACKGROUND
        border = NO_BORDER
        layout = BoxLayout(this, BoxLayout.Y_AXIS);

        //val file = File(PHOTO_DIR, photo.filename)
        //val photo = readPhotoMetadata(file)
        //val img = ImageIO.read(file)

        val thumbnailImage = makeThumbnailImage()
        label = if(thumbnailImage == null) {
            JLabel("not loaded")
        } else {
            JLabel(ImageIcon(thumbnailImage))
        }
        label.toolTipText = "<html>${photo.filename}<br>${photo?.getDateFormatted()}<br>${photo?.latitude}, ${photo?.longitude}</html>"
        label.alignmentX = CENTER_ALIGNMENT
        add(label)

        addButton = JButton("+")
        addButton.alignmentX = CENTER_ALIGNMENT
        addButton.addActionListener {
           mainFrame.selectPhoto(photo)
        }
        add(addButton)
    }

    fun getPhoto(): Photo {
        return photo
    }

}