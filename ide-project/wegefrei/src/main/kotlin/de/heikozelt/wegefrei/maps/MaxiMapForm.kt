package de.heikozelt.wegefrei.maps

import de.heikozelt.wegefrei.model.Photo
import de.heikozelt.wegefrei.model.SelectedPhotosListModel
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.Insets
import java.util.*
import javax.swing.*

/**
 * große füllende Karte und darunter Buttons,
 * um den Marker zu setzen oder zu entfernen
 */
class MaxiMapForm(
    private val noticeFrame: NoticeFrame,
    selectedPhotosListModel: SelectedPhotosListModel
) : JPanel() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val maxiMap = MaxiMap(noticeFrame, selectedPhotosListModel)
    //private val maxiMapButtonsBar = MaxiMapButtonsBar(noticeFrame, this)

    private val zoomInButton = JButton("+")
    private val zoomOutButton = JButton("-")
    private val fitButton = JButton("Anpassen")
    private val addButton = JButton("Tatort-Marker setzen")
    private val removeButton = JButton("Tatort-Marker entfernen")

    init {
        zoomInButton.addActionListener { zoomIn() }
        zoomOutButton.addActionListener { zoomOut() }

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
                        .addComponent(zoomInButton)
                        .addComponent(zoomOutButton)
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
                        .addComponent(zoomInButton)
                        .addComponent(zoomOutButton)
                        .addComponent(fitButton)
                        .addComponent(addButton)
                        .addComponent(removeButton)
                )
        )
        val m = zoomInButton.margin
        zoomInButton.margin = Insets(m.top,10, m.bottom, 10)
        lay.linkSize(SwingConstants.HORIZONTAL, zoomInButton, zoomOutButton)
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

    fun setPhotoMarkers(selectedPhotos: TreeSet<Photo>) {
        maxiMap.replacedPhotoSelection(selectedPhotos)
    }

    fun enableOrDisableEditing() {
        maxiMap.enableOrDisableDragAndDrop()
        enableOrDisableOffenseMarkerButton()
    }

    /**
     * decrease zoom level, more details
     */
    private fun zoomIn() {
        val minZoomLevel = maxiMap.tileFactory.info.minimumZoomLevel
        val maxZoomLevel = maxiMap.tileFactory.info.maximumZoomLevel
        if(maxiMap.zoom == maxZoomLevel) {
            zoomOutButton.isEnabled = true
        }
        if(maxiMap.zoom > minZoomLevel) {
            maxiMap.zoom--
            if(maxiMap.zoom == minZoomLevel) {
                zoomInButton.isEnabled = false
            }
        }
    }

    /**
     * increase zoom level, lesser details
     */
    private fun zoomOut() {
        val minZoomLevel = maxiMap.tileFactory.info.minimumZoomLevel
        val maxZoomLevel = maxiMap.tileFactory.info.maximumZoomLevel
        if(maxiMap.zoom == minZoomLevel) {
            zoomInButton.isEnabled = true
        }
        if(maxiMap.zoom < maxZoomLevel) {
            maxiMap.zoom++
            if(maxiMap.zoom == maxZoomLevel) {
                zoomOutButton.isEnabled = false
            }
        }
    }

    fun fit() {
        val minZoomLevel = maxiMap.tileFactory.info.minimumZoomLevel
        maxiMap.fitToMarkers()
        zoomInButton.isEnabled = (maxiMap.zoom != minZoomLevel)
    }

    fun getMaxiMap(): MaxiMap {
        return maxiMap
    }

    private fun enableOrDisableOffenseMarkerButton() {
        val notice = noticeFrame.getNotice()
        val enab = !notice.isSent()
        addButton.isEnabled = enab
        removeButton.isEnabled = enab
    }

    companion object {
        const val MINIMUM_ZOOM_LEVEL = 0
        const val MAXIMUM_ZOOM_LEVEL = 19
    }
}