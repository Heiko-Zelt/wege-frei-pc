package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.MainFrame.Companion.ZOOM_PANEL_BACKGROUND
import mu.KotlinLogging
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import javax.swing.JPanel

class ZoomPanel(private var mainFrame: MainFrame): JPanel() {

    private val log = KotlinLogging.logger {}

    init {
        background = ZOOM_PANEL_BACKGROUND
        border = MainFrame.NO_BORDER
        layout = BorderLayout()
        showNothing()
    }

    fun showNothing() {
        if (componentCount == 1) {
            remove(0)
            revalidate()
        }
    }

    fun showPhoto(photo: Photo) {
        log.debug("show photo")
        if (componentCount == 1) {
            val comp = getComponent(0)
            if (comp is MaxiPhotoPanel) {
                if (comp.getPhoto() != photo) {
                    remove(0)
                    addPhoto(photo)
                }
            } else {
                remove(0)
                addPhoto(photo)
            }
        } else {
            addPhoto(photo)
        }
    }

    private fun addPhoto(photo: Photo) {
        log.debug("add photo")
        add(MaxiPhotoPanel(mainFrame, photo))
        revalidate()
    }

    fun showSelectedPhoto(photo: Photo) {
        log.debug("show selected photo")
        if (componentCount == 1) {
            val comp = getComponent(0)
            if (comp is MaxiSelectedPhotoPanel) {
                if (comp.getPhoto() != photo) {
                    remove(0)
                    addSelectedPhoto(photo)
                }
            } else {
                remove(0)
                addSelectedPhoto(photo)
            }
        } else {
            addSelectedPhoto(photo)
        }
    }

    private fun addSelectedPhoto(photo: Photo) {
        log.debug("add selected photo")
        add(MaxiSelectedPhotoPanel(mainFrame, photo))
        revalidate()
    }

    fun showMap() {
        if (componentCount == 1) {
            if (getComponent(0) !is MaxiMap) {
                remove(0)
                addMap()
            }
        } else {
            addMap()
        }
    }

    private fun addMap() {
        log.debug("add map")
        add(MaxiMap(), CENTER)
        revalidate()
    }

    fun getMaxiPhoto(): Photo? {
        return if(componentCount == 1) {
            val comp = getComponent(0)
            if(comp is MaxiPhotoPanel) {
                comp.getPhoto()
            } else if(comp is MaxiSelectedPhotoPanel) {
                comp.getPhoto()
            } else {
                null
            }
        } else {
            null
        }
    }
}