package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.NoticeEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class DurationInMinutesTest {

    @Test
    fun durationInMinutes_less_than_1_minute() {
        val startTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val endTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 59, 0, ZoneId.of("Europe/Berlin"))
        Assertions.assertEquals(0, durationInMinutes(startTime, endTime))
    }

    @Test
    fun durationInMinutes_exactly_1_minute() {
        val startTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val endTime = ZonedDateTime.of(2021, 12, 31, 13, 1, 0, 0, ZoneId.of("Europe/Berlin"))
        Assertions.assertEquals(1, durationInMinutes(startTime, endTime))
    }

    @Test
    fun durationInMinutesFormatted_less_than_1_minute() {
        val startTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val endTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 59, 0, ZoneId.of("Europe/Berlin"))
        Assertions.assertEquals("weniger als 1 Minute", durationInMinutesFormatted(startTime, endTime))
    }

    @Test
    fun durationInMinutesFormatted_exactly_1_minute() {
        val startTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        val endTime = ZonedDateTime.of(2021, 12, 31, 13, 1, 0, 0, ZoneId.of("Europe/Berlin"))
        Assertions.assertEquals("1 Minute", durationInMinutesFormatted(startTime, endTime))
    }
}