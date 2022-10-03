package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.databaseService
import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS

class AllPhotosPanel(private val mainFrame: MainFrame, private var firstPhotoFilename: String, private var selectedPhotos: Set<Photo>): JPanel() {

    private val log = KotlinLogging.logger {}

    val photoPanels = arrayListOf<PhotoPanel>()

    init {
        layout = BoxLayout(this, X_AXIS);

        val backButton = JButton("<")
        add(backButton)

        var photos = databaseService.getPhotos(firstPhotoFilename, 6)
        for(photo in photos) {
            val active = !selectedPhotos.contains(photo)
            val photoPanel = PhotoPanel(mainFrame, photo, active)
            photoPanels.add(photoPanel)
            add(photoPanel)
        }

        val forwardButton = JButton(">")
        add(forwardButton)
    }

    private fun panelWithPhoto(photo: Photo): PhotoPanel? {
        for(photoPanel in photoPanels) {
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

    fun deactivatePhoto(photoPanel: PhotoPanel) {
        photoPanel.deactivate()
    }
}