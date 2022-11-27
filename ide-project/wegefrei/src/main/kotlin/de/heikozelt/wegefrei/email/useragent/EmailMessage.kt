package de.heikozelt.wegefrei.email.useragent

import de.heikozelt.wegefrei.email.EmailAddressEntity
import java.util.*

/**
 * data of an email message
 * like javax.mail.Message but simplified and specific to the Wege frei! domain
 * todo Prio 4: Sendezeitpunkt hinzufügen
 */
data class EmailMessage(
    val from: EmailAddressEntity,
    val tos: TreeSet<EmailAddressEntity> = TreeSet<EmailAddressEntity>(),
    val subject: String,
    val coverLetter: String,
    val ccs: TreeSet<EmailAddressEntity> = TreeSet<EmailAddressEntity>(),
    val attachments: LinkedList<EmailAttachment> = LinkedList<EmailAttachment>()
)