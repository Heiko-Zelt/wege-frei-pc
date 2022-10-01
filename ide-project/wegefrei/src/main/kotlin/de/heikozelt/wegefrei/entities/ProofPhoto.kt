package de.heikozelt.wegefrei.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name="PROOF_PHOTOS")
class ProofPhoto(
    @Id
    val filename: String? = null,

    @Column
    val longitude: Float? = 0f,

    @Column
    val latitude: Float? = 0f,

    @Column
    val dateTime: ZonedDateTime? = null
)