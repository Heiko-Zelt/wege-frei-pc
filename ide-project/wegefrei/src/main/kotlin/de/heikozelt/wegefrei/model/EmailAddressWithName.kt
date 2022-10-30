package de.heikozelt.wegefrei.model

import javax.mail.internet.InternetAddress

/**
 * like javax.mail.internet.InternetAddress but less complicated
 */
class EmailAddressWithName(val address: String, val name: String? = null) {
    fun asInternetAddress(): InternetAddress {
        return InternetAddress(address, name)
    }

    fun asText(): String {
        return if(name.isNullOrBlank()) {
            "<${address}>"
        } else {
            "${name} <${address}>"
        }
    }
}
