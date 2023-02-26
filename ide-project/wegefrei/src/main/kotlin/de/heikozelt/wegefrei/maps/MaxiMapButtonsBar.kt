package de.heikozelt.wegefrei.maps

import de.heikozelt.wegefrei.gui.Styles
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import org.jxmapviewer.viewer.GeoPosition
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JPanel

/**
 * Button-Leiste unterhalb der großen Karte
 */
class MaxiMapButtonsBar(private val noticeFrame: NoticeFrame, private val maxiMapForm: MaxiMapForm): JPanel() {
    private val fitButton = JButton("Anpassen")
    private val addButton = JButton("Tatort-Marker setzen")
    private val removeButton = JButton("Tatort-Marker entfernen")

    init {
        layout = FlowLayout(FlowLayout.RIGHT, 5,0)

        fitButton.margin = Styles.BUTTON_MARGIN
        fitButton.addActionListener { maxiMapForm.fit() }
        fitButton.toolTipText = "Kartenausschnitt anpassen"
        add(fitButton)

        val fitStruts = Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE)
        add(fitStruts);

        addButton.addActionListener {
            noticeFrame.updateOffensePositionFromSelectedPhotos()
            addButton.isVisible = false
            removeButton.isVisible = true
        }
        addButton.isVisible = false
        add(addButton)

        removeButton.addActionListener {
            noticeFrame.deleteOffensePosition()
            removeButton.isVisible = false
            addButton.isVisible = true
        }
        removeButton.isVisible = false
        add(removeButton)

        val buttonStruts = Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE)
        add(buttonStruts);

        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));
        enableOrDisableOffenseMarkerButton() // is disabled
    }

    fun enableOrDisableOffenseMarkerButton() {
        val notice = noticeFrame.getNotice()
        notice?.let { ne ->
            val enab = !ne.isFinalized()
            addButton.isEnabled = enab
            removeButton.isEnabled = enab
        }
    }

    fun setAddressPosition(addressLocation: GeoPosition?) {
        val addressMarkerVisible = addressLocation != null
        removeButton.isVisible = addressMarkerVisible
        addButton.isVisible = !addressMarkerVisible
    }
}