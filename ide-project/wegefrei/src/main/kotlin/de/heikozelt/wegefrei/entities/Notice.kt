package de.heikozelt.wegefrei.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="NOTICES")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = 0,

    @Column
    val countrySymbol: Double? = 0.0,

    @Column
    val licensePlate: Double? = 0.0,

    @Column
    val carMake: String? = null,

    @Column
    val color: String? = null,

    /**
     * west-east/x-position
     */
    @Column
    val longitude: Float? = null,

    /**
     * north-south/y-position
     */
    @Column
    val latitude: Float? = 0f,

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
)