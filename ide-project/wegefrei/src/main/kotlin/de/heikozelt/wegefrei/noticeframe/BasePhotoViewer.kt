package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory
import java.awt.Insets
import javax.swing.*

open class BasePhotoViewer(
    private val noticeFrame: NoticeFrame,
    private val photo: Photo
): JPanel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var scrollPane = JScrollPane()
    private val label = MaxiPhotoLabel(scrollPane.viewport, photo)
    private var zoomLevel: Short = 0
    protected val actionButton = JButton()

    init {
        log.debug("init(photo: pos.latitude=${photo.getGeoPosition()?.latitude})")
        log.debug("photo: toolTipText=${photo.getToolTipText()})")

        border = NO_BORDER

        scrollPane.setViewportView(label)

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
                        .addComponent(actionButton)
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
                        .addComponent(actionButton)
                )
        )
        val m = zoomInButton.margin
        zoomInButton.margin = Insets(m.top,10, m.bottom, 10)
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