package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.SELECTED_PHOTOS_BACKGROUND
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import mu.KotlinLogging
import java.awt.Container
import javax.swing.BoxLayout
import javax.swing.BoxLayout.X_AXIS
import javax.swing.JPanel
import javax.swing.JScrollPane

class SelectedPhotosPanel(private val noticeFrame: NoticeFrame) : JScrollPane(JPanel()),
    SelectedPhotosObserver {

    private val log = KotlinLogging.logger {}

    private val miniSelectedPhotoPanels = arrayListOf<MiniSelectedPhotoPanel>()

    init {
        log.debug("viewport.view" + viewport.view)
        border = NO_BORDER
        val cont = viewport.view
        if (cont != null && cont is Container) {
            cont.layout = BoxLayout(cont, X_AXIS)
            cont.background = SELECTED_PHOTOS_BACKGROUND

            // nicht notwendig, wenn selectedPhotos anfänglich leer ist und Observer vorher schon registriert ist
            // aber man weiß ja nie
            for (photo in noticeFrame.getSelectedPhotos().getPhotos()) {
                log.warn("observer zu spät registriert?")
                val panel = MiniSelectedPhotoPanel(noticeFrame, photo)
                miniSelectedPhotoPanels.add(panel)
                cont.add(panel)
            }
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

    /*
    selectedPhotos.getPhotos().indexOf(photo)
    fun indexOfPhoto(photo: Photo): Int {
        var i = 0
        for (photoPanel in miniSelectedPhotoPanels) {
            if (photoPanel.getPhoto() == photo) {
                return i
            }
            i ++
        }
        return i
    }
    */

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
        val cont = viewport.view
        if (cont != null && cont is Container) {
            log.debug("add selected photo panel to container. component count: ${cont.componentCount}")
            cont.add(panel, index)
            log.debug("after add: component count: ${cont.componentCount}")
            cont.revalidate()
            cont.repaint()
            revalidate()
            repaint()
        }
    }

    override fun removedPhoto(index: Int, photo: Photo) {
        log.debug("removed photo")
        val cont = viewport.view
        val panel = panelWithPhoto(photo)
        miniSelectedPhotoPanels.remove(panel)
        if (cont != null && cont is Container) {
            log.debug("remove selected photo panel to container. component count: ${cont.componentCount}")
            cont.remove(panel)
            log.debug("after remove: component count: ${cont.componentCount}")
            cont.revalidate()
            cont.repaint()
        }
    }
}