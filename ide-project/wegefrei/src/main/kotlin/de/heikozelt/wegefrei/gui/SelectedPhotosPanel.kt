package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.databaseService
import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import java.awt.Container
import javax.swing.*
import javax.swing.BoxLayout.X_AXIS

class SelectedPhotosPanel(private val mainFrame: MainFrame, var photos: Set<Photo>): JScrollPane(JPanel()) {

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
            cont.remove(photoPanel)
            cont.revalidate()
            // revalidate() funktioniert nicht richtig
            cont.repaint()
        }
    }

    // todo: an der chronologisch richtigen Stelle einfügen
    fun addPhoto(photo: Photo) {
        val cont = viewport.view
        if(cont != null && cont is Container) {
            log.debug("add photo")
            val index = 0
            cont.add(SelectedPhotoPanel(mainFrame, photo), index)
            cont.revalidate()
        }
    }
}