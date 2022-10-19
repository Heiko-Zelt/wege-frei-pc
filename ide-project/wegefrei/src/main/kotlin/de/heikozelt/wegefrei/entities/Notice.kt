package de.heikozelt.wegefrei.entities

import de.heikozelt.wegefrei.model.NoticeState
import jakarta.persistence.*
import org.jxmapviewer.viewer.GeoPosition
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "NOTICES")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    // todo: entscheiden ob UTC oder CET/CEST?
    // TIMESTAMP WITH TIME ZONE
    @Column
    var observationTime: ZonedDateTime? = null,

    var sentTime: ZonedDateTime? = null,

    @Column(length = 3)
    var countrySymbol: String? = null,

    @Column
    var licensePlate: String? = null,

    @Column
    var vehicleMake: String? = null,

    @Column
    var color: String? = null,

    /**
     * Breitengrad des Adress-Markers.
     * Y-Achse, Richtung Norden, , z.B. 50.08 für Wiesbaden.
     * Die Geo-Koordinaten müssen nicht zwingend ans Ordnungsamt übermittelt werden.
     * Sie sind aber zur eigenen Nutzung sinnvoll.
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

    /**
     * Beispiele:
     * <ul>
     *   <li>"in der Hauptstraße an der Ecke Schulstraße"</li>
     *   <li>"am Staatstheater"</li>
     *   <li>"Bushaltestelle Museum"</li>
     * </ul>
     */
    @Column
    var locationDescription: String? = null,

    @Column
    var duration: Int? = null,

    /**
     * Ordnungswidrigkeit/Verstoß
     * American English: offense, British: offence
     */
    @Column
    var offense: Int? = null,

    /**
     * mit Behinderung
     * keine Angabe (null) und negative Angabe (false) sind gleichbedeutend
     * Deswegen nur true oder false, aber kein null erlaubt.
     */
    @Column
    var obstruction: Boolean = false,

    /**
     * mit Gefaehrdung
     */
    @Column
    var endangering: Boolean = false,

    /**
     * Fahrzeug war verlassen
     * keine Angabe (null) und false sind gleichbedeutend
     */
    @Column
    var vehicleAbandoned: Boolean = false,

    /**
     * Warnblinkanlage eingeschaltet
     */
    @Column
    var warningLights: Boolean = false,

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
     * Umweltplakette fehlte in Umweltzone
     * keine Angabe (null) und false sind gleichbedeutend,
     * deswegen ist null nicht erlaubt und false der Standardwert.
     */
    @Column
    var environmentalStickerMissing: Boolean = false,

    @Column
    var recipient: String? = null,

    @ManyToMany
    @JoinTable(
        name = "NOTICES_PHOTOS",
        joinColumns = [JoinColumn(name = "id" /*, referencedColumnName = "filename" */)],
        inverseJoinColumns = [JoinColumn(name = "filename" /*, referencedColumnName = "id" */)]
    )
    var photos: Set<Photo> = setOf(),

    @Column
    var note: String? = null
) {

    @Column
    val createdTime = ZonedDateTime.now()

    fun getCreatedTimeFormatted(): String {
        val d = createdTime
        return if (d == null) {
            ""
        } else {
            d.format(dateTimeFormat)
        }
    }

    /**
     * Die Beobachtungszeit muss nicht mit der Tatzeit identisch sein.
     * Das Fahrzeug kann vorher schon falsch gehalten/geparkt haben und/oder nachher immer noch.
     */
    fun getObservationTimeFormatted(): String {
        val d = observationTime
        return if (d == null) {
            ""
        } else {
            d.format(dateTimeFormat)
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

    fun setGeoPosition(position: GeoPosition?) {
        latitude = position?.latitude?.toFloat()
        longitude = position?.longitude?.toFloat()
    }

    /**
     * Sind alle Pflichtfelder ausgefüllt?
     * (Zwischenspeichern geht immer,
     * aber zum Absenden einer E-Mail müssen bestimmte Felder ausgefüllt sein. )
     */
    fun isComplete(): Boolean {
        val isOffenseComplete = when (offense) {
            null -> false // keine Angabe
            1 -> note != null // sonstiges Vergehen, siehe Hinweis
            else -> true // Vergehen aus Katalog
        }
        val isVehicleInspectionComplete = when (vehicleInspectionExpired) {
            false -> true
            else -> vehicleInspectionMonth != null && vehicleInspectionYear != null
        }
        return isOffenseComplete && isVehicleInspectionComplete && licensePlate != null && street != null
                && zipCode != null && town != null && observationTime != null && photos.isNotEmpty()
    }

    /**
     * Wurde schon eine E-Mail abgesendet?
     * todo Prio 2: mehrmals versenden ggf. an unterschiedliche Empfänger oder BCC, eigene Datenbank-Tabelle
     * todo Prio 3: Outbox. Nachricht erst in den Postausgang legen. Im Hintergrund-Thread wird versendet.
     * isSent() liefert dann true, wenn mindestend eine E-Mail erfolgreich versendet wurde
     */
    fun isSent(): Boolean {
        return sentTime != null
    }

    fun getState(): NoticeState {
        return if (isSent()) {
            NoticeState.SENT
        } else if (isComplete()) {
            NoticeState.COMPLETE
        } else {
            NoticeState.INCOMPLETE
        }
    }

    companion object {
        val dateTimeFormat: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm z")
    }
}