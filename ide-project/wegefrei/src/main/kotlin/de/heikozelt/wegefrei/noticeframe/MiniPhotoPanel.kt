package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.gui.Styles.Companion.THUMBNAIL_SIZE
import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel


class MiniPhotoPanel(
    noticeId: Int?,
    photo: Photo?,
    active: Boolean,
    selected: Boolean
) :
    JPanel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        val thumbnail = photo?.getPhotoFile()?.thumbnail

        layout = null
        preferredSize = Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
        minimumSize = preferredSize
        maximumSize = preferredSize

        /*
        val button = JButton("+")
        button.isEnabled = active
        val buttonXY = THUMBNAIL_SIZE - SELECT_BUTTON_SIZE
        button.setBounds(buttonXY, buttonXY, SELECT_BUTTON_SIZE, SELECT_BUTTON_SIZE)
        button.margin = Insets(0, 0, 0, 0)
        add(button)
         */
        //val layered = JLayeredPane()

        // lazy loading database access? in EDT?
        val noticeIds = photo?.getPhotoEntity()?.noticeEntities?.map { it.id }?.filter { it != noticeId }?.sortedBy{it}
        log.debug("noticeIds: $noticeIds")
        noticeIds?.let { ids ->
            if (ids.isNotEmpty()) {
                val layer = JPanel()
                layer.layout = FlowLayout(FlowLayout.LEFT)
                layer.maximumSize = Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                layer.isOpaque = false
                ids.forEach { id ->
                    val lbl = JLabel("$id")

                    lbl.isOpaque = true
                    lbl.background = Color(255,255, 255, 100)
                    //lbl.background = Color.orange
                    layer.add(lbl)
                }
                val layerSize = layer.preferredSize
                //layer.setBounds(0, THUMBNAIL_SIZE - layerSize.height, THUMBNAIL_SIZE, layerSize.height)
                layer.setBounds(0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                //layer.revalidate()
                add(layer)
            }
        }

        add(ThumbnailLabel(thumbnail, active, selected))
    }

    /*
    fun selectPhoto() {
        noticeFrame.selectPhoto(photoEntity)
    }
     */

    /*
    fun getPhoto(): PhotoEntity {
        return photoEntity
    }
     */

    /*
    fun isActive(): Boolean {
        return active
    }
     */

    /*
    fun activate() {
        log.debug("activate()")
        active = true

        val worker = ThumbnailWorker(photoEntity, active, thumbnailLabel)
        worker.execute()

        button.isEnabled = true
        //thumbnailLabel.isEnabled = true
        thumbnailLabel.addMouseListener(mouseListener)
    }
     */

    /*
    fun deactivate() {
        log.debug("deactivate()")
        active = false

        val worker = ThumbnailWorker(photoEntity, active, thumbnailLabel)
        worker.execute()

        button.isEnabled = false
        //thumbnailLabel.isEnabled = false
        thumbnailLabel.removeMouseListener(mouseListener)
    }
     */

    /*
    fun displayBorder(visible: Boolean) {
        if (visible && !borderVisible) {
            thumbnailLabel.border = HIGHLIGHT_BORDER
            thumbnailLabel.revalidate()
            borderVisible = true
        } else if (!visible && borderVisible) {
            thumbnailLabel.border = NORMAL_BORDER
            thumbnailLabel.revalidate()
            borderVisible = false
        }
    }
    */
}