package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.SELECTED_PHOTOS_BACKGROUND
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import org.slf4j.LoggerFactory
import javax.swing.BoxLayout
import javax.swing.BoxLayout.X_AXIS
import javax.swing.JPanel

class SelectedPhotosPanel(private val noticeFrame: NoticeFrame) : JPanel(),
    SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val miniSelectedPhotoPanels = arrayListOf<MiniSelectedPhotoPanel>()

    init {
        border = NO_BORDER
        layout = BoxLayout(this, X_AXIS)
        background = SELECTED_PHOTOS_BACKGROUND

        // nicht notwendig, wenn selectedPhotos anfänglich leer ist und Observer vorher schon registriert ist
        // aber man weiß ja nie
        for (photo in noticeFrame.getSelectedPhotos().getPhotos()) {
            log.warn("observer zu spät registriert?")
            val panel = MiniSelectedPhotoPanel(noticeFrame, photo)
            miniSelectedPhotoPanels.add(panel)
            add(panel)
        }
        autoscrolls = true
    }

    /**
     * get Panel of Photo
     */
    private fun panelWithPhoto(photo: Photo): MiniSelectedPhotoPanel? {
        for (photoPanel in miniSelectedPhotoPanels) {
            if (photoPanel.getPhoto() == photo) {
                return photoPanel
            }
        }
        return null
    }

    fun showBorder(miniSelectedPhotoPanel: MiniSelectedPhotoPanel) {
        for (panel in miniSelectedPhotoPanels) {
            panel.displayBorder(panel == miniSelectedPhotoPanel)
        }
    }

    fun showBorder(photo: Photo) {
        for (panel in miniSelectedPhotoPanels) {
            panel.displayBorder(photo == panel.getPhoto())
        }
    }

    fun hideBorder() {
        for (panel in miniSelectedPhotoPanels) {
            panel.displayBorder(false)
        }
    }

    override fun addedPhoto(index: Int, photo: Photo) {
        log.debug("added photo")
        val panel = MiniSelectedPhotoPanel(noticeFrame, photo)
        miniSelectedPhotoPanels.add(index, panel)
        log.debug("add selected photo panel to container. component count: $componentCount")
        add(panel, index)
        log.debug("after add: component count: $componentCount")
        revalidate()
        repaint()
        revalidate()
        repaint()
    }

    override fun removedPhoto(index: Int, photo: Photo) {
        log.debug("removed photo")
        val panel = panelWithPhoto(photo)
        miniSelectedPhotoPanels.remove(panel)
        log.debug("remove selected photo panel to container. component count: $componentCount")
        remove(panel)
        log.debug("after remove: component count: $componentCount")
        revalidate()
        repaint()
    }
}