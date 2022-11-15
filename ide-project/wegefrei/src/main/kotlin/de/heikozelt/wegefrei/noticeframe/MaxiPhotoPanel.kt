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
    private val label = MaxiPhotoLabel(photo)
    private var scrollPane = JScrollPane(label)

    init {
        background = ZOOM_PANEL_BACKGROUND
        border = NO_BORDER
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        //val file = File(PHOTO_DIR, photo.filename)
        //val photo = readPhotoMetadata(file)
        //val img = ImageIO.read(file)

        val button = JButton("+")
        button.margin = Insets(0, 0, 0, 0)
        button.alignmentX = CENTER_ALIGNMENT
        button.addActionListener {
           noticeFrame.selectPhoto(photo)
        }

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
                        .addComponent(button)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(scrollPane)
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(button)
                )
        )
        layout = lay
    }

    fun getPhoto(): Photo {
        return photo
    }

}