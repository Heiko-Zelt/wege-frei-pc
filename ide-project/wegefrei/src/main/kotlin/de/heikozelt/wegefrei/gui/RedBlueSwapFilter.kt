package de.heikozelt.wegefrei.gui

import java.awt.image.RGBImageFilter

class RedBlueSwapFilter: RGBImageFilter() {
    init {
        // The filter's operation does not depend on the
        // pixel's location, so IndexColorModels can be
        // filtered directly.
        canFilterIndexColorModel = true
    }

    override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
        return (rgb and -0xff0100
                or (rgb and 0xff0000 shr 16)
                or (rgb and 0xff shl 16))
    }
}