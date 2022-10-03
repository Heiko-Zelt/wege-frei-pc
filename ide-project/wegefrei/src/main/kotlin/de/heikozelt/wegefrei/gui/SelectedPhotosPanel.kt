package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.MainFrame.Companion.SELECTED_PHOTOS_BACKGROUND
import mu.KotlinLogging
import java.awt.Color
import java.awt.Container
import java.util.*
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS

class SelectedPhotosPanel(private val mainFrame: MainFrame, var photos: TreeSet<Photo>) : JScrollPane(JPanel()) {

    private val log = KotlinLogging.logger {}

    private val miniSelectedPhotoPanels = arrayListOf<MiniSelectedPhotoPanel>()

    init {
        log.debug("viewport.view" + viewport.view)
        border = NO_BORDER
        val cont = viewport.view
        if (cont != null && cont is Container) {
            cont.layout = BoxLayout(cont, X_AXIS);
            cont.background = SELECTED_PHOTOS_BACKGROUND

        }

        for (photo in photos) {
            if (photo != null) {
                val panel = MiniSelectedPhotoPanel(mainFrame, photo)
                addPanel(panel)
            }
        }

        autoscrolls = true
    }

    private fun panelWithPhoto(photo: Photo): MiniSelectedPhotoPanel? {
        for (photoPanel in miniSelectedPhotoPanels) {
            if (photoPanel.getPhoto() == photo) {
                return photoPanel
            }
        }
        return null
    }

    private fun addPanel(panel: MiniSelectedPhotoPanel) {
        miniSelectedPhotoPanels.add(panel)
        val cont = viewport.view
        if (cont != null && cont is Container) {
            val index = photos.indexOf(panel.getPhoto())
            cont.add(panel, index)
        }
    }

    private fun removePanel(panel: MiniSelectedPhotoPanel) {
        miniSelectedPhotoPanels.remove(panel)
        val cont = viewport.view
        if (cont != null && cont is Container) {
            cont.remove(panel)
        }
    }

    fun removePhoto(photoPanel: MiniSelectedPhotoPanel) {
        log.debug("removePhoto(photoPanel)")
        val cont = viewport.view
        if (cont != null && cont is Container) {
            log.debug("remove photo")
            photos.remove(photoPanel.getPhoto())
            removePanel(photoPanel)
            cont.revalidate()
            // revalidate() funktioniert nicht richtig
            cont.repaint()
        }
    }

    fun removePhoto(photo: Photo) {
        log.debug("removePhoto(photo)")
        val panel = panelWithPhoto(photo)
        log.debug("panel: $panel")
        if (panel != null) {
            removePhoto(panel)
        }
    }

    fun addPhoto(photo: Photo) {
        val cont = viewport.view
        if (cont != null && cont is Container) {
            log.debug("add photo")
            photos.add(photo)
            val panel = MiniSelectedPhotoPanel(mainFrame, photo)
            addPanel(panel)
            cont.revalidate()
        }
    }

    fun showBorder(miniSelectedPhotoPanel: MiniSelectedPhotoPanel) {
        for(panel in miniSelectedPhotoPanels) {
            panel.displayBorder(panel == miniSelectedPhotoPanel)
        }
    }

    fun hideBorder() {
        for(panel in miniSelectedPhotoPanels) {
            panel.displayBorder(false)
        }
    }
}