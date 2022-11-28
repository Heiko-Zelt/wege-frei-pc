package de.heikozelt.wegefrei.email

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
class EmailAddressEntity(
    @Id
    val address: String = "",
    @Column
    var name: String? = null
): Comparable<EmailAddressEntity> {
    fun asInternetAddress(): InternetAddress {
        return InternetAddress(address, name) // address, personal
    }

    /**
     * für RecipientComboBox-Editor
     */
    override fun toString(): String {
        return asShortText()
    }

    fun asText(): String {
        return if(address.isBlank()) {
            return ""
        } else {
            if(name.isNullOrBlank()) {
                "<${address}>"
            } else {
                "${name} <${address}>"
            }
        }
    }

    fun asShortText(): String {
        return name?:address
    }

    override fun compareTo(other: EmailAddressEntity): Int {
        return asText().compareTo(other.asText())
    }
}
