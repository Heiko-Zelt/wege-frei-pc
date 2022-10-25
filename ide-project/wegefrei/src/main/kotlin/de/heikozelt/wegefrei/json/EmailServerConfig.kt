package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json

/**
 * Part of Settings.
 * The mail server password is not saved.
 * User is asked when sending emails.
 */
class EmailServerConfig(
    @Json(name = "smtp_host")
    var smtpHost: String = "",

    @Json(name = "smtp_port") // Klaxon kann kein Short
    var smtpPort: Int = 25,

    @Json(name = "smtp_user_name")
    var smtpUserName: String = "",

    var tls: Tls = Tls.START_TLS
)