package de.heikozelt.wegefrei.noticeframe

import org.jxmapviewer.viewer.GeoPosition
import java.net.URI

class ExternalMap(private var name: String, private var uriTemplate: String) {

    fun toURI(position: GeoPosition): URI {
        return URI(uriTemplate.replace("latitude", position.latitude.toString()).replace("longitude", position.longitude.toString()))
    }

    override fun toString(): String {
        return name
    }

    companion object {
        val EXTERNAL_MAPS = arrayOf(
            ExternalMap("Falschparker-Karte", "https://wege-frei.heikozelt.de/?x=longitude&y=latitude&z=19"),
            ExternalMap("Google Maps", "https://www.google.de/maps/@latitude,longitude,19z"),
            ExternalMap("Bing Karten", "https://www.bing.com/maps?cp=latitude%7Elongitude&lvl=19"),
            ExternalMap("OpenStreetMap", "https://www.openstreetmap.org/#map=19/latitude/longitude")
        )
    }
}