package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.gui.Styles
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.Image
import java.awt.image.BufferedImage
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/**
 * Represents a photo which will be/is loaded from file system.
 * todo maybe change fields id and dataLoadedCallback to method parameters of loadPhotoData
 */
class PhotoFile(
    var path: Path,

    /**
     * Ein SHA1-Hashwert hat im Binär-Format 20 Bytes = 160 Bit.
     * Hex-encoded: 40 characters
     * Java data type: byte[]
     */
    var hash: ByteArray? = null,

    /**
     * Breitengrad der Foto-Metadaten
     * y-Achse, Richtung Norden, z.B. 50.08 für Wiesbaden
     */
    var latitude: Float? = null,

    /**
     * Längengrad der Foto-Metadaten
     * x-Achse, Richtung Osten, z.B. 8.24 für Wiesbaden
     */
    var longitude: Float? = null,

    /**
     * Datum und Uhrzeit in CET oder CEST
     */
    var dateTime: ZonedDateTime? = null,

    /**
     * Pixeldaten, Breite & Höhe
     */
    var image: BufferedImage? = null,

    /**
     * Thumbnail gleich mitberechnen
     * todo: Thumbnail-Berechnung in Thread-Pool
     */
    var thumbnail: BufferedImage? = null

) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * Vergleicht 2 Fotos anhand des Pfades.
     */
    fun compareTo(other: PhotoFile): Int {
        return path.compareTo(other.path)
    }

    /**
     * liefert einen ToolTipText
     * Beispiel:
     * <code>
     *   "<html>
     *     20220301_185137.jpg<br>
     *     01.03.2022, 18:51:37 CET<br>
     *     50.07917, 8.24195
     *   </html>"
     * </code>
     */
    fun getToolTipText(): String {
        val lines = mutableListOf<String>()
        lines.add(getFilename())
        dateTime?.let {
            lines.add(getDateFormatted())
        }
        getGeoPositionFormatted()?.let {
            lines.add(it)
        }

        val text = "<html>${lines.joinToString("<br>")}</html>"
        //log.debug(text)
        return text
    }

    // todo: may return null, if no date is given
    fun getDateFormatted(): String {
        dateTime?.let {
              return it.format(PhotoFileWithFuture.format)
        }
        return ""
    }

    fun getGeoPosition(): GeoPosition? {
        latitude?.let { lat ->
            longitude?.let { lon ->
                GeoPosition(lat.toDouble(), lon.toDouble())
            }
        }
        return null
    }

    fun getGeoPositionFormatted(): String? {
        return if (latitude != null && longitude != null) {
            val lat = "%.5f".format(latitude)
            val lon = "%.5f".format(longitude)
            "$lat, $lon"
        } else {
            null
        }
    }

    fun getHashHex(): String? {
        return hash?.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    /**
     * return only the filename without directory path
     */
    fun getFilename(): String {
        return path.fileName.toString()
    }

    fun makeThumbnail() {
        log.debug("makeThumbnail()")
        image?.let {img ->
            val thumbnailWidth: Int
            val thumbnailHeight: Int
            // Thumbnail-Größe auf die Längere der beiden Bild-Seiten anpassen
            if (img.height > img.width) {
                val scaleFactor = Styles.THUMBNAIL_SIZE.toFloat() / img.height
                thumbnailHeight = Styles.THUMBNAIL_SIZE
                thumbnailWidth = (scaleFactor * img.width).toInt()
            } else {
                val scaleFactor = Styles.THUMBNAIL_SIZE.toFloat() / img.width
                thumbnailHeight = (scaleFactor * img.height).toInt()
                thumbnailWidth = Styles.THUMBNAIL_SIZE
            }
            val scaled = img.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_SMOOTH)
            val tn = BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB)
            tn.graphics.drawImage(scaled, 0, 0, null)
            thumbnail = tn
        }
    }

    companion object {
        val format: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss z")
    }
}