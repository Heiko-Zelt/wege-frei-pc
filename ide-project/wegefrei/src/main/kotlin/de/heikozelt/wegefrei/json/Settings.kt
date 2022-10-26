package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json
import org.slf4j.LoggerFactory
import javax.swing.UIManager


/**
 * @param lookAndFeel: java class name example: "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" or "javax.swing.plaf.metal.MetalLookAndFeel"
 */
data class Settings (
    var witness: Witness = Witness(),

    @Json(name = "email_server")
    var emailServerConfig: EmailServerConfig = EmailServerConfig(),

    @Json(name = "look_and_feel")
    var lookAndFeel: String = "",

    @Json(name = "photos_directory")
    var photosDirectory: String = "~",

    @Json(name = "database_directory")
    var databaseDirectory: String = "~"
): Cloneable {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * To set the look'n'feel the className-Field is needed.
     * getLookAndFeelInfo().className
     */
    fun getLookAndFeelInfo(): UIManager.LookAndFeelInfo? {
        return UIManager.getInstalledLookAndFeels().firstOrNull { it.name == lookAndFeel }
    }

    /**
     * Create a deep copy.
     * String values are not copied. They are immutable.
     * The result should be different of copy(), because there are non-primitive fields.
     */
    public override fun clone(): Settings {
        return Settings(
            witness.clone(),
            emailServerConfig.clone(),
            lookAndFeel,
            photosDirectory,
            databaseDirectory
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