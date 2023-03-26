package de.heikozelt.wegefrei.entities

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.hex
import de.heikozelt.wegefrei.model.GeoPositionFormatter
import jakarta.persistence.*
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO
import kotlin.jvm.Transient

@Entity
@Table(name = "PHOTOS")
// todo Prio 3: two constructors instead of default values
/**
 * Strategie: nur von den Fotos, die Metadaten in der Datenbank speichern,
 * zu denen es auch eine oder mehrere Anzeigen gibt.
 */
class PhotoEntity(
    @Id
    var path: String? = null,

    /**
     * Ein SHA1-Hashwert hat im Binär-Format 20 Bytes = 160 Bit.
     * Hex-encoded: 40 characters
     * Java data type: byte[]
     * todo: Prio 1: make this the primary key/id
     */
    @Column(columnDefinition = "BINARY(20)")
    var hash: ByteArray? = null,

    /**
     * Breitengrad der Foto-Metadaten
     * y-Achse, Richtung Norden, z.B. 50.08 für Wiesbaden
     */
    @Column // REAL = A single precision floating point number.
    var latitude: Double? = null,

    /**
     * Längengrad der Foto-Metadaten
     * x-Achse, Richtung Osten, z.B. 8.24 für Wiesbaden
     */
    @Column // REAL = A single precision floating point number.
    var longitude: Double? = null,

    /**
     * Datum und Uhrzeit in UTC
     */
    @Column
    var dateTime: ZonedDateTime? = null,

    // todo Prio 2: show in AllPhotosPanel, which photos have been used for which notice
    @ManyToMany(mappedBy = "photoEntities")
    var noticeEntities: MutableSet<NoticeEntity> = mutableSetOf()
) : Comparable<PhotoEntity> {

    @jakarta.persistence.Transient
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * null is kleiner als ein String, oder?
     */
    override fun compareTo(other: PhotoEntity): Int {
        path?.let {thisPath ->
            other.path?.let { otherPath ->
                return thisPath.compareTo(otherPath)
            }
        }
        return 0
    }

    private fun loadImage() {
        log.debug("loadImage(), path=$path")
        val file = File(path)
        img = ImageIO.read(file)
    }

    @Transient
    private var img: BufferedImage? = null
    // todo Prio3 val img: BufferedImage? by lazy { ... }

    /**
     * Lazy loading
     */
    fun getImage(): BufferedImage? {
        if (img == null) {
            loadImage()
        }
        return img
    }

    fun getDateFormatted(): String {
        dateTime?.let {
            return it.format(format)
        }
        return ""
    }

    fun getGeoPosition(): GeoPosition? {
        latitude?.let { lat ->
            longitude?.let { lon ->
                return GeoPosition(lat, lon)
            }

        }
        return null
    }

    fun getGeoPositionFormatted(): String? {
        return GeoPositionFormatter.format(latitude, longitude)
    }

    fun getHashHex(): String? {
        hash?.let {
            return hex(it)
        }
        return null
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

        if (dateTime != null) {
            lines.add(getDateFormatted())
        }
        getGeoPositionFormatted()?.let {
            lines.add(it)
        }
        getHashHex()?.let {
            lines.add(it)
        }
        val text = "<html>${lines.joinToString("<br>")}</html>"
        //log.debug(text)
        return text
    }

    fun getFilename(): String {
        val path = Paths.get(path)
        return path.fileName.toString()
    }

    /**
     * Load metadata and image asynchronously from the file and call back when done.
     */
    fun load(index: Int, done: (index: Int) -> Unit) {
        // todo Prio 1: load data
        done(index)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        //val fmt = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss z")
        val format: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss z")

        /**
         * todo check if photo is already in database
         */
        fun fromPath(directoryPath: Path, filename: String): PhotoEntity {
            LOG.debug("fromPath(directoryPath=${directoryPath.toString()}, filename=$filename)")
            val file = File(directoryPath.toString(), filename)
            val photo = readPhotoMetadata(file)
            return photo
        }

        private fun readPhotoMetadata(file: File): PhotoEntity {
            LOG.debug("readPhotoMetadata(file=${file.canonicalPath})")
            var latitude: Double? = null
            var longitude: Double? = null
            var date: Date? = null
            val metadata = ImageMetadataReader.readMetadata(file)
            LOG.debug("metadata: $metadata")

            val gpsDirs = metadata.getDirectoriesOfType(GpsDirectory::class.java)
            for (gpsDir in gpsDirs) {
                val geoLocation: GeoLocation? = gpsDir.geoLocation
                if (geoLocation != null && !geoLocation.isZero) {
                    LOG.debug("latitude: ${geoLocation.latitude}, longitude: ${geoLocation.longitude}")
                    latitude = geoLocation.latitude
                    longitude = geoLocation.longitude

                }
            }
            val exifDirs = metadata.getDirectoriesOfType(ExifSubIFDDirectory::class.java)
            for (exifDir in exifDirs) {
                date = exifDir.dateOriginal
                if (date != null) {
                    LOG.debug("date: $date")
                }
            }
            val datTim = if (date == null) {
                null
            } else {
                ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                //Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
            }
            val sha1Hash = "0123456789abcdefghij".toByteArray()
            return PhotoEntity(file.absolutePath, sha1Hash, latitude, longitude, datTim)
        }
    }
}