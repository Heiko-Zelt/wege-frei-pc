package de.heikozelt.wegefrei.entities

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="NOTICES")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int,

    @Column
    val countrySymbol: Double,

    @Column
    val licensePlate: Double,

    @Column
    val carMake: String,

    @Column
    val color: String,

    /**
     * west-east/x-position
     */
    @Column
    val longitude: Float,

    /**
     * north-south/y-position
     */
    @Column
    val latitude: Float,

    @Column
    val street: String,

    @Column
    val zipCode: String,

    @Column
    val town: String,

    @Column
    val dateTime: ZonedDateTime,

    @Column
    val environmentalStickerMissing: Boolean,

    @Column
    val vehicleInspectionExpired: Boolean,

    @Column
    val vehicleInspectionYear: Short,

    @Column
    val vehicleInspectionMonth: Byte,

    @Column
    val vehicleAbandoned: Boolean
)