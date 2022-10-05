package de.heikozelt.wegefrei.gui

import mu.KotlinLogging
import org.jxmapviewer.viewer.DefaultWaypoint
import org.jxmapviewer.viewer.GeoPosition
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.image.FilteredImageSource
import javax.imageio.ImageIO
import javax.swing.GrayFilter
import javax.swing.ImageIcon
import javax.swing.JLabel


class AddressMarker(coord: GeoPosition?) : Marker(coord) {

    init {
        lbl.icon = icn
        lbl.setSize(20, 34)
        lbl.preferredSize = Dimension(20, 34)
    }

    companion object {
        val icn: ImageIcon;

        private val log = KotlinLogging.logger {}

        init {
            val img = ImageIO.read(DefaultWaypoint::class.java.getResource("/images/standard_waypoint.png"))
            log.debug("img height: ${img.height}, width: ${img.width}")
            val filter = RedBlueSwapFilter()
            val producer = FilteredImageSource(img.source, filter)
            val grayImg = Toolkit.getDefaultToolkit().createImage(producer)
            icn = ImageIcon(grayImg)
        }
    }
}