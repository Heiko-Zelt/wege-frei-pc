package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo

/**
 * data of an email message
 * like javax.mail.Message but simplified and specific to the Wege frei! domain
 * todo Prio 4: Sendezeitpunkt hinzufügen
 */
data class EmailMessage(
    val from: EmailAddressWithName,
    val to: Set<EmailAddressWithName>,
    val subject: String,
    val content: String,
    val cc: Set<EmailAddressWithName>? = null,
    val attachedPhotos: Set<Photo>? = null
)