package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.PHOTO_MARKER_BACKGROUND
import org.jxmapviewer.viewer.GeoPosition


class PhotoMarker(index: Int, coord: GeoPosition?) : Marker(coord) {

    init {
        updateText(index)
        lbl.border = NORMAL_BORDER
        lbl.background = PHOTO_MARKER_BACKGROUND
        //lbl.horizontalTextPosition = JLabel.RIGHT // keine Auswirkung
        //lbl.verticalTextPosition = JLabel.CENTER
        lbl.isOpaque = true
        //label.addMouseListener(SwingWaypointMouseListener(text))
    }

    fun updateText(index: Int) {
        lbl.text = " ${index + 1} "
    }

    /*
    private class SwingWaypointMouseListener(val text: String) : MouseListener {
        override fun mouseClicked(e: MouseEvent?) {
            val comp = e?.component
            //if (comp != null && comp is JButton) {
            JOptionPane.showMessageDialog(comp, "You clicked on $text")
            //}
        }

        override fun mousePressed(e: MouseEvent?) {}
        override fun mouseReleased(e: MouseEvent?) {}
        override fun mouseEntered(e: MouseEvent?) {}
        override fun mouseExited(e: MouseEvent?) {}
    }
     */

    /*
    companion object {
        val icn = ImageIcon(ImageIO.read(DefaultWaypoint::class.java.getResource("/images/standard_waypoint.png")));
    }

     */
}