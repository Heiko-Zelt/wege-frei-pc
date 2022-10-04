package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.ALL_PHOTOS_BACKGROUND
import de.heikozelt.wegefrei.gui.MainFrame.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NORMAL_BORDER
import mu.KotlinLogging
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.FilteredImageSource
import javax.swing.*

class MiniPhotoPanel(private val mainFrame: MainFrame, private val photo: Photo, private var active: Boolean): JPanel() {

    private val log = KotlinLogging.logger {}
    private val thumbnailLabel: JLabel
    private val mouseListener: MiniPhotoPanelMouseListener
    private val addButton: JButton
    private var borderVisible = false

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
        background = ALL_PHOTOS_BACKGROUND
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
        thumbnailLabel.border = NORMAL_BORDER
        mouseListener = MiniPhotoPanelMouseListener(mainFrame, this)
        if(active) {
            thumbnailLabel.addMouseListener(mouseListener)
        }
        add(thumbnailLabel)

        addButton = JButton("+")
        addButton.alignmentX = CENTER_ALIGNMENT
        addButton.isEnabled = active
        addButton.addActionListener {
           mainFrame.selectPhoto(photo)
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
        thumbnailLabel.addMouseListener(mouseListener)
        log.debug("activate")
    }

    fun deactivate() {
        active = false
        val thumbnailImage = makeThumbnailImage()
        if(thumbnailImage != null) {
            thumbnailLabel.icon = ImageIcon(thumbnailImage)
            thumbnailLabel.removeMouseListener(mouseListener)
        }
        addButton.isEnabled = false
        log.debug("deactivate")
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
}