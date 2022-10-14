package de.heikozelt.wegefrei.entities

import de.heikozelt.wegefrei.App
import jakarta.persistence.*
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import kotlin.jvm.Transient

@Entity
@Table(name="PHOTOS")
class Photo (

    @Id
    val filename: String? = null,

    /**
     * Breitengrad der Foto-Metadaten
     * y-Achse, Richtung Norden, z.B. 50.08 für Wiesbaden
     */
    @Column
    val latitude: Float? = null,

    /**
     * Längengrad der Foto-Metadaten
     * x-Achse, Richtung Osten, z.B. 8.24 für Wiesbaden
     */
    @Column
    val longitude: Float? = null,

    /**
     * Datum und Uhrzeit in UTC
     */
    @Column
    val date: ZonedDateTime? = null,

    @ManyToMany(mappedBy="photos")
    val notices: Set<Notice>? = null
): Comparable<Photo> {

    @jakarta.persistence.Transient
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    // null is kleiner als ein String
    override fun compareTo(other: Photo): Int {
        /*
        if(other == null) {
            throw NullPointerException()
        }
         */
        return if(filename == null && other.filename == null) {
            0
        } else if(filename == null) {
            -1
        } else if(other.filename == null) {
            1
        } else {
            filename.compareTo(other.filename)
        }
    }

    fun loadImage() {
        val file = File(App.PHOTO_DIR, filename)
        //val photo = readPhotoMetadata(file)
        img = ImageIO.read(file)
    }

    @Transient
    private var img: BufferedImage? = null

    fun getImage(): BufferedImage? {
        if(img == null) {
            loadImage()
        }
        return img
    }

    fun getDateFormatted(): String {
        return if(date == null) {
            ""
        } else {
            date.format(format)
        }
    }

    fun getGeoPosition(): GeoPosition? {
        return if(latitude != null && longitude != null) {
            GeoPosition(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
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
        if(filename != null) {
            lines.add(filename)
        }
        if (date != null) {
            lines.add(getDateFormatted())
        }
        if (latitude != null && longitude != null) {
            lines.add("$latitude, $longitude")
        }
        val text = "<html>${lines.joinToString("<br>")}</html>"
        log.debug(text)
        return text
    }

    companion object {
        //val fmt = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss z")
        val format: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss z")
    }
}