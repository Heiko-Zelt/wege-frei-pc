package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.databaseService
import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter
import org.jxmapviewer.viewer.*
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import javax.swing.*

class MainFrame: JFrame("Wege frei!") {

    private val log = KotlinLogging.logger {}

    private var allPhotosPanel: AllPhotosPanel
    private var selectedPhotosPanel: SelectedPhotosPanel

    init {
        val selectedPhoto1 = databaseService.getPhotoByFilename("20220301_184943.jpg")
        val selectedPhoto2 = databaseService.getPhotoByFilename("20220301_184952.jpg")
        val selectedPhoto3 = databaseService.getPhotoByFilename("20220301_185001.jpg")
        val selectedPhotos = mutableSetOf<Photo>()
        if(selectedPhoto1 != null) {
            selectedPhotos.add(selectedPhoto1)
        }
        if(selectedPhoto2 != null) {
            selectedPhotos.add(selectedPhoto2)
        }
        if(selectedPhoto3 != null) {
            selectedPhotos.add(selectedPhoto3)
        }

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE;

        layout = GridBagLayout();
        val constraints = GridBagConstraints()
        constraints.anchor = WEST
        constraints.fill = BOTH
        constraints.weightx=0.5
        constraints.weighty=0.1

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridwidth = 2
        add(MainToolBar(), constraints)

        constraints.gridy++
        /*
        val photo1 = databaseService.getPhotoByFilename("20220301_184943.jpg")
        val photo2 = databaseService.getPhotoByFilename("20220301_184952.jpg")
        val photo3 = databaseService.getPhotoByFilename("20220301_185001.jpg")
        val photos = mutableSetOf<Photo>()
        if(photo1 != null) {
            photos.add(photo1)
        }
        if(photo2 != null) {
            photos.add(photo2)
        }
        if(photo3 != null) {
            photos.add(photo3)
        }
         */
        allPhotosPanel = AllPhotosPanel(this, "20220301_184943.jpg", selectedPhotos)
        add(allPhotosPanel, constraints)

        constraints.gridy++

        selectedPhotosPanel = SelectedPhotosPanel(this, selectedPhotos)
        add(selectedPhotosPanel, constraints)

        constraints.gridy++
        constraints.gridwidth = 1
        add(NoticeForm(), constraints)

        constraints.gridx=1
        add(ZoomPanel(), constraints)

        setSize(1000, 700)
        isVisible = true
    }

    fun unselectPhoto(photoPanel: SelectedPhotoPanel) {
        log.debug("unselect photo")
        selectedPhotosPanel.removePhoto(photoPanel)
        allPhotosPanel.activatePhoto(photoPanel.getPhoto())
    }

    fun selectPhoto(photoPanel: PhotoPanel) {
        log.debug("select photo")
        allPhotosPanel.deactivatePhoto(photoPanel)
        selectedPhotosPanel.addPhoto(photoPanel.getPhoto())
    }
}