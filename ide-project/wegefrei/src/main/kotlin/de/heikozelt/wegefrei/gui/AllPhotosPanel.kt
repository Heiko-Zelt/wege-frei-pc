package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.ALL_PHOTOS_BACKGROUND
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import org.slf4j.LoggerFactory
import java.util.*
import javax.swing.BoxLayout
import javax.swing.BoxLayout.X_AXIS
import javax.swing.JButton
import javax.swing.JPanel

class AllPhotosPanel(private val noticeFrame: NoticeFrame) : JPanel(), SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var firstPhotoFilename: String? = null
    private val selectedPhotos = noticeFrame.getSelectedPhotos()
    private val miniPhotoPanels = arrayListOf<MiniPhotoPanel>()

    init {
        background = ALL_PHOTOS_BACKGROUND
        layout = BoxLayout(this, X_AXIS)

        val backButton = JButton("<")
        add(backButton)

        val forwardButton = JButton(">")
        add(forwardButton)
    }

    // todo Prio 2: load from database as background job
    fun loadData(firstPhotoFilename: String) {
        val photos = noticeFrame.getDatabaseService().getPhotos(firstPhotoFilename, 20)
        for (photo in photos) {
            val active = !selectedPhotos.getPhotos().contains(photo)
            val miniPhotoPanel = MiniPhotoPanel(noticeFrame, photo, active)
            miniPhotoPanels.add(miniPhotoPanel)
            add(miniPhotoPanel, 1) // hinter backButton
        }
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

    /**
     * all selected photos have been replaced
     * active or deactivate panels
     */
    override fun replacedAllPhotos(photos: TreeSet<Photo>) {
        for(panel in miniPhotoPanels) {
            val photo = panel.getPhoto()
            if(photo in photos && panel.isActive()) {
                panel.deactivate()
            }
            if(photo !in photos && !panel.isActive()) {
                panel.activate()
            }
        }
    }

}