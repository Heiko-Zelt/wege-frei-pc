package de.heikozelt.wegefrei.gui

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
    private val addButton = JButton("Adress-Marker setzen")
    private val removeButton = JButton("Adress-Marker entfernen")

    init {
        layout = FlowLayout(FlowLayout.RIGHT, 5,0)

        fitButton.margin = Styles.BUTTON_MARGIN
        fitButton.addActionListener { maxiMapForm.fit() }
        fitButton.toolTipText = "Kartenausschnitt anpassen"
        add(fitButton)

        val fitStruts = Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE)
        add(fitStruts);

        addButton.margin = Styles.BUTTON_MARGIN
        addButton.addActionListener {/* todo: implemntieren */ }
        addButton.isVisible = false
        add(addButton)

        removeButton.margin = Styles.BUTTON_MARGIN
        removeButton.addActionListener { /* todo: implementieren */ }
        removeButton.isVisible = false
        add(removeButton)

        val buttonStruts = Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE)
        add(buttonStruts);

        add(Box.createHorizontalStrut(Styles.BUTTONS_DISTANCE));
    }

    fun setAddressPosition(addressLocation: GeoPosition?) {
        val addressMarkerVisible = addressLocation != null
        removeButton.isVisible = addressMarkerVisible
        addButton.isVisible = !addressMarkerVisible
    }
}