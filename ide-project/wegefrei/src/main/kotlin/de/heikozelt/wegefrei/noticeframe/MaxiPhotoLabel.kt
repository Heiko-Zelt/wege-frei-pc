package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Container
import java.awt.Image
import java.lang.Math.min
import javax.swing.ImageIcon
import javax.swing.JLabel
import kotlin.math.pow

class MaxiPhotoLabel(private val parentContainer: Container, private val photo: Photo): JLabel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var fitFactor = 0.0
    private var scaledImg: Image? = null
    private var zoomLevel: Short = 0

    init {
        toolTipText = photo.getToolTipText()
        //alignmentX = CENTER_ALIGNMENT
        horizontalAlignment = CENTER
        background = Color.pink
    }

    fun calculateFitFactor() {
        photo.getPhotoFile()?.image?.let {
            val horizontalFitFactor = parentContainer.width.toDouble() / it.width
            val verticalFitFactor = parentContainer.height.toDouble() / it.height
            fitFactor = min(horizontalFitFactor, verticalFitFactor)
        }
    }

    fun scale() {
        photo.getPhotoFile()?.image?.let {
            val newWidth = (it.width * fitFactor * BASE.pow(zoomLevel.toDouble())).toInt()
            val newHeight = (it.height * fitFactor * BASE.pow(zoomLevel.toDouble())).toInt()
            if(newWidth != 0 && newHeight != 0) {
                scaledImg = photo.getPhotoFile()?.image?.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)
            }
        }
        if (scaledImg == null) {
            text ="not loaded"
            icon = null
        } else {
            text = null
            icon = ImageIcon(scaledImg)
        }
    }

    fun zoomTo(level: Short) {
        zoomLevel = level
        log.debug("zoomTo(level=$level)")
        calculateFitFactor()
        scale()
    }

    companion object {
        const val BASE = 1.2
    }
}