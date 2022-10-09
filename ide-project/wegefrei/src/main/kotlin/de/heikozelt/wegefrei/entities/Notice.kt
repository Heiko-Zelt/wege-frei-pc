package de.heikozelt.wegefrei.entities

import jakarta.persistence.*
import org.jxmapviewer.viewer.GeoPosition
import java.time.ZonedDateTime

@Entity
@Table(name="NOTICES")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(length=3)
    var countrySymbol: String? = null,

    @Column
    var licensePlate: String? = null,

    @Column
    var vehicleMake: String? = null,

    @Column
    var color: String? = null,

    /**
     * Breitengrad des Adress-Markers
     * Y-Achse, Richtung Norden, , z.B. 50.08 für Wiesbaden
     */
    @Column
    var latitude: Float? = null,

    /**
     * Längengrad des Adress-Markers
     * X-Achse, Richtung Osten, , z.B. 8.24 für Wiesbaden
     */
    @Column
    var longitude: Float? = null,

    @Column
    var street: String? = null,

    @Column
    var zipCode: String? = null,

    @Column
    var town: String? = null,

    // todo: entscheiden ob UTC oder CET/CEST?
    // TIMESTAMP WITH TIME ZONE
    @Column
    var date: ZonedDateTime? = null,

    @Column
    var duration: Int? = null,

    /**
     * Umweltplakette fehlte in Umweltzone
     * keine Angabe (null) und false sind gleichbedeutend,
     * deswegen ist null nicht erlaubt und false der Standardwert.
     */
    @Column
    var environmentalStickerMissing: Boolean = false,

    /**
     * TÜV/HU-Plakette war abgelaufen
     * keine Angabe (null) und false sind gleichbedeutend
     */
    @Column
    var vehicleInspectionExpired: Boolean = false,

    @Column
    var vehicleInspectionYear: Short? = null,

    @Column
    var vehicleInspectionMonth: Byte? = null,

    /**
     * Fahrzeug war verlassen
     * keine Angabe (null) und false sind gleichbedeutend
     */
    @Column
    var vehicleAbandoned: Boolean = false,

    @Column
    var recipient: String? = null,

    @ManyToMany
    @JoinTable(name= "NOTICES_PHOTOS",
    joinColumns = [JoinColumn(name = "id" /*, referencedColumnName = "filename" */) ],
    inverseJoinColumns = [JoinColumn(name = "filename" /*, referencedColumnName = "id" */)])
    var photos: Set<Photo> = setOf()
) {
    fun getDateFormatted(): String {
        val d = date
        return if(d == null) {
            ""
        } else {
            d.format(Photo.format)
        }
    }

    fun getGeoPosition(): GeoPosition? {
        val lat = latitude
        val lon = longitude
        return if (lat != null && lon != null) {
            GeoPosition(lat.toDouble(), lon.toDouble())
        } else {
            null
        }
    }
}