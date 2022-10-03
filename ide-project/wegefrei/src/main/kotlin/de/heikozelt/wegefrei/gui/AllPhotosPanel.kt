package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.databaseService
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.ALL_PHOTOS_BACKGROUND
import mu.KotlinLogging
import java.awt.Color
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS

class AllPhotosPanel(private val mainFrame: MainFrame, private var firstPhotoFilename: String, private var selectedPhotos: Set<Photo>): JPanel() {

    private val log = KotlinLogging.logger {}

    val miniPhotoPanels = arrayListOf<MiniPhotoPanel>()

    init {
        background = ALL_PHOTOS_BACKGROUND
        layout = BoxLayout(this, X_AXIS);

        val backButton = JButton("<")
        add(backButton)

        var photos = databaseService.getPhotos(firstPhotoFilename, 6)
        for(photo in photos) {
            val active = !selectedPhotos.contains(photo)
            val miniPhotoPanel = MiniPhotoPanel(mainFrame, photo, active)
            miniPhotoPanels.add(miniPhotoPanel)
            add(miniPhotoPanel)
        }

        val forwardButton = JButton(">")
        add(forwardButton)
    }

    private fun panelWithPhoto(photo: Photo): MiniPhotoPanel? {
        for(photoPanel in miniPhotoPanels) {
            if(photoPanel.getPhoto() == photo) {
                return photoPanel
            }
        }
        return null
    }

    fun activatePhoto(photo: Photo) {
        log.debug("activate photo")
        panelWithPhoto(photo)?.activate()
    }

    fun deactivatePhoto(miniPhotoPanel: MiniPhotoPanel) {
        miniPhotoPanel.deactivate()
    }

    fun deactivatePhoto(photo: Photo) {
        panelWithPhoto(photo)?.deactivate()
    }

    fun showBorder(miniPhotoPanel: MiniPhotoPanel) {
        for(panel in miniPhotoPanels) {
            panel.displayBorder(panel == miniPhotoPanel)
        }
    }

    fun hideBorder() {
        for(panel in miniPhotoPanels) {
            panel.displayBorder(false)
        }
    }
}