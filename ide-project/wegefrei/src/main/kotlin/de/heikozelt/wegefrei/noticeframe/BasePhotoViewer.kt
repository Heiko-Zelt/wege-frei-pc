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

    private val zoomInButton = JButton("+")
    private val zoomOutButton = JButton("-")

    init {
        log.debug("init(photo: pos.latitude=${photo.getGeoPosition()?.latitude})")
        log.debug("photo: toolTipText=${photo.getToolTipText()})")

        border = NO_BORDER

        scrollPane.setViewportView(label)

        zoomInButton.addActionListener { zoomIn() }
        zoomOutButton.addActionListener { zoomOut() }
        zoomOutButton.isEnabled = false

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

    /**
     * increase zoom level, more details
     */

    private fun zoomIn() {
        if(zoomLevel == MIN_ZOOM_LEVEL) {
            zoomOutButton.isEnabled = true
        }
        if(zoomLevel < MAX_ZOOM_LEVEL) {
            zoomLevel++
            label.zoomTo(zoomLevel)
            if(zoomLevel == MAX_ZOOM_LEVEL) {
                zoomInButton.isEnabled = false
            }
        }
    }

    /**
     * decrease zoom level, lesser details
     */
    private fun zoomOut() {
        if(zoomLevel == MAX_ZOOM_LEVEL) {
            zoomInButton.isEnabled = true
        }
        if(zoomLevel > MIN_ZOOM_LEVEL) {
            zoomLevel--
            label.zoomTo(zoomLevel)
            if(zoomLevel == MIN_ZOOM_LEVEL) {
                zoomOutButton.isEnabled = false
            }
        }
    }

    fun fit() {
        zoomLevel = 0
        label.zoomTo(zoomLevel)
    }

    companion object {
        const val MIN_ZOOM_LEVEL = 0.toShort()
        const val MAX_ZOOM_LEVEL = 7.toShort()
    }

}