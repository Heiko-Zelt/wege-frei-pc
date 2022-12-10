package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory

/**
 * zeigt ein ausgewähltes Foto an
 */
class MaxiSelectedPhotoPanel(
    noticeFrame: NoticeFrame,
    photo: Photo
): BasePhotoViewer(noticeFrame, photo)
{
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        actionButton.text = "entfernen"
        actionButton.addActionListener {
            noticeFrame.unselectPhoto(photo)
        }
        deleteButton.isVisible = false
    }
}