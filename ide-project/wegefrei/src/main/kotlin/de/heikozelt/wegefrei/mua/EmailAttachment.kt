package de.heikozelt.wegefrei.mua

import java.nio.file.Path
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.internet.MimeBodyPart

/**
 * like javax.mail.internet.MimeBodyPart but simplified
 */
class EmailAttachment(var path: Path) {

    fun asMimeBodyPart(): MimeBodyPart {
        val part = MimeBodyPart()
        val source = FileDataSource(path.toString())
        part.dataHandler = DataHandler(source)
        part.fileName = path.fileName.toString()
        return part
    }
}