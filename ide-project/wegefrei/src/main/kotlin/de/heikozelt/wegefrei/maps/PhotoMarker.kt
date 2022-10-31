package de.heikozelt.wegefrei.maps

import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.PHOTO_MARKER_BACKGROUND
import org.jxmapviewer.viewer.GeoPosition


class PhotoMarker(private var photoIndex: Int, coord: GeoPosition?) : Marker(coord) {

    init {
        updateText()
        lbl.border = NORMAL_BORDER
        lbl.background = PHOTO_MARKER_BACKGROUND
        lbl.isOpaque = true
    }

    fun incrementPhotoIndex() {
        photoIndex++
        updateText()
    }

    fun decrementPhotoIndex() {
        photoIndex--
        updateText()
    }

    fun updateText() {
        lbl.text = " ${photoIndex + 1} "
    }
}