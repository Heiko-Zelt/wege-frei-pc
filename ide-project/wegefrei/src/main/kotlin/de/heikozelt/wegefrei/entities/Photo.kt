package de.heikozelt.wegefrei.entities

import de.heikozelt.wegefrei.PHOTO_DIR
import de.heikozelt.wegefrei.readPhotoMetadata
import jakarta.persistence.*
import org.jxmapviewer.viewer.GeoPosition
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO
import kotlin.jvm.Transient


@Entity
@Table(name="PHOTOS")
class Photo (

    @Id
    val filename: String? = null,

    /**
     * Y-Achse, Richtung Norden, Breitengrad, z.B. 50.08 für Wiesbaden
     */
    @Column
    val latitude: Float? = 0f,

    /**
     * X-Achse, Richtung Osten, Längengrad, z.B. 8.24 für Wiesbaden
     */
    @Column
    val longitude: Float? = 0f,

    /**
     * Datum und Uhrzeit in UTC
     */
    @Column
    val date: Date? = null,

    @ManyToMany(mappedBy="photos")
    val notices: Set<Notice>? = null
): Comparable<Photo> {

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
        val file = File(PHOTO_DIR, filename)
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
        return fmt.format(date)
    }

    fun getGeoPosition(): GeoPosition? {
        return if(latitude != null && longitude != null) {
            GeoPosition(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
    }

    companion object {
        val fmt = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss z")
    }
}