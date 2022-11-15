package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.JLabel

class MaxiPhotoLabel(photo: Photo): JLabel() {

    init {
        val scaledImg = photo.getPhotoFile()?.image?.getScaledInstance(600, 400, Image.SCALE_SMOOTH)
        if (scaledImg == null) {
            text ="not loaded"
        } else {
            icon = ImageIcon(scaledImg)
        }
        toolTipText = photo.getToolTipText()
        alignmentX = CENTER_ALIGNMENT
    }
}