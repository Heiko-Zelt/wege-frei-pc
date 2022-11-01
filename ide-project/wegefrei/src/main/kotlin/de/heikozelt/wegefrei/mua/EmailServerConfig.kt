package de.heikozelt.wegefrei.mua

import com.beust.klaxon.Json
import de.heikozelt.wegefrei.json.Tls

/**
 * Part of Settings.
 * The mail server password is not saved.
 * User is asked when sending emails.
 */
data class EmailServerConfig(
    @Json(name = "smtp_host")
    var smtpHost: String = "",

    @Json(name = "smtp_port") // Klaxon kann kein Short
    var smtpPort: Int = 25,

    @Json(name = "smtp_user_name")
    var smtpUserName: String = "",

    var tls: Tls = Tls.START_TLS
): Cloneable {

    /**
     * There is no difference to copy(), isn't it?
     */
    public override fun clone(): EmailServerConfig {
        return EmailServerConfig(
            smtpHost,
            smtpPort,
            smtpUserName,
            tls
        )
    }
}