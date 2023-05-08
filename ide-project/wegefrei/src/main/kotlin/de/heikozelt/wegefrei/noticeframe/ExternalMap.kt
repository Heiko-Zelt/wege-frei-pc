package de.heikozelt.wegefrei.noticeframe

import org.jxmapviewer.viewer.GeoPosition
import java.net.URI

class ExternalMap(private var name: String, private var uriTemplate: String) {

    fun toURI(position: GeoPosition): URI {
        return URI(uriTemplate.replace("{latitude}", position.latitude.toString()).replace("{longitude}", position.longitude.toString()))
    }

    override fun toString(): String {
        return name
    }

    companion object {
        // todo Prio 3: logitude und latitude auf 6 Stellen nach dem Komma runden
        val EXTERNAL_MAPS = arrayOf(
            ExternalMap("Falschparker-Karte", "https://wege-frei.heikozelt.de/?x={longitude}&y={latitude}&z=19"),
            ExternalMap("Google Maps" ,"https://www.google.com/maps/search/?api=1&query={latitude},{longitude}"),
            // Bing parameters: cp: center point, sp: set point, lvl: zoom level, style: a = areal, r = road view
            ExternalMap("Bing Straßenkarte", "https://www.bing.com/maps?sp=point.{latitude}_{longitude}_Tatort&style=r"),
            ExternalMap("Bing Luftbild", "https://www.bing.com/maps?sp=point.{latitude}_{longitude}_Tatort&style=a"),
            ExternalMap("OpenStreetMap", "https://www.openstreetmap.org/?mlat={latitude}&mlon={longitude}#map=19/{latitude}/{longitude}")
        )
    }
}