package de.heikozelt.wegefrei

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * Dauer in Minuten = Differenz Ende und Anfang des beobachteten Falschparkens.
 * Es wird immer abgerundet. Beispiel: 59 Sekunden = 0 Minuten
 */
fun durationInMinutes(start: ZonedDateTime?, end: ZonedDateTime?): Int {
    start?.let { s ->
        end?.let { e ->
            val unit = ChronoUnit.MINUTES
            return unit.between(s, e).toInt()
        }
    }
    return 0
}

fun durationInMinutesFormatted(start: ZonedDateTime?, end: ZonedDateTime?): String {
    return when(val d = durationInMinutes(start, end)) {
        0 -> "weniger als 1 Minute"
        1 -> "1 Minute"
        else -> "$d Minuten"
    }
}
