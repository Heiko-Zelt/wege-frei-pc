package de.heikozelt.wegefrei

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit


val dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy")
val timeFormat = DateTimeFormatter.ofPattern("H:m:s")
val timeFormatMinutes = DateTimeFormatter.ofPattern("H:m")


/**
 * Prüft nur, ob Tatdatum und Uhrzeit oder weder noch angeben sind.
 * Zweck: Beide Formular-Eingaben zusammen Speichern als ein Wert in der Datenbank.
 * Außerdem wird geprüft, ob die Eingabe-Formate stimmen.
 * @return Liefert eine Liste von Validierungs-Fehlern oder eine leere Liste
 */
fun validateStartDateTime(startDateStr: String?, startTimeStr: String?): List<String> {
    val errors = mutableListOf<String>()
    if (!(startDateStr.isNullOrBlank()) && startTimeStr.isNullOrBlank()) {
        errors.add("Tatdatum angegeben, aber keine Uhrzeit.")
    }
    if (!(startTimeStr.isNullOrBlank()) && startDateStr.isNullOrBlank()) {
        errors.add("Tatuhrzeit angegeben, aber kein Datum.")
    }
    if(!(startDateStr.isNullOrBlank())) {
        try {
            dateFormat.parse(startDateStr)
        } catch(ex: DateTimeParseException) {
            errors.add("Tatdatum hat nicht das Format 31.12.2023.")
        }
    }
    if(!(startTimeStr.isNullOrBlank())) {
        try {
            timeFormat.parse(startTimeStr)
        } catch(ex: DateTimeParseException) {
            errors.add("Tatzeit hat nicht das Format 23:59:59.")
        }
    }
    return errors
}

/**
 * Prüft nur, ob Enddatum und Uhrzeit oder weder noch angegeben sind.
 * Falls kein Datum angegeben ist, wird auch das Anfangsdatum als Enddatum akzeptiert.
 * Zweck: Beide Formular-Eingaben zusammen Speichern als ein Wert in der Datenbank.
 * Außerdem wird geprüft, ob die Eingabe-Formate stimmen.
 * @return Liefert eine Liste von Validierungs-Fehlern oder eine leere Liste.
 */
fun validateEndDateTime(startDateStr: String?, endDateStr: String?, endTimeStr: String?): List<String> {
    val errors = mutableListOf<String>()
    if ((!(startDateStr.isNullOrBlank()) || !(endDateStr.isNullOrBlank())) && endTimeStr.isNullOrBlank()) {
        errors.add("Tatdatum angegeben, aber keine Tatende-Uhrzeit.")
    }
    if (!(endTimeStr.isNullOrBlank()) && startDateStr.isNullOrBlank() && endDateStr.isNullOrBlank()) {
        errors.add("Tatende-Uhrzeit angegeben, aber kein Datum.")
    }
    if(!(endDateStr.isNullOrBlank())) {
        try {
            dateFormat.parse(endDateStr)
        } catch(ex: DateTimeParseException) {
            errors.add("Tatenddatum hat nicht das Format 31.12.2023.")
        }
    }
    if(!(endTimeStr.isNullOrBlank())) {
        try {
            timeFormat.parse(endTimeStr)
        } catch(ex: DateTimeParseException) {
            errors.add("Tatende-Uhrzeit hat nicht das Format 23:59:59.")
        }
    }
    return errors
}



fun parseDate(dateStr: String?): LocalDate? {
    val dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy")
    return try {
        LocalDate.parse(dateStr, dateFormat)
    } catch (ex: DateTimeParseException) {
        null
    }
}

fun parseTime(timeStr: String?): LocalTime? {
    val timeFormat = DateTimeFormatter.ofPattern("H:m:s")
    return try {
        LocalTime.parse(timeStr, timeFormat) // null als Parameter erlaubt?
    } catch (ex: DateTimeParseException) {
        null
    }
}

/**
 * merges 2 values for date and time into one ZonedDateTime value
 */
fun zonedDateTime(date: LocalDate?, time: LocalTime?): ZonedDateTime? {
    return if (date == null || time == null) {
        null
    } else {
        ZonedDateTime.of(date, time, ZoneId.systemDefault())
    }
}

/**
 * use startDate if endDate is not provided
 */
fun endZonedDateTime(startDate: LocalDate?, endDate: LocalDate?, endTime: LocalTime?): ZonedDateTime? {
    return if(endDate == null) {
        zonedDateTime(startDate, endTime)
    } else {
        zonedDateTime(endDate, endTime)
    }
}

/**
 * @return duration in minutes
 */
fun durationInMinutes(startDateStr: String?, startTimeStr: String?, endDateStr: String?, endTimeStr: String?): String? {
    val startDate = parseDate(startDateStr)
    val startTime = parseTime(startTimeStr)
    val endDate = parseDate(endDateStr)
    val endTime = parseTime(endTimeStr)
    val startZonedDateTime = zonedDateTime(startDate, startTime)
    val endZonedDateTime = endZonedDateTime(startDate, endDate, endTime)
    return durationInMinutesFormatted(startZonedDateTime, endZonedDateTime)
}

/**
 * Dauer in Minuten = Differenz Ende und Anfang des beobachteten Falschparkens.
 * Es wird immer abgerundet. Beispiel: 59 Sekunden = 0 Minuten
 * todo Prio 2: Stunden und Minuten
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

/**
 * @return liefert die Dauer formatiert oder null, wenn keine Dauer berechnet werden kann.
 */
fun durationInMinutesFormatted(start: ZonedDateTime?, end: ZonedDateTime?): String? {
    return if(start == null || end == null) {
        null
    } else {
        when (val d = durationInMinutes(start, end)) {
            0 -> "weniger als 1 Minute"
            1 -> "1 Minute"
            else -> "$d Minuten"
        }
    }
}

/**
 * liefert die Dauer im Format 25 h 59 min 59 sec
 */
fun durationFormatted(start: ZonedDateTime?, end: ZonedDateTime?): String? {
    return if(start == null || end == null) {
        null
    } else {
        val duration = Duration.between(start, end) // null?
        val days = duration.toDays()
        val hours = duration.toHoursPart()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()
        val parts = mutableListOf<String>()
        if(days != 0L) parts.add("$days d")
        if(hours != 0 || days != 0L) parts.add("$hours h")
        if(minutes != 0 || hours != 0 || days != 0L) parts.add("$minutes min")
        parts.add("$seconds sec")
        parts.joinToString(" ")
    }
}
