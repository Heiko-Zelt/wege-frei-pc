package de.heikozelt.wegefrei.entities

import jakarta.persistence.*
import org.jxmapviewer.viewer.GeoPosition
import java.util.*

@Entity
@Table(name="NOTICES")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,

    @Column(length=3)
    val countrySymbol: String? = null,

    @Column
    val licensePlate: String? = null,

    @Column
    val carMake: String? = null,

    @Column
    val color: String? = null,

    /**
     * Breitengrad des Adress-Markers
     * Y-Achse, Richtung Norden, , z.B. 50.08 für Wiesbaden
     */
    @Column
    val latitude: Float? = null,

    /**
     * Längengrad des Adress-Markers
     * X-Achse, Richtung Osten, , z.B. 8.24 für Wiesbaden
     */
    @Column
    val longitude: Float? = null,

    @Column
    val street: String? = null,

    @Column
    val zipCode: String? = null,

    @Column
    val town: String? = null,

    @Column
    val date: Date? = null,

    @Column
    val environmentalStickerMissing: Boolean = false,

    @Column
    val vehicleInspectionExpired: Boolean = false,

    @Column
    val vehicleInspectionYear: Short = 0,

    @Column
    val vehicleInspectionMonth: Byte = 0,

    @Column
    val vehicleAbandoned: Boolean = false,

    @ManyToMany
    @JoinTable(name= "NOTICES_PHOTOS",
    joinColumns = [JoinColumn(name = "id" /*, referencedColumnName = "filename" */) ],
    inverseJoinColumns = [JoinColumn(name = "filename" /*, referencedColumnName = "id" */)])
    val photos: Set<Photo>? = null
) {
    fun getGeoPosition(): GeoPosition? {
        return if (latitude != null && longitude != null) {
            GeoPosition(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
    }
}