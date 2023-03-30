package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json
import de.heikozelt.wegefrei.email.useragent.EmailServerConfig
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.UIManager


/**
 * @param lookAndFeel: java class name example: "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" or "javax.swing.plaf.metal.MetalLookAndFeel"
 */
data class Settings (
    var witness: Witness = Witness(),

    /**
     * Version number of privacy agreement user consented
     */
    @Json(name = "privacy_consent")
    var privacyConsent: Int = 0,

    @Json(name = "email_server")
    var emailServerConfig: EmailServerConfig = EmailServerConfig(),

    @Json(name = "look_and_feel")
    var lookAndFeel: String = "",

    @Json(name = "photos_directory")
    var photosDirectory: String? = null,

    @Json(name = "database_directory")
    var databaseDirectory: String? = null,

    /**
     * Geo-Koordinaten des Tatortes automatisch ermitteln, Mittelpunkt der Geo-Positionen der Fotos
     */
    @Json(name = "auto_geo_position")
    var autoGeoPosition: Boolean = true,

    /**
     * automatisch reverse address lookup ausführen und Adresse eintragen
     */
    @Json(name = "auto_address")
    var autoAddress: Boolean = true,

    /**
     * Anfang, Ende und Dauer der Tatbeobachtung automatisch ausfüllen/ändern
     */
    @Json(name = "auto_offense_time")
    var autoOffenseTime: Boolean = true
): Cloneable {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * To set the look'n'feel the className-Field is needed.
     * getLookAndFeelInfo().className
     */
    fun getLookAndFeelInfo(): UIManager.LookAndFeelInfo? {
        return UIManager.getInstalledLookAndFeels().firstOrNull { it.name == lookAndFeel }
    }

    fun getPhotosPath(): Path {
        return Paths.get(photosDirectory)
    }

    /**
     * Create a deep copy.
     * String values are not copied. They are immutable.
     * The result should be different of copy(), because there are non-primitive fields.
     */
    public override fun clone(): Settings {
        return Settings(
            witness.clone(),
            privacyConsent,
            emailServerConfig.clone(),
            lookAndFeel,
            photosDirectory,
            databaseDirectory,
            autoGeoPosition,
            autoAddress,
            autoOffenseTime
        )
    }

    /*
    is already implemented by data class
    public fun equals(other: Settings) {
        return this.witness == other.witness && this.emailServerConfig == other.email
    }
    */

    companion object {
        fun lookAndFeelNames(): List<String> {
            return UIManager.getInstalledLookAndFeels().map { it.name }
        }
    }
}