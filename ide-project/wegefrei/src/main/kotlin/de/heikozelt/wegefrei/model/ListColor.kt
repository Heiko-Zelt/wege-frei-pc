package de.heikozelt.wegefrei.model

import java.awt.Color

class ListColor(val colorName: String, val color: Color?) {

    companion object {
        /**
         * null -> COLORS[0]
         * "--" -> COLORS[0]
         * not found -> COLORS[0]
         */
        fun fromColorName(colorName: String?): ListColor {
            return if(colorName == null) {
                COLORS[0]
            } else {
                val c = COLORS.find { it.colorName == colorName }
                c ?: COLORS[0]
            }
        }

        val COLORS = arrayOf(
            ListColor("--", null),
            ListColor("Weiß", Color.white),
            ListColor("Silber", Color(192, 192, 192)),
            ListColor("Grau", Color.gray),
            ListColor("Schwarz", Color.black),
            ListColor("Beige", Color(240, 240, 210)),
            ListColor("Gelb", Color.yellow),
            ListColor("Orange", Color.orange),
            ListColor("Gold", Color(218, 165, 32)),
            ListColor("Braun", Color(139, 69, 19)),
            ListColor("Rot", Color(240, 0, 0)),
            ListColor("Grün", Color(0, 200, 0)),
            ListColor("Blau", Color.blue),
            ListColor("Rosa/Pink", Color.pink),
            ListColor("Violett/Lila", Color(136, 0, 255))
        )
    }
}