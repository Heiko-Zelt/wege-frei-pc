package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory

class MaxiPhotoPanel(
    noticeFrame: NoticeFrame,
    photo: Photo
): BasePhotoViewer(noticeFrame, photo) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        actionButton.text = "hinzufügen"
        actionButton.addActionListener {
            log.debug("addActionListener(photo: pos.latitude=${photo.getGeoPosition()?.latitude})")
            noticeFrame.selectPhoto(photo)
        }
        photo.getPhotoEntity()?.let {p ->
            deleteButton.isVisible = p.noticeEntities.isNotEmpty()
        }
    }
}