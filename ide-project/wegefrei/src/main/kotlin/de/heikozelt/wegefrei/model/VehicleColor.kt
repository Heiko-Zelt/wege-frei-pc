package de.heikozelt.wegefrei.model

import java.awt.Color

class VehicleColor(val colorName: String, val color: Color?) {

    companion object {
        /**
         * null -> COLORS[0]
         * "--" -> COLORS[0]
         * not found -> COLORS[0]
         */
        fun fromColorName(colorName: String?): VehicleColor {
            return if(colorName == null) {
                COLORS[0]
            } else {
                val c = COLORS.find { it.colorName == colorName }
                c ?: COLORS[0]
            }
        }

        val COLORS = arrayOf(
            VehicleColor("", null),
            VehicleColor("Weiß", Color.white),
            VehicleColor("Silber", Color(192, 192, 192)),
            VehicleColor("Grau", Color.gray),
            VehicleColor("Schwarz", Color.black),
            VehicleColor("Beige", Color(240, 240, 210)),
            VehicleColor("Gelb", Color.yellow),
            VehicleColor("Orange", Color.orange),
            VehicleColor("Gold", Color(218, 165, 32)),
            VehicleColor("Braun", Color(139, 69, 19)),
            VehicleColor("Rot", Color(240, 0, 0)),
            VehicleColor("Grün", Color(0, 200, 0)),
            VehicleColor("Blau", Color.blue),
            VehicleColor("Rosa/Pink", Color.pink),
            VehicleColor("Violett/Lila", Color(136, 0, 255))
        )
    }
}