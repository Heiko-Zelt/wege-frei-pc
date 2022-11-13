package de.heikozelt.wegefrei.model

import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture


/**
 * Represents a photo which will be/is loaded from file system.
 */
class PhotoFileWithFuture(
    private val path: Path,
    private val doneCallback: (PhotoFileWithFuture) -> Unit
) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private val futurePhotoData: CompletableFuture<PhotoData> = CompletableFuture
        .supplyAsync(PhotoDataSupplier(path))
        .thenApply{ it ->
            doneCallback(this)
            it
        }

    fun getPhotoDataBlocking(): PhotoData {
        return futurePhotoData.get()
    }

    fun getPhotoDataIfDone(): PhotoData? {
        return if(futurePhotoData.isDone) {
            futurePhotoData.get()
        } else {
            null
        }
    }

    /**
     * Vergleicht 2 Fotos anhand des Pfades.
     */
    fun compareTo(other: PhotoFileWithFuture): Int {
         return path.compareTo(other.path)
    }

    /**
     * liefert einen ToolTipText
     * Beispiel:
     * <code>
     *   "<html>
     *     20220301_185137.jpg<br>
     *     01.03.2022, 18:51:37 CET<br>
     *     50.079174, 8.241951
     *   </html>"
     * </code>
     */
    fun getToolTipText(): String {
        val lines = mutableListOf<String>()
        lines.add(getFilename())
        getPhotoDataIfDone()?.let { photoData ->
            photoData.date?.let {
                lines.add(photoData.getDateFormatted())
            }
            photoData.getGeoPositionFormatted()?.let {
                lines.add(it)
            }
        }
        val text = "<html>${lines.joinToString("<br>")}</html>"
        //log.debug(text)
        return text
    }

    /**
     * return only the filename without directory path
     */
    fun getFilename(): String {
        return path.fileName.toString()
    }

    companion object {
        val format: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss z")
    }
}