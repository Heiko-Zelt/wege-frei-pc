package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.Styles.Companion.FORM_BACKGROUND
import de.heikozelt.wegefrei.gui.Styles.Companion.NO_BORDER
import de.heikozelt.wegefrei.model.SelectedPhotos
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * große füllende Karte und darunter Buttons,
 * um den Marker zu setzen oder zu entfernen
 */
class MaxiMapForm(private val noticeFrame: NoticeFrame) : JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val maxiMap = MaxiMap(noticeFrame)
    private val maxiMapButtonsBar = MaxiMapButtonsBar(noticeFrame, this)

    init {
        layout = BorderLayout()
        background = FORM_BACKGROUND
        border = NO_BORDER

        add(maxiMap, BorderLayout.CENTER)
        add(maxiMapButtonsBar, BorderLayout.SOUTH)

        isVisible = true
    }

    fun setAddressMarker(addressPosition: GeoPosition?) {
        maxiMap.setAddressPosition(addressPosition)
        maxiMapButtonsBar.setAddressPosition(addressPosition)
    }

    fun setPhotoMarkers(selectedPhotos: SelectedPhotos) {
        maxiMap.replacedPhotoSelection(selectedPhotos.getPhotos())
    }

    fun fit() {
        maxiMap.fitToMarkers()
    }

    fun getMaxiMap(): MaxiMap {
        return maxiMap
    }
}