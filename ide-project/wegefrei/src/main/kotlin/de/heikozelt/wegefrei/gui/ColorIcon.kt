package de.heikozelt.wegefrei.gui

import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon


class ColorIcon(val color: Color?): Icon {

    override fun getIconHeight(): Int {
        return 20
    }

    override fun getIconWidth(): Int {
        return 20
    }

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        color?.let {
            g.color = color
            g.fillRect(0, 0, iconWidth, iconHeight)
        }
    }
}