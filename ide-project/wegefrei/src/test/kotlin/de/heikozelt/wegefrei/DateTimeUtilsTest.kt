package de.heikozelt.wegefrei

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.ZonedDateTime

class DateTimeUtilsTest {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private fun traceErrors(errors: List<String>) {
        log.debug("Anzahl Validierungs-Fehler: ${errors.size}")
        errors.forEach {
            log.debug("Fehlertext: $it")
        }
    }

    @Test
    fun validateStartDateTime_with_minutes_precision() {
        val errors = validateStartDateTime("31.12.2023", "23:59")
        traceErrors(errors)
        assertEquals(1, errors.size)
        assertEquals("Tatzeit hat nicht das Format 23:59:59.", errors[0])
    }

    @Test
    fun validateStartDateTime_perfect_with_seconds_precision() {
        val errors = validateStartDateTime("31.12.2023", "23:59:59")
        traceErrors(errors)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun validateStartDateTime_blank_date() {
        val errors = validateStartDateTime("", "23:59:59")
        traceErrors(errors)
        assertEquals(1, errors.size)
        assertEquals("Tatuhrzeit angegeben, aber kein Datum.", errors[0])
    }

    @Test
    fun validateStartDateTime_blank_time() {
        val errors = validateStartDateTime("31.12.2023", "")
        traceErrors(errors)
        assertEquals(1, errors.size)
        assertEquals("Tatdatum angegeben, aber keine Uhrzeit.", errors[0])
    }

    @Test
    fun validateStartDateTime_nothing() {
        val errors = validateStartDateTime("", "")
        traceErrors(errors)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun validateStartDateTime_wrong_date_format() {
        val errors = validateStartDateTime("32.13.2023", "23:59:59")
        traceErrors(errors)
        assertEquals(1, errors.size)
        assertEquals("Tatdatum hat nicht das Format 31.12.2023.", errors[0])
    }

    @Test
    fun validateStartDateTime_wrong_time_format() {
        val errors = validateStartDateTime("31.12.2023", "24:61:61")
        traceErrors(errors)
        assertEquals(1, errors.size)
        assertEquals("Tatzeit hat nicht das Format 23:59:59.", errors[0])
    }

    @Test
    fun validateStartDateTime_wrong_date_and_wrong_time_format() {
        val errors = validateStartDateTime("32.13.2023", "24:61:61")
        traceErrors(errors)
        assertEquals(2, errors.size)
        assertEquals(1, errors.filter { "Tatdatum hat nicht das Format 31.12.2023." == it }.size)
        assertEquals(1, errors.filter { "Tatzeit hat nicht das Format 23:59:59." == it }.size)
    }

    @Test
    fun validateEndDateTime_perfect_same_day() {
        val errors = validateEndDateTime("31.12.2023", "","23:59:59")
        traceErrors(errors)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun validateEndDateTime_perfect_next_day() {
        val errors = validateEndDateTime("31.12.2023", "01.01.2024","23:59:59")
        traceErrors(errors)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun validateEndDateTime_no_start_date() {
        val errors = validateEndDateTime("", "01.01.2024","23:59:59")
        traceErrors(errors)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun validateEndDateTime_blank_date() {
        val errors = validateEndDateTime("", "","23:59:59")
        traceErrors(errors)
        assertEquals(1, errors.size)
        assertEquals("Tatende-Uhrzeit angegeben, aber kein Datum.", errors[0])
    }

    @Test
    fun validateEndDateTime_blank_time() {
        val errors = validateEndDateTime("", "01.01.2024","")
        traceErrors(errors)
        assertEquals(1, errors.size)
        assertEquals("Tatdatum angegeben, aber keine Tatende-Uhrzeit.", errors[0])
    }

    @Test
    fun durationInMinutes_less_than_1_minute() {
        val startTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val endTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals(0, durationInMinutes(startTime, endTime))
    }

    @Test
    fun durationInMinutes_exactly_1_minute() {
        val startTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val endTime = ZonedDateTime.of(2021, 12, 31, 13, 1, 0, 0, ZoneId.of("Europe/Berlin"))
        assertEquals(1, durationInMinutes(startTime, endTime))
    }

    @Test
    fun durationInMinutesFormatted_less_than_1_minute() {
        val startTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val endTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("weniger als 1 Minute", durationInMinutesFormatted(startTime, endTime))
    }

    @Test
    fun durationInMinutesFormatted_exactly_1_minute() {
        val startTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val endTime = ZonedDateTime.of(2021, 12, 31, 13, 1, 0, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("1 Minute", durationInMinutesFormatted(startTime, endTime))
    }

    @Test
    fun duration_2days() {
        val start = ZonedDateTime.of(2021, 12, 31, 13, 1, 1, 0, ZoneId.of("Europe/Berlin"))
        val end = ZonedDateTime.of(2022, 1, 2, 14, 59, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("2 d 1 h 58 min 58 sec", durationFormatted(start, end))
    }

    /**
     * macht keinen Sinn. Eine Validierung sollte verhindern, dass diese Dauer gemeldet wird.
     */
    @Test
    fun duration_negative() {
        val start = ZonedDateTime.of(2022, 1, 2, 14, 59, 59, 0, ZoneId.of("Europe/Berlin"))
        val end = ZonedDateTime.of(2021, 12, 31, 13, 1, 1, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("-2 d -1 h -58 min -58 sec", durationFormatted(start, end))
    }

    @Test
    fun duration_zero() {
        val start = ZonedDateTime.of(2022, 1, 2, 14, 59, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("0 sec", durationFormatted(start, start))
    }

    @Test
    fun duration_1_second() {
        val start = ZonedDateTime.of(2022, 1, 2, 14, 59, 59, 0, ZoneId.of("Europe/Berlin"))
        val end = ZonedDateTime.of(2022, 1, 2, 15, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("1 sec", durationFormatted(start, end))
    }

    @Test
    fun duration_exactly_1_hour() {
        val start = ZonedDateTime.of(2022, 1, 2, 14, 59, 59, 0, ZoneId.of("Europe/Berlin"))
        val end = ZonedDateTime.of(2022, 1, 2, 15, 59, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("1 h 0 min 0 sec", durationFormatted(start, end))
    }

    @Test
    fun duration_1_year_and_1_minute() {
        val start = ZonedDateTime.of(2022, 1, 2, 14, 58, 59, 0, ZoneId.of("Europe/Berlin"))
        val end = ZonedDateTime.of(2023, 1, 2, 14, 59, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("365 d 0 h 1 min 0 sec", durationFormatted(start, end))
    }
}