package de.heikozelt.wegefrei.entities

import de.heikozelt.wegefrei.model.CountrySymbol
import de.heikozelt.wegefrei.model.NoticeState
import jakarta.persistence.*
import org.jxmapviewer.viewer.GeoPosition
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "NOTICES")
// todo Prio 3: two constructors instead of default values
class NoticeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    // todo: Prio 3: entscheiden ob UTC oder CET/CEST? Wie ist es in den Photo-Meta-Daten gespeichert?
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
     * Sie sind aber zur eigenen Nutzung sehr sinnvoll.
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
    var offense: String? = null,

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
     * todo Prio 2: Warnblinkanlage-Check-Box in NoticeFormFields anzeigen
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

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "NOTICES_PHOTOS",
        joinColumns = [JoinColumn(name = "notice_id", foreignKey = ForeignKey(name = "FK_NOTICE_ID")) ],
        inverseJoinColumns = [JoinColumn(name = "photo_path", foreignKey = ForeignKey(name = "FK_PHOTO_PATH"))]
    )
    var photoEntities: MutableSet<PhotoEntity> = mutableSetOf(),

    @Column
    var note: String? = null
) {

    @Column
    var createdTime: ZonedDateTime? = null

    fun getCreatedTimeFormatted(): String {
        val d = createdTime
        return if (d == null) {
            ""
        } else {
            d.format(dateTimeFormat)
        }
    }

    fun setCreatedTimeNow() {
        createdTime = ZonedDateTime.now()
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

    /**
     * Achtung: liefert bei jedem Aufruf ein neues Objekt
     */
    fun getGeoPosition(): GeoPosition? {
        val lat = latitude
        val lon = longitude
        return if (lat != null && lon != null) {
            GeoPosition(lat.toDouble(), lon.toDouble())
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

    fun setGeoPosition(position: GeoPosition?) {
        latitude = position?.latitude?.toFloat()
        longitude = position?.longitude?.toFloat()
    }

    fun getAddress(): String? {
        return if(street == null && zipCode == null && town == null ) {
            null
        } else {
            "$street, $zipCode $town"
        }
    }

    fun getDurationFormatted(): String? {
        return duration?.let {
            when(it) {
                0 -> "weniger als 1 Minute"
                1 -> "1 Minute"
                else -> "${duration?.toString()} Minuten"
            }
        }
    }

    fun getCountryFormatted(): String? {
        return countrySymbol?.let {
            val name = CountrySymbol.fromAbbreviation(it).countryName
            "$it ($name)"
        }
    }

    fun getCircumstancesHtml(): String? {
        val lines = mutableListOf<String>()
        if(vehicleAbandoned) lines.add("Das Fahrzeug war verlassen")
        if(warningLights) lines.add("Die Warnblinkanlage war eingeschaltet")
        if(obstruction) lines.add("mit Behinderung")
        if(endangering) lines.add("mit Gefährdung")
        if(environmentalStickerMissing) lines.add("Die Umweltplakette fehlte/war ungültig")
        if(vehicleInspectionExpired) lines.add("Die HU-Plakette war abgelaufen")
        return if(lines.isEmpty()) {
            null
        } else {
            lines.joinToString("<br>")
        }
    }

    fun getInspectionMonthYear(): String? {
        return if(vehicleInspectionMonth == null || vehicleInspectionYear == null) {
            null
        } else {
            "$vehicleInspectionMonth/$vehicleInspectionYear"
        }
    }

    /**
     * Sind alle Pflichtfelder ausgefüllt?
     * (Zwischenspeichern geht immer,
     * aber zum Absenden einer E-Mail müssen bestimmte Felder ausgefüllt sein. )
     */
    fun isComplete(): Boolean {
        val isVehicleInspectionComplete = when (vehicleInspectionExpired) {
            false -> true
            else -> vehicleInspectionMonth != null && vehicleInspectionYear != null
        }
        return offense != null && isVehicleInspectionComplete && licensePlate != null && street != null
                && zipCode != null && town != null && observationTime != null && recipient != null
                && photoEntities.isNotEmpty()
    }

    /**
     * Wurde schon eine E-Mail abgesendet?
     * todo Prio 2: mehrmals versenden ggf. an unterschiedliche Empfänger oder BCC, eigene Datenbank-Tabelle
     * todo Prio 3: Outbox. Nachricht erst in den Postausgang legen. Im Hintergrund-Thread wird versendet.
     * isSent() liefert dann true, wenn mindestend eine E-Mail erfolgreich versendet wurde
     * todo Prio 1: E-Mail synchron versenden
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

        fun createdNow(): NoticeEntity {
            val n = NoticeEntity()
            n.setCreatedTimeNow()
            return n
        }
    }
}