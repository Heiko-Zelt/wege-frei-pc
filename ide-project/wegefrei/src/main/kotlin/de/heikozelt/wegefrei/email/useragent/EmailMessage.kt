package de.heikozelt.wegefrei.email.useragent

import de.heikozelt.wegefrei.email.EmailAddressEntity
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

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
) {

    fun asMimeMessage(session: Session): MimeMessage {
        val msg = PrivateMimeMessage(session)
        msg.addHeader("User-Agent", EmailUserAgent.MAIL_USER_AGENT)
        msg.setFrom(from.asInternetAddress())
        //msg.addHeader("Return-Receipt-To", emailMessage.from.address)
        //msg.addHeader("Disposition-Notification-To", emailMessage.from.address)
        tos.forEach { msg.addRecipient(Message.RecipientType.TO, it.asInternetAddress()) }
        ccs.forEach { msg.addRecipient(Message.RecipientType.CC, it.asInternetAddress()) }
        msg.setSubject(subject, "UTF-8")

        val multipart = MimeMultipart()
        val mainPart = MimeBodyPart()
        mainPart.setText(coverLetter, "utf-8", "html")
        multipart.addBodyPart(mainPart)

        attachments.forEach {
            multipart.addBodyPart(it.asMimeBodyPart())
        }

        msg.setContent(multipart)
        return msg
    }
}