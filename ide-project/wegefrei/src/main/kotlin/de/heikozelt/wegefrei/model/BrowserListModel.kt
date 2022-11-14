package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.ImageFilenameFilter
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Math.min
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.AbstractListModel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

/**
 * 2 phases:
 * <ol>
 *     <li>phase: load filenames of all photos in the directory (now they are accessible using an index)</li>
 *     <li>phase: on access via getElementAt(index) load photo form filesystem</li>
 * </ol>
 * cache photos in memory?
 *
 * Problem: Photos are filesystem objects, not database entities
 * Solution: introduce a factory method
 *
 * Problem: It takes a lot of time to read the photos from a filesystem.
 * Solution: the list is initially empty and filled later by a background job.
 *
 * Todo Prio 3: Problem: Just reading the filenames takes some time too. Solution: another background job
 */
class BrowserListModel(
    private val cache: LeastRecentlyUsedCache<Path, Photo>,
    private val photoLoader: PhotoLoader,
    private val selectedPhotosListModel: SelectedPhotosListModel
): AbstractListModel<Photo?>(), ListDataListener, PhotoLoaderObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var directoryPath: Path? = null
    private val filenames = mutableListOf<Path>()

    init {
        photoLoader.registerObserver(this)
    }

    override fun getSize(): Int {
        return filenames.size
    }

    override fun doneLoadingFile(photo: Photo) {
        val index = filenames.indexOf(photo.getPath())
        this.fireContentsChanged(this, index, index)
    }

    override fun doneLoadingEntity(photo: Photo) {
        val index = filenames.indexOf(photo.getPath())
        this.fireContentsChanged(this, index, index)
    }

    /**
     * Problem: blockiert, die Methode wird im EDT (Thread: "AWT-EventQueue-0") ausgeführt!
     * Lösung: Erst mal einen leeren Rahmen (mit Dateinamen) anzeigen.
     *
     * Fotos cachen?
     */
    override fun getElementAt(index: Int): Photo? {
        log.debug("getElementAt(index=$index)")
        directoryPath?.let { dirPath ->
            val filename = filenames[index]
            val path = Paths.get(directoryPath.toString(), filename.toString())
            val cachedElement = cache[path]
            return if (cachedElement == null) {
                //val filePath = Paths.get(dirPath.toString(), filenames[index].toString())
                val photo = Photo(path)
                cache[path] = photo
                photoLoader.loadPhotoFile(photo)
                photoLoader.loadPhotoEntity(photo)
                //photoFile.loadPhotoData(executorService) { photoFile -> doneLoadingPhoto(photoFile) }
                photo
            } else {
                cachedElement
            }
        }
        return null
    }

    fun getDirectoryPath(): Path? {
        return directoryPath
    }

    /**
     * fill the list of filenames using a background job
     */
    fun setDirectory(path: Path) {
        log.debug("setDirectory(path=${path})")
        val oldSize = filenames.size
        this.directoryPath = path
        readFilenames()

        if(filenames.size != 0) {
            fireContentsChanged(this, 0, min(filenames.size, oldSize) - 1)
        }
        if(oldSize > filenames.size) {
            fireIntervalRemoved(this, filenames.size, oldSize - 1)
        }
        if(oldSize < filenames.size) {
            fireIntervalAdded(this, oldSize, filenames.size - 1)
        }
    }

    private fun readFilenames() {
        directoryPath?.let {p ->
            val dir = File(p.toString())
            if (!dir.isDirectory) {
                log.error("$p ist kein Verzeichnis.")
            }
            val unsortedFilenames = dir.list(ImageFilenameFilter()) ?: return
            filenames.clear()
            unsortedFilenames.sorted().forEach {
                filenames.add(Paths.get(it))
            }
            log.debug("filenames.size=${filenames.size}")
        }
    }

    /**
     * a photo has been added to the selected photos list
     * gray it out
     */
    override fun intervalAdded(e: ListDataEvent?) {
        e?.let { event ->
            for(selectedIndex in event.index0..event.index1) {
                val photo = selectedPhotosListModel.getElementAt(selectedIndex)
                photo?.let { p ->
                    val browserIndex = filenames.indexOf(p.getPath())
                    this.fireContentsChanged(this, browserIndex, browserIndex)
                }
            }
        }
    }

    /*
    fun unselectedPhoto(photo: Photo) {
        val index = filenames.indexOf(photo.getPath())
        this.fireContentsChanged(this, index, index)
    }
    */

    /**
     * a photo has been removed from the selected photos list
     * show it colorful again
     */
    override fun intervalRemoved(e: ListDataEvent?) {
        if(e is SelectedPhotosListDataEvent) {
            for(photo in e.photos) {
                val index = filenames.indexOfFirst { it == photo.getPath() }
                if(index >= 0) {
                    this.fireContentsChanged(this, index, index)
                }
            }
        }
    }

    /**
     * content of the selected photos list changed
     * file or entity loaded?
     * BrowserListModel is PhotoLoaderObserver, so it's already updated
     */
    override fun contentsChanged(e: ListDataEvent?) {
        // ignore
    }
}