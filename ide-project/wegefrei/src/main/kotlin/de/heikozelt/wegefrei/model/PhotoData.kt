package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.noticeframe.ThumbnailLabel
import org.jxmapviewer.viewer.GeoPosition
import java.awt.image.BufferedImage
import java.time.ZonedDateTime

/**
 * Part of PhotoFile
 */
class PhotoData (
    /**
     * Ein SHA1-Hashwert hat im Binär-Format 20 Bytes = 160 Bit.
     * Hex-encoded: 40 characters
     * Java data type: byte[]
     */
    val hash: ByteArray? = null,

    /**
     * Breitengrad der Foto-Metadaten
     * y-Achse, Richtung Norden, z.B. 50.08 für Wiesbaden
     */
    val latitude: Float? = null,

    /**
     * Längengrad der Foto-Metadaten
     * x-Achse, Richtung Osten, z.B. 8.24 für Wiesbaden
     */
    val longitude: Float? = null,

    /**
     * Datum und Uhrzeit in CET oder CEST
     */
    val date: ZonedDateTime? = null,

    /**
     * Pixeldaten, Breite & Höhe
     */
    var image: BufferedImage? = null,

    /**
     * Thumbnail gleich mitberechnen
     * todo: Thumbnail-Berechnung in Thread-Pool
     */
    var thumbnailLabel: ThumbnailLabel? = null
) {
    // todo: may return null, if no date is given
    fun getDateFormatted(): String {
        return if(date == null) {
            ""
        } else {
            date.format(PhotoFileWithFuture.format)
        }
    }

    fun getGeoPosition(): GeoPosition? {
        return if(latitude != null && longitude != null) {
            GeoPosition(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
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
}
