package de.heikozelt.wegefrei.mua

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import javax.mail.internet.InternetAddress

/**
 * like javax.mail.internet.InternetAddress but less complicated
 */
@Entity
@Table(name = "EMAIL_ADDRESSES")
data class EmailAddressEntity(
    @Id
    val address: String = "",
    @Column
    val name: String? = null
): Comparable<EmailAddressEntity> {
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

    override fun compareTo(other: EmailAddressEntity): Int {
        return asText().compareTo(other.asText())
    }
}
