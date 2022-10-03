package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.FilteredImageSource
import javax.swing.*

class PhotoPanel(private val mainFrame: MainFrame, private val photo: Photo, private var active: Boolean): JPanel() {

    private val log = KotlinLogging.logger {}

    private val thumbnailLabel: JLabel

    private val addButton: JButton

    private fun makeThumbnailImage(): Image? {
        var scaledImg = photo.getImage()?.getScaledInstance(150,100, Image.SCALE_SMOOTH)
        if(!active) {
            val filter = GrayFilter(true, 50)
            val producer = FilteredImageSource(scaledImg?.source, filter)
            val grayImg = Toolkit.getDefaultToolkit().createImage(producer)
            scaledImg = grayImg
        }
        return scaledImg
    }

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS);

        //val file = File(PHOTO_DIR, photo.filename)
        //val photo = readPhotoMetadata(file)
        //val img = ImageIO.read(file)

        val thumbnailImage = makeThumbnailImage()
        thumbnailLabel = if(thumbnailImage == null) {
            JLabel("not loaded")
        } else {
            JLabel(ImageIcon(thumbnailImage))
        }
        thumbnailLabel.toolTipText = "<html>${photo.filename}<br>${photo?.getDateFormatted()}<br>${photo?.latitude}, ${photo?.longitude}</html>"
        thumbnailLabel.alignmentX = CENTER_ALIGNMENT
        add(thumbnailLabel)

        addButton = JButton("+")
        addButton.alignmentX = CENTER_ALIGNMENT
        addButton.isEnabled = active
        addButton.addActionListener {
           mainFrame.selectPhoto(this)
        }
        add(addButton)
    }

    fun getPhoto(): Photo {
        return photo
    }

    fun activate() {
        active = true
        val thumbnailImage = makeThumbnailImage()
        if(thumbnailImage != null) {
            thumbnailLabel.icon = ImageIcon(thumbnailImage)
        }
        addButton.isEnabled = true
        log.debug("activate")
    }

    fun deactivate() {
        active = false
        val thumbnailImage = makeThumbnailImage()
        if(thumbnailImage != null) {
            thumbnailLabel.icon = ImageIcon(thumbnailImage)
        }
        addButton.isEnabled = false
        log.debug("deactivate")
    }
}