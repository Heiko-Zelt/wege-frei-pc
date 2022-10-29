package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.SELECTED_PHOTOS_BACKGROUND
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import org.slf4j.LoggerFactory
import java.util.*
import javax.swing.BoxLayout
import javax.swing.BoxLayout.X_AXIS
import javax.swing.JPanel

class SelectedPhotosPanel(private val noticeFrame: NoticeFrame) : JPanel(),
    SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var photosDir: String? = null
    private val miniSelectedPhotoPanels = arrayListOf<MiniSelectedPhotoPanel>()

    init {
        border = NO_BORDER
        layout = BoxLayout(this, X_AXIS)
        background = SELECTED_PHOTOS_BACKGROUND

        // nicht notwendig, wenn selectedPhotos anfänglich leer ist und Observer vorher schon registriert ist
        // aber man weiß ja nie
        // photosDir ist im Konstruktor noch null. Es kann also so gar nicht funktionieren.
        /*
        var i = 1
        for (photo in noticeFrame.getSelectedPhotos().getPhotos()) {
            log.warn("observer zu spät registriert?")
            photosDir?.let {
                val panel = MiniSelectedPhotoPanel(it, noticeFrame, photo, i)
                miniSelectedPhotoPanels.add(panel)
                add(panel)
            }

            i++
        }
        */
        autoscrolls = true
    }


    fun setPhotosDirectory(photosDir: String) {
        this.photosDir = photosDir
    }

    /**
     * get Panel of Photo
     */
    private fun panelWithPhoto(photo: Photo): MiniSelectedPhotoPanel? {
        for (photoPanel in miniSelectedPhotoPanels) {
            if (photoPanel.getPhoto() == photo) {
                return photoPanel
            }
        }
        return null
    }

    /**
     * zeigt bei einem Panel den Rahmen hervorgehoben an,
     * bei allen anderen normal
     */
    fun showBorder(miniSelectedPhotoPanel: MiniSelectedPhotoPanel) {
        for (panel in miniSelectedPhotoPanels) {
            panel.displayBorder(panel == miniSelectedPhotoPanel)
        }
    }

    /**
     * zeigt bei einem Panel den Rahmen hervorgehoben an,
     * bei allen anderen normal
     */
    fun showBorder(photo: Photo) {
        for (panel in miniSelectedPhotoPanels) {
            panel.displayBorder(photo == panel.getPhoto())
        }
    }

    /**
     * zeigt bei allen Panel den Rahmen normal an
     */
    fun hideBorder() {
        for (panel in miniSelectedPhotoPanels) {
            panel.displayBorder(false)
        }
    }

    override fun selectedPhoto(index: Int, photo: Photo) {
        log.debug("added photo")
        photosDir?.let { dir ->
            val panel = MiniSelectedPhotoPanel(dir, noticeFrame, photo, index)
            miniSelectedPhotoPanels.add(index, panel)
            log.debug("add selected photo panel to container. component count: $componentCount")
            add(panel, index)

            log.debug("after add: component count: $componentCount")
            // bei allen nachfolgenden Fotos den Index-Text ändern
            for (i in index + 1 until miniSelectedPhotoPanels.size) {
                miniSelectedPhotoPanels[i].updateText(i)
            }

            // todo Prio 3: gemeinsame Basis-Klasse für MaxiPhotoPanel und MaxiSelectedPhotoPanel
            val zoomComponent = noticeFrame.getZoomComponent()
            if (zoomComponent is MaxiPhotoPanel) {
                if (photo == zoomComponent.getPhoto()) {
                    panel.displayBorder(true)
                }
            }
            if (zoomComponent is MaxiSelectedPhotoPanel) {
                if (photo == zoomComponent.getPhoto()) {
                    panel.displayBorder(true)
                }
            }

            revalidate()
            repaint()
        }
    }

    override fun unselectedPhoto(index: Int, photo: Photo) {
        log.debug("removed photo")
        val panel = panelWithPhoto(photo)
        miniSelectedPhotoPanels.remove(panel)
        log.debug("miniSelectedPhotoPanels.size: ${miniSelectedPhotoPanels.size}")
        // bei allen nachfolgenden Fotos den Index-Text ändern
        for(i in index until miniSelectedPhotoPanels.size) {
            log.debug("i: $i")
            miniSelectedPhotoPanels[i].updateText(i)
        }

        log.debug("remove selected photo panel to container. component count: $componentCount")
        remove(panel)
        log.debug("after remove: component count: $componentCount")
        revalidate()
        repaint()
    }

    override fun replacedPhotoSelection(photos: TreeSet<Photo>) {
        photosDir?.let { dir ->
            removeAll()
            miniSelectedPhotoPanels.clear()
            for ((i, photo) in photos.withIndex()) {
                val panel = MiniSelectedPhotoPanel(dir, noticeFrame, photo, i)
                miniSelectedPhotoPanels.add(panel)
                add(panel)
            }
        }
    }
}