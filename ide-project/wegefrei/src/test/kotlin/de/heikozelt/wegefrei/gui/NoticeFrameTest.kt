package de.heikozelt.wegefrei.gui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.jxmapviewer.viewer.GeoPosition

class NoticeFrameTest {

    /**
     * https://www.luftlinie.org/Wiesbaden/Frankfurt
     */
    @Test
    public fun distanceStraight_Wiesbaden_Frankfurt() {
        val wiesbaden = GeoPosition(50.084068, 8.238381)
        val frankfurt = GeoPosition(50.111511,8.680506)
        val distance = NoticeFrame.distanceStraight(wiesbaden, frankfurt)
        assertEquals(31719, distance.toInt()) // 39 Meter Abweichung ist ok bei knapp 32 km Entfernung
    }

    /**
     * https://www.luftlinie.org/Wiesbaden/Frankfurt
     */
    @Test
    public fun distanceStraight_Frankfurt_Wiesbaden() {
        val wiesbaden = GeoPosition(50.084068, 8.238381)
        val frankfurt = GeoPosition(50.111511,8.680506)
        val distance = NoticeFrame.distanceStraight(frankfurt, wiesbaden)
        assertEquals(31719, distance.toInt()) // 39 Meter Abweichung ist ok bei knapp 32 km Entfernung
    }
}