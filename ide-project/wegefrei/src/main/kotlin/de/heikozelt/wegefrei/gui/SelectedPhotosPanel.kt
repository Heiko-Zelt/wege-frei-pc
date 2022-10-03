package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.databaseService
import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import java.awt.Container
import java.util.*
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS

class SelectedPhotosPanel(private val mainFrame: MainFrame, var photos: TreeSet<Photo>): JScrollPane(JPanel()) {

    private val log = KotlinLogging.logger {}

    init {
        log.debug("viewport.view" + viewport.view)
        val cont = viewport.view
        if(cont != null && cont is Container) {
            cont.layout = BoxLayout(cont, X_AXIS);

            for(photo in photos) {
                if(photo != null) {
                    cont.add(SelectedPhotoPanel(mainFrame, photo))
                }
            }

            autoscrolls = true
        }
    }

    fun removePhoto(photoPanel: SelectedPhotoPanel) {
        val cont = viewport.view
        if(cont != null && cont is Container) {
            log.debug("remove photo")
            photos.remove(photoPanel.getPhoto())
            cont.remove(photoPanel)
            cont.revalidate()
            // revalidate() funktioniert nicht richtig
            cont.repaint()
        }
    }

    fun addPhoto(photo: Photo) {
        val cont = viewport.view
        if(cont != null && cont is Container) {
            log.debug("add photo")
            photos.add(photo)
            val index = photos.indexOf(photo)
            cont.add(SelectedPhotoPanel(mainFrame, photo), index)
            cont.revalidate()
        }
    }
}