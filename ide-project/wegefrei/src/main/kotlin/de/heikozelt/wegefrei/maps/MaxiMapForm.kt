package de.heikozelt.wegefrei.maps

import de.heikozelt.wegefrei.model.SelectedPhotos
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import javax.swing.GroupLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.LayoutStyle

/**
 * große füllende Karte und darunter Buttons,
 * um den Marker zu setzen oder zu entfernen
 */
class MaxiMapForm(private val noticeFrame: NoticeFrame) : JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val maxiMap = MaxiMap(noticeFrame)
    //private val maxiMapButtonsBar = MaxiMapButtonsBar(noticeFrame, this)

    private val fitButton = JButton("Anpassen")
    private val addButton = JButton("Tatort-Marker setzen")
    private val removeButton = JButton("Tatort-Marker entfernen")

    init {
        //border = NO_BORDER

        // GUI components
        fitButton.addActionListener { fit() }
        fitButton.toolTipText = "Kartenausschnitt anpassen"
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

        val lay = GroupLayout(this)
        lay.autoCreateGaps = false
        lay.autoCreateContainerGaps = false
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(maxiMap)
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            GroupLayout.PREFERRED_SIZE,
                            Int.MAX_VALUE
                        )
                        .addComponent(fitButton)
                        .addComponent(addButton)
                        .addComponent(removeButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(maxiMap)
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(fitButton)
                        .addComponent(addButton)
                        .addComponent(removeButton)
                )
        )
        layout = lay

        //add(maxiMap, BorderLayout.CENTER)

        isVisible = true

        enableOrDisableOffenseMarkerButton()
    }

    // todo Prio 2: Bug: wenn kein Foto-Marker vorhanden ist, wird auch kein Offense-Marker gesetzt
    // Es kann aber passieren, dass Fotos keine Meta-Daten enthalten,
    // die Anwender_in aber trotzdem eine Anzeige machen möchte und die Address-Suche nutzen möchte.
    fun setOffenseMarker(offensePosition: GeoPosition?) {
        maxiMap.setOffensePosition(offensePosition)

        val addressMarkerVisible = offensePosition != null
        removeButton.isVisible = addressMarkerVisible
        addButton.isVisible = !addressMarkerVisible
    }

    fun setPhotoMarkers(selectedPhotos: SelectedPhotos) {
        maxiMap.replacedPhotoSelection(selectedPhotos.getPhotos())
    }

    fun enableOrDisableEditing() {
        maxiMap.enableOrDisableDragAndDrop()
        enableOrDisableOffenseMarkerButton()
    }

    fun fit() {
        maxiMap.fitToMarkers()
    }

    fun getMaxiMap(): MaxiMap {
        return maxiMap
    }

    private fun enableOrDisableOffenseMarkerButton() {
        val notice = noticeFrame.getNotice()
        val enab = (notice != null) && !notice.isSent()
        addButton.isEnabled = enab
        removeButton.isEnabled = enab
    }
}