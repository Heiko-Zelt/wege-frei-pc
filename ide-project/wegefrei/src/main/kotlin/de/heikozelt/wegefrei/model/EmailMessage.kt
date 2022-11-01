package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Photo
import java.util.*

/**
 * data of an email message
 * like javax.mail.Message but simplified and specific to the Wege frei! domain
 * todo Prio 4: Sendezeitpunkt hinzufügen
 */
data class EmailMessage(
    val from: EmailAddressWithName,
    val tos: TreeSet<EmailAddressWithName> = TreeSet<EmailAddressWithName>(),
    val subject: String,
    val content: String,
    val ccs: TreeSet<EmailAddressWithName> = TreeSet<EmailAddressWithName>(),
    val attachedPhotos: TreeSet<Photo> = TreeSet<Photo>()
)