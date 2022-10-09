package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.ALL_PHOTOS_BACKGROUND
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import mu.KotlinLogging
import javax.swing.BoxLayout
import javax.swing.BoxLayout.X_AXIS
import javax.swing.JButton
import javax.swing.JPanel

class AllPhotosPanel(
    private val mainFrame: MainFrame,
    private var firstPhotoFilename: String
) : JPanel(), SelectedPhotosObserver {

    private val log = KotlinLogging.logger {}
    private val selectedPhotos = mainFrame.getSelectedPhotos()
    private val miniPhotoPanels = arrayListOf<MiniPhotoPanel>()

    init {
        background = ALL_PHOTOS_BACKGROUND
        layout = BoxLayout(this, X_AXIS)

        val backButton = JButton("<")
        add(backButton)

        var photos = mainFrame.getDatabaseService().getPhotos(firstPhotoFilename, 20)
        for (photo in photos) {
            val active = !selectedPhotos.getPhotos().contains(photo)
            val miniPhotoPanel = MiniPhotoPanel(mainFrame, photo, active)
            miniPhotoPanels.add(miniPhotoPanel)
            add(miniPhotoPanel)
        }

        val forwardButton = JButton(">")
        add(forwardButton)
    }

    private fun panelWithPhoto(photo: Photo): MiniPhotoPanel? {
        for (photoPanel in miniPhotoPanels) {
            if (photoPanel.getPhoto() == photo) {
                return photoPanel
            }
        }
        return null
    }

    private fun activatePhoto(photo: Photo) {
        log.debug("activate photo")
        panelWithPhoto(photo)?.activate()
    }

    private fun deactivatePhoto(photo: Photo) {
        panelWithPhoto(photo)?.deactivate()
    }

    fun showBorder(miniPhotoPanel: MiniPhotoPanel) {
        for (panel in miniPhotoPanels) {
            panel.displayBorder(panel == miniPhotoPanel)
        }
    }

    private fun showBorder(photo: Photo) {
        for (panel in miniPhotoPanels) {
            panel.displayBorder(photo == panel.getPhoto())
        }
    }

    fun hideBorder() {
        log.debug("hideBorder()")
        for (panel in miniPhotoPanels) {
            panel.displayBorder(false)
        }
    }

    /**
     * Observer-Methode
     */
    override fun addedPhoto(index: Int, photo: Photo) {
        log.debug("addedPhoto()")
        hideBorder()
        deactivatePhoto(photo)
    }

    /**
     * Observer-Methode
     */
    override fun removedPhoto(index: Int, photo: Photo) {
        log.debug("removedPhoto()")
        showBorder(photo)
        activatePhoto(photo)
    }
}