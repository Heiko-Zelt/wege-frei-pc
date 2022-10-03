package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.databaseService
import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import java.util.*
import javax.swing.*

/**
 * Haupt-Fenster zum Bearbeiten einer Meldung
 * mit Dispatcher-Funktionen
 */
class MainFrame: JFrame("Wege frei!") {

    private val log = KotlinLogging.logger {}

    private var mainToolBar: MainToolBar
    private var allPhotosPanel: AllPhotosPanel
    private var selectedPhotosPanel: SelectedPhotosPanel
    private var noticeForm: NoticeForm
    private var zoomPanel: ZoomPanel

    init {
        background = Color.green

        val selectedPhoto1 = databaseService.getPhotoByFilename("20220301_184943.jpg")
        val selectedPhoto2 = databaseService.getPhotoByFilename("20220301_184952.jpg")
        val selectedPhoto3 = databaseService.getPhotoByFilename("20220301_185001.jpg")
        val selectedPhotos = TreeSet<Photo>()
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
        constraints.weightx=1.0
        constraints.weighty=0.06

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridwidth = 2
        mainToolBar = MainToolBar()
        add(mainToolBar, constraints)

        constraints.gridy++
        constraints.weighty=0.17
        allPhotosPanel = AllPhotosPanel(this, "20220301_184943.jpg", selectedPhotos)
        add(allPhotosPanel, constraints)

        constraints.gridy++
        selectedPhotosPanel = SelectedPhotosPanel(this, selectedPhotos)
        add(selectedPhotosPanel, constraints)

        constraints.gridy++
        constraints.weightx=0.5
        constraints.weighty=0.60
        constraints.gridwidth = 1
        noticeForm = NoticeForm(this)
        add(noticeForm, constraints)

        constraints.gridx=1
        constraints.fill = BOTH
        zoomPanel = ZoomPanel(this)
        add(zoomPanel, constraints)

        setSize(1000, 700)
        isVisible = true
    }

    /**
     * wählt ein Foto aus
     */
    fun selectPhoto(miniPhotoPanel: MiniPhotoPanel) {
        log.debug("select photo")
        allPhotosPanel.deactivatePhoto(miniPhotoPanel)
        selectedPhotosPanel.addPhoto(miniPhotoPanel.getPhoto())
    }

    /**
     * wählt ein Foto aus
     */
    fun selectPhoto(photo: Photo) {
        log.debug("select photo")
        allPhotosPanel.deactivatePhoto(photo)
        selectedPhotosPanel.addPhoto(photo)
    }

    /**
     * entfernt ein Foto aus der Auswahl für die Meldung
     */
    fun unselectPhoto(photoPanel: MiniSelectedPhotoPanel) {
        log.debug("unselect photo")
        selectedPhotosPanel.removePhoto(photoPanel)
        allPhotosPanel.activatePhoto(photoPanel.getPhoto())
    }

    /**
     * entfernt ein Foto aus der Auswahl für die Meldung
     */
    fun unselectPhoto(photo: Photo) {
        log.debug("unselect photo")
        selectedPhotosPanel.removePhoto(photo)
        allPhotosPanel.activatePhoto(photo)
    }

    /**
     * zeigt im Zoom-Bereich eine große Landkarte an
     */
    fun showMaxiMap() {
        log.debug("show maxi map")
        noticeForm.getMiniMap().displayBorder(true)
        zoomPanel.showMap()
        allPhotosPanel.hideBorder()
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich ein großes Foto an
     */
    fun showPhoto(miniPhotoPanel: MiniPhotoPanel) {
        log.debug("show photo")
        zoomPanel.showPhoto(miniPhotoPanel.getPhoto())
        noticeForm.getMiniMap().displayBorder(false)
        allPhotosPanel.showBorder(miniPhotoPanel)
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich ein großes bereits ausgewähltes Foto an
     */
    fun showSelectedPhoto(miniSelectedPhotoPanel: MiniSelectedPhotoPanel) {
        log.debug("show selected photo")
        zoomPanel.showSelectedPhoto(miniSelectedPhotoPanel.getPhoto())
        noticeForm.getMiniMap().displayBorder(false)
        allPhotosPanel.hideBorder()
        selectedPhotosPanel.showBorder(miniSelectedPhotoPanel)
    }

    companion object {
        val NORMAL_BORDER = BorderFactory.createLineBorder(Color.black)
        val HIGHLIGHT_BORDER = BorderFactory.createLineBorder(Color.yellow)
        val NO_BORDER = BorderFactory.createEmptyBorder()

        val TEXT_COLOR = Color.white

        val TOOLBAR_BACKGROUND = Color(50, 50, 50)
        val ALL_PHOTOS_BACKGROUND = Color(20, 20, 20)
        val SELECTED_PHOTOS_BACKGROUND = Color(50, 50, 50)
        val FORM_BACKGROUND = Color(20, 20, 20)
        val ZOOM_PANEL_BACKGROUND = Color(35, 35, 35)
    }
}