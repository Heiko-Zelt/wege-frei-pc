package de.heikozelt.wegefrei.gui

import org.jxmapviewer.viewer.DefaultWaypoint
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.image.FilteredImageSource
import javax.imageio.ImageIO
import javax.swing.ImageIcon


class OffenseMarker(coord: GeoPosition?) : Marker(coord) {

    init {
        lbl.icon = icn
        lbl.setSize(20, 34)
        lbl.preferredSize = Dimension(20, 34)
    }

    companion object {
        val icn: ImageIcon

        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        init {
            val img = ImageIO.read(DefaultWaypoint::class.java.getResource("/images/standard_waypoint.png"))
            LOG.debug("img height: ${img.height}, width: ${img.width}")
            val filter = RedBlueSwapFilter()
            val producer = FilteredImageSource(img.source, filter)
            val grayImg = Toolkit.getDefaultToolkit().createImage(producer)
            icn = ImageIcon(grayImg)
        }
    }
}