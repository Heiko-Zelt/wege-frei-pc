package de.heikozelt.wegefrei.entities

import jakarta.persistence.*
import org.jxmapviewer.viewer.GeoPosition
import java.util.*

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

    @Column
    var date: Date? = null,

    @Column
    var environmentalStickerMissing: Boolean = false,

    @Column
    var vehicleInspectionExpired: Boolean = false,

    @Column
    var vehicleInspectionYear: Short = 0,

    @Column
    var vehicleInspectionMonth: Byte = 0,

    @Column
    var vehicleAbandoned: Boolean = false,

    @ManyToMany
    @JoinTable(name= "NOTICES_PHOTOS",
    joinColumns = [JoinColumn(name = "id" /*, referencedColumnName = "filename" */) ],
    inverseJoinColumns = [JoinColumn(name = "filename" /*, referencedColumnName = "id" */)])
    var photos: Set<Photo> = setOf()
) {
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