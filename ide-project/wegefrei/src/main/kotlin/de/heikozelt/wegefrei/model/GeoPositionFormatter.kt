package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.db.entities.NoticeEntity
import org.jxmapviewer.viewer.GeoPosition
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.absoluteValue

class GeoPositionFormatter {
    companion object {

        private var usSymbols = DecimalFormatSymbols(Locale.US)
        private var latitudeFormat = DecimalFormat("00.00000", usSymbols)
        private var longitudeFormat = DecimalFormat("000.00000", usSymbols)

        fun format(latitude: Double?, longitude: Double?): String? {
            latitude?.let { lat ->
                longitude?.let { lon ->
                    val latSign = if(lat < 0) 'S' else 'N'
                    val lonSign = if(lon < 0) 'W' else 'E'
                    val latStr = latitudeFormat.format(lat.absoluteValue)
                    val lonStr = longitudeFormat.format(lon.absoluteValue)
                    return "$latStr° $latSign $lonStr° $lonSign WGS 84"
                }
            }
            return null
        }

        fun format(geoPosition: GeoPosition): String? {
            return format(geoPosition.latitude, geoPosition.longitude)
        }
    }
}