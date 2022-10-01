package de.heikozelt.wegefrei.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name="PROOF_PHOTOS")
class ProofPhoto(
    @Id
    val filename: String? = null,

    @Column
    val longitude: Float? = 0f,

    @Column
    val latitude: Float? = 0f,

    /**
     * Datum und Uhrzeit in UTC
     */
    @Column
    val date: Date? = null
)