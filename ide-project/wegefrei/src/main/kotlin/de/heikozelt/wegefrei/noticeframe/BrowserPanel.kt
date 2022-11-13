package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.dirnavi.AbsolutePath
import de.heikozelt.wegefrei.dirnavi.DirectoryNavigation
import de.heikozelt.wegefrei.gui.Styles
import de.heikozelt.wegefrei.model.*
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.nio.file.Path
import java.util.*
import javax.swing.GroupLayout
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane

/**
 * 1. Der Frame wird initialisiert
 * 2. Die Dateinamen der Fotos werden aus dem Dateisystem gelesen und fortlaufend indiziert
 * 3. Die Liste wird mit dem
 * Layout:
 * <pre>
 * (/) (home) (heiko) (Pictures) (----<)
 * horizontal list
 * </pre>
 *
 * LeastRecentlyUsedCache(128)
 */
class BrowserPanel(
    private val noticeFrame: NoticeFrame,
    private val dbRepo: DatabaseRepo,
    private val cache: LeastRecentlyUsedCache<Path, Photo>,
    private val photoLoader: PhotoLoader
) : JPanel(), SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val selectedPhotos = noticeFrame.getSelectedPhotos()
    private val browserListModel = BrowserListModel(cache, photoLoader)
    private val browserListCellRenderer = BrowserListCellRenderer()
    private val browserList = JList(browserListModel)
    private val scrollPane = JScrollPane(browserList)
    private val directoryNavigation = DirectoryNavigation { browserListModel.setDirectory(it.asPath()) }
    private var photosDir: Path? = null

    init {

        // if no fixed size is set, all elements will be loaded to get maximum size
        browserList.fixedCellWidth = Styles.THUMBNAIL_SIZE
        browserList.fixedCellHeight = Styles.THUMBNAIL_SIZE
        browserList.visibleRowCount = 1
        browserList.layoutOrientation = JList.HORIZONTAL_WRAP
        browserList.cellRenderer = browserListCellRenderer
        scrollPane.minimumSize = Dimension(Styles.THUMBNAIL_SIZE / 2, Styles.THUMBNAIL_SIZE / 2)

        /*
        todo zur viele Aufrufe von getElementAt() bei Maus-Bewegungen

        browserList.addMouseMotionListener(
            object: MouseMotionAdapter() {
                private var lastToolTipIndex = -1
                override fun mouseMoved(e: MouseEvent?) {
                    val source = e?.source
                    if(source is JList<*>) {
                        val model = source.model
                        if(model is BrowserListModel) {
                            val index = source.locationToIndex(e.point)
                            if(index != lastToolTipIndex && index >= 0) {
                                log.debug("lastToolTipIndex: $lastToolTipIndex, index: $index")
                                val text = model.getElementAt(index)?.getToolTipText()
                                text?.let {
                                    source.toolTipText = it
                                }
                                lastToolTipIndex = index
                            }
                        }
                    }
                }
            }
        )
         */
        browserList.selectionModel = BrowserListSelectionModel(selectedPhotos, browserListModel)
        browserList.addListSelectionListener{
            browserList.selectedValue?.let {photo ->
                //if(photo.getPhotoEntity() !in selectedPhotos.getPhotos()) {
                    noticeFrame.showPhoto(photo)
                //}
            }
        }

        val lay = GroupLayout(this)
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(directoryNavigation)
                .addComponent(scrollPane)
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(directoryNavigation)
                        .addComponent(scrollPane)
        )
        layout = lay

    }

    /**
     * load photos from filesystem as background job
     */
    fun loadData(photosDir: Path) {
        directoryNavigation.setDirectory(AbsolutePath.fromPath(photosDir))
        this.photosDir = photosDir
        browserListModel.setDirectory(photosDir)
        log.debug("photosDir: $photosDir")
        /*
        val dbRepo = noticeFrame.getDatabaseRepo()?:return
        val worker = LoadPhotosWorker(dbRepo, firstPhotoFilename, this)
        worker.execute()
         */

    }

    fun setSelectedPhotos(selectedPhotos: SelectedPhotos) {
        browserListCellRenderer.setSelectedPhotos(selectedPhotos)
    }

    fun setNoticeId(noticeId: Int) {
        browserListCellRenderer.setNoticeId(noticeId)
    }

    /**
     * called from LoadPhotosWorker
    fun appendPhoto(photo: Photo) {
    log.debug("appendPhoto(${photo.path})")
    val active = !selectedPhotos.getPhotos().contains(photo)
    log.debug("photosDir: $photosDir")
    photosDir?.let {
    val miniPhotoPanel = MiniPhotoPanel(it, noticeFrame, photo, active)
    miniPhotoPanels.add(miniPhotoPanel)
    add(miniPhotoPanel, componentCount - 1) // am Ende, aber vor forwardButton
    }
    }
     */

    /*
    private fun panelWithPhoto(photo: Photo): MiniPhotoPanel? {
        for (photoPanel in miniPhotoPanels) {
            if (photoPanel.getPhoto() == photo) {
                return photoPanel
            }
        }
        return null
    }
    */

    fun hideBorder() {
        log.debug("hideBorder()")
        browserList.clearSelection()
    }

    /**
     * Observer-Methode
     */
    override fun selectedPhoto(index: Int, photo: Photo) {
        log.debug("selectedPhoto(index = $index)")
        hideBorder()
    }

    /**
     * Observer-Methode
     */
    override fun unselectedPhoto(index: Int, photo: Photo) {
        log.debug("unselectedPhoto(index = $index)")
        browserListModel.unselectedPhoto(photo)
    }

    /**
     * all selected photos have been replaced
     * active or deactivate panels
     */
    override fun replacedPhotoSelection(photo: TreeSet<Photo>) {
        log.debug("replacedPhotoSelection()")
        /*
        for(panel in miniPhotoPanels) {
            val photo = panel.getPhoto()
            if(photo in photos && panel.isActive()) {
                panel.deactivate()
            }
            if(photo !in photos && !panel.isActive()) {
                panel.activate()
            }
        }
         */
    }

}