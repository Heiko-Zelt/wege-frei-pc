package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Container
import java.awt.Image
import java.lang.Math.min
import javax.swing.ImageIcon
import javax.swing.JLabel
import kotlin.math.abs
import kotlin.math.pow

class MaxiPhotoLabel(private val parentContainer: Container, private val photo: Photo): JLabel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var zoomLevel: Short = 0
    private var fitFactor = 0.0
    private var zoomFactor = 0.0
    private var scaleFactor = 0.0
    private var oldScaleFactor = 0.0
    private var scaledImg: Image? = null

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

    fun calculateZoomFactor() {
        zoomFactor = ZOOM_BASE.pow(zoomLevel.toDouble())
    }

    fun calculateScaleFactor() {
        scaleFactor = fitFactor * zoomFactor
    }

    fun scaleIfNeeded() {
        log.debug("old scale factor: $oldScaleFactor")
        log.debug("new scale factor: $scaleFactor")
        if(abs(1 - (oldScaleFactor / scaleFactor)) > SCALE_TOLERANCE) {
            scale()
        }
    }

    private fun scale() {
        log.debug("scale to scale factor: $scaleFactor")
        photo.getPhotoFile()?.image?.let {
            val newWidth = (it.width * scaleFactor).toInt()
            val newHeight = (it.height * scaleFactor).toInt()
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
        oldScaleFactor = scaleFactor
    }

    fun zoomTo(level: Short) {
        zoomLevel = level
        log.debug("zoomTo(level=$level)")
        calculateFitFactor()
        calculateZoomFactor()
        calculateScaleFactor()
        scaleIfNeeded()
    }

    companion object {
        const val ZOOM_BASE = 1.3
        const val SCALE_TOLERANCE = 0.03
    }
}