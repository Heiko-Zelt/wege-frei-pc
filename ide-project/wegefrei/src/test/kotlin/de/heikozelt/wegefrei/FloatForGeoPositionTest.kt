package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.noticeframe.NoticeFrame.Companion.distanceStraight
import org.junit.jupiter.api.Test
import org.jxmapviewer.viewer.GeoPosition



class GeoPositionPrecisionTest {
    /**
     * test precision of geo position in float at poles (or international date line in the pacific)
     * result: about 3 meters precision
     * according to openstreetmap 5 decimal places = 1,11 meter precision
     * https://sites.google.com/site/trescopter/Home/concepts/required-precision-for-gps-calculations
     */
    @Test
    fun using_float() {
        val longitude = 0.0f
        val maxLatitude = 180.0f
        var distanceDegree = 0.005f
        var previousPosition: GeoPosition? = null

        for (i in 0..100) {
            val latitude = maxLatitude - distanceDegree
            val position = GeoPosition(latitude.toDouble(), longitude.toDouble())
            val distanceMeters = distanceStraight(position, previousPosition)
            val latitudeText = "%.12f".format(latitude)
            val distanceDegreeText = "%.12f".format(distanceDegree)
            val distanceMetersText = "%.12f".format(distanceMeters)
            println("#$i: *itude: $latitudeText, distance: $distanceDegreeText degree, $distanceMetersText meters")
            previousPosition = position
            distanceDegree *= 0.94f
        }
    }

    /**
     * extreme precision, if using double
     */
    @Test
    fun using_double() {
        val longitude = 0.0
        val maxLatitude = 180.0
        var distanceDegree = 0.00005
        var previousPosition: GeoPosition? = null

        for (i in 0..100) {
            val latitude = maxLatitude - distanceDegree
            val position = GeoPosition(latitude, longitude)
            val distanceMeters = distanceStraight(position, previousPosition)
            val latitudeText = "%.12f".format(latitude)
            val distanceDegreeText = "%.12f".format(distanceDegree)
            val distanceMetersText = "%.12f".format(distanceMeters)
            println("#$i: *itude: $latitudeText, distance: $distanceDegreeText degree, $distanceMetersText meters")
            previousPosition = position
            distanceDegree *= 0.94
        }
    }

    /**
     * decimal positions
     * 1.113194444798 meters
     */
    @Test
    fun flensburg_5_decimal_positions() {
        val longitude = 9.0
        val flensburgLatitude1 = 55.00000
        val flensburgLatitude2 = 55.00001
        val position1 = GeoPosition(flensburgLatitude1, longitude)
        val position2 = GeoPosition(flensburgLatitude2, longitude)
        val distanceMeters = distanceStraight(position1, position2)
        val distanceMetersText = "%.12f".format(distanceMeters)
        println("$distanceMetersText meters")
    }
}