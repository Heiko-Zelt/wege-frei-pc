package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.SELECTED_PHOTOS_BACKGROUND
import mu.KotlinLogging
import java.awt.Image
import javax.swing.*

class MiniSelectedPhotoPanel(private val mainFrame: MainFrame, private val photo: Photo): JPanel() {

    private val log = KotlinLogging.logger {}
    private val thumbnailLabel: JLabel
    private var borderVisible = false

    init {
        background = SELECTED_PHOTOS_BACKGROUND
        layout = BoxLayout(this, BoxLayout.Y_AXIS);

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
        thumbnailLabel.border = MainFrame.NORMAL_BORDER
        thumbnailLabel.addMouseListener(MiniSelectedPhotoPanelMouseListener(mainFrame, this))
        add(thumbnailLabel)

        val removeButton = JButton("-")
        removeButton.alignmentX = CENTER_ALIGNMENT
        removeButton.addActionListener {
           mainFrame?.unselectPhoto(photo)
        }

        add(removeButton)
    }

    fun displayBorder(visible: Boolean) {
        if(visible && !borderVisible) {
            thumbnailLabel.border = MainFrame.HIGHLIGHT_BORDER
            thumbnailLabel.revalidate()
            borderVisible = true
        } else if(!visible && borderVisible) {
            thumbnailLabel.border = MainFrame.NORMAL_BORDER
            thumbnailLabel.revalidate()
            borderVisible = false
        }
    }

    fun getPhoto(): Photo {
        return photo
    }
}