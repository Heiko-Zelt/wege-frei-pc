package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory

// todo prio 2: fix bug: constructor is always called twice
class MaxiPhotoPanel(
    noticeFrame: NoticeFrame,
    photo: Photo
) : BasePhotoViewer(noticeFrame, photo) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        actionButton.text = "hinzufügen"
        actionButton.addActionListener {
            log.debug("addActionListener(photo: pos.latitude=${photo.getGeoPosition()?.latitude})")
            noticeFrame.selectPhoto(photo)
        }
        // Annahme, wenn es Meta-Daten zu dem Foto in der Datenbank gibt,
        // dann ist es auch in einer Meldung referenziert und umgekehrt.
        val visible = photo.getPhotoEntity() == null
        deleteButton.isVisible = visible
        if (visible) {
            deleteButton.addActionListener { noticeFrame.deletePhoto(photo) }
        }

    }
}