package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.ZOOM_PANEL_BACKGROUND
import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory
import java.awt.Insets
import javax.swing.*

class MaxiPhotoPanel(
    private val noticeFrame: NoticeFrame,
    private val photo: Photo
): JPanel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var scrollPane = JScrollPane()
    private val label = MaxiPhotoLabel(scrollPane.viewport, photo)
    private var zoomLevel: Short = 0

    init {
        background = ZOOM_PANEL_BACKGROUND
        border = NO_BORDER
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        scrollPane.setViewportView(label)

        //val file = File(PHOTO_DIR, photo.filename)
        //val photo = readPhotoMetadata(file)
        //val img = ImageIO.read(file)

        val addButton = JButton("hinzufügen")
        addButton.margin = Insets(0, 0, 0, 0)
        addButton.addActionListener {
           noticeFrame.selectPhoto(photo)
        }

        val zoomInButton = JButton("+")
        zoomInButton.addActionListener { zoomIn() }
        val zoomOutButton = JButton("-")
        zoomOutButton.addActionListener { zoomOut() }

        val lay = GroupLayout(this)
        lay.autoCreateGaps = false
        lay.autoCreateContainerGaps = false
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane)
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            GroupLayout.PREFERRED_SIZE,
                            Int.MAX_VALUE
                        )
                        .addComponent(zoomInButton)
                        .addComponent(zoomOutButton)
                        .addComponent(addButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(scrollPane)
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(zoomInButton)
                        .addComponent(zoomOutButton)
                        .addComponent(addButton)
                )
        )
        zoomInButton.margin = Insets(0, 10, 0, 10)
        lay.linkSize(SwingConstants.HORIZONTAL, zoomInButton, zoomOutButton)
        layout = lay
    }

    fun getPhoto(): Photo {
        return photo
    }

    private fun zoomIn() {
        if(zoomLevel < 7) {
            zoomLevel++
            label.zoomTo(zoomLevel)
        }
    }

    private fun zoomOut() {
        if(zoomLevel > 0) {
            zoomLevel--
            label.zoomTo(zoomLevel)
        }
    }

    fun fit() {
        zoomLevel = 0
        label.zoomTo(zoomLevel)
    }

}