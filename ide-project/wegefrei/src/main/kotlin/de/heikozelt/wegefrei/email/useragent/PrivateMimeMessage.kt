package de.heikozelt.wegefrei.email.useragent

import de.heikozelt.wegefrei.hex
import java.security.SecureRandom
import javax.mail.Session
import javax.mail.internet.MimeMessage

/**
 * A MIME message,
 * which does not include username and computer name in the message id,
 * or any other private information.
 * The message id is generated completely randomly. example:
 * "Message-ID: <d3317aef786ca3e50250e92c547dccb97d524455@localhost>"
 * Default javax.mail example:
 * "Message-ID: <770569131.1.1671312904378.JavaMail.heiko@pavi>"
 */
class PrivateMimeMessage(session: Session): MimeMessage(session) {

    /**
     * binary 20 bytes
     * hex-encoded 40 bytes
     */
    private var binaryMessageId: ByteArray? = null

    fun getBinaryMessageId(): ByteArray? {
        return binaryMessageId
    }

    override fun updateMessageID() {
        binaryMessageId = ByteArray(20) { 0 }
        SecureRandom.getInstanceStrong().nextBytes(binaryMessageId)
        binaryMessageId?.let {
            setHeader("Message-ID", "<" + hex(it) + "@localhost>")
        }
    }

}