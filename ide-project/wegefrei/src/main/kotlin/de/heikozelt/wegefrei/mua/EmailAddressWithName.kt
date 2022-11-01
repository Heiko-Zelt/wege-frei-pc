package de.heikozelt.wegefrei.mua

import javax.mail.internet.InternetAddress

/**
 * like javax.mail.internet.InternetAddress but less complicated
 */
data class EmailAddressWithName(val address: String, val name: String? = null): Comparable<EmailAddressWithName> {
    fun asInternetAddress(): InternetAddress {
        return InternetAddress(address, name) // address, personal
    }

    fun asText(): String {
        return if(name.isNullOrBlank()) {
            "<${address}>"
        } else {
            "${name} <${address}>"
        }
    }

    override fun compareTo(other: EmailAddressWithName): Int {
        return asText().compareTo(other.asText())
    }
}
