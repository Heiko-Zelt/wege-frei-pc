package de.heikozelt.wegefrei.entities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class NoticeEntityTest {
    @Test
    fun constructor_with_no_arguments() {
        val n = NoticeEntity()
        n.apply {
            assertNull(id)
            assertNull(countrySymbol)
            assertNull(licensePlate)
            assertNull(vehicleMake)
            assertNull(color)
            assertNull(latitude)
            assertNull(longitude)
            assertNull(street)
            assertNull(zipCode)
            assertNull(town)
            assertNull(observationTime)
            assertNull(endTime)
            assertEquals(0,getDuration())
            assertFalse(environmentalStickerMissing)
            assertFalse(vehicleInspectionExpired)
            assertNull(vehicleInspectionYear)
            assertNull(vehicleInspectionMonth)
            assertFalse(vehicleAbandoned)
            assertEquals(0, photoEntities.size)
            assertEquals("", getCreatedTimeFormatted())
        }
    }

    @Test
    fun constructor_with_all_arguments() {
        /*
        val cal = Calendar.getInstance()
        cal.set(2021, 12, 31, 19, 49, 59)
        val dat = cal.time
         */
        val photoTime1 = ZonedDateTime.of(2021, 12, 30, 19, 49, 50, 0, ZoneId.of("CET"))
        val photoTime2 = ZonedDateTime.of(2021, 12, 30, 19, 52, 59, 0, ZoneId.of("CET"))
        val expectedSentTime = ZonedDateTime.of(2021, 12, 31, 19, 49, 59, 0, ZoneId.of("CET"))

        val hash1 = "0123456789abcdefghij".toByteArray()
        val hash2 = "abcdefghij0123456789".toByteArray()
        val photoEntities = hashSetOf(
            PhotoEntity("img1.jpeg", hash1,50.111, 8.111, photoTime1),
            PhotoEntity("img2.jpeg", hash2,50.222, 8.222, photoTime2)
        )

        val n = NoticeEntity(
            123,
            photoTime1,
            photoTime2,
            expectedSentTime,
            "D",
            "WI JA 233",
            "Honda",
            "rot",
            "Pkw",
            50.123,
            8.123,
            "Wilhelmstraße 13",
            "65193",
            "Wiesbaden",
            "Altstadt-Nord",
            "gegenüber Bäcker",
            "Öl läuft aus",
            true,
            true,
            true,
            true,
            true,
            2010,
            12,
            false,
            'F',
            "verwarngeldstelle@notice-j-unit-test.de",
            "Verwarngeldstelle J-Unit-Test",
            photoEntities
        )
        n.apply {
            assertEquals(123, id)
            assertEquals("D", countrySymbol)
            assertEquals("WI JA 233", licensePlate)
            assertEquals("Honda",vehicleMake)
            assertEquals("rot", color)
            assertEquals("Pkw", vehicleType)
            assertEquals(50.123, latitude)
            assertEquals(8.123, longitude)
            assertEquals("Wilhelmstraße 13", street)
            assertEquals("65193", zipCode)
            assertEquals("Wiesbaden", town)
            assertEquals("Altstadt-Nord", quarter)
            assertEquals(photoTime1, observationTime)
            assertEquals(photoTime2, endTime)
            assertEquals(3, getDuration())
            assertFalse(environmentalStickerMissing)
            assertTrue(vehicleInspectionExpired)
            assertEquals(2010, vehicleInspectionYear)
            assertEquals(12, vehicleInspectionMonth)
            assertTrue(vehicleAbandoned)
            assertEquals('F', deliveryType)
            assertEquals("verwarngeldstelle@notice-j-unit-test.de", recipientEmailAddress)
            assertEquals(2, photoEntities.size)
        }
    }

    @Test
    fun getAddress_null() {
        val n = NoticeEntity()
        assertNull(n.getAddress())
    }

    @Test
    fun getAddress_perfect() {
       val n = NoticeEntity()
       n.street = "Bahnhofstraße 1"
       n.zipCode = "12345"
       n.town = "Musterstadt"
       assertEquals("Bahnhofstraße 1, 12345 Musterstadt", n.getAddress())
    }

    @Test
    fun getAddress_only_street() {
        val n = NoticeEntity()
        n.street = "Bahnhofstraße 1"
        assertEquals("Bahnhofstraße 1", n.getAddress())
    }

    @Test
    fun getAddress_only_zipCode() {
        val n = NoticeEntity()
        n.zipCode = "12345"
        assertEquals("12345", n.getAddress())
    }

    @Test
    fun getAddress_only_town() {
        val n = NoticeEntity()
        n.town = "Musterstadt"
        assertEquals("Musterstadt", n.getAddress())
    }

    @Test
    fun getAddress_street_and_zipCode() {
        val n = NoticeEntity()
        n.zipCode = "12345"
        n.town = "Musterstadt"
        assertEquals("12345 Musterstadt", n.getAddress())
    }

    @Test
    fun getAddress_street_and_town() {
        val n = NoticeEntity()
        n.street = "Bahnhofstraße 1"
        n.town = "Musterstadt"
        assertEquals("Bahnhofstraße 1, Musterstadt", n.getAddress())
    }

    @Test
    fun getAddress_town_and_zipCode() {
        val n = NoticeEntity()
        n.zipCode = "12345"
        n.town = "Musterstadt"
        assertEquals("12345 Musterstadt", n.getAddress())
    }

    @Test
    fun getObservationTimeFormatted() {
        val n = NoticeEntity()
        n.observationTime = ZonedDateTime.of(2021, 12, 31, 12, 58, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("31.12.2021, 12:58 MEZ", n.getObservationTimeFormatted())
    }

    /**
     * üblich Koordinate in Deutschland
     */
    @Test
    fun getGeoPositionFormatted_north_east() {
        val n = NoticeEntity()
        n.latitude = 50.12341234
        n.longitude = 8.12341234
        assertEquals("50.12341° N 008.12341° E WGS 84", n.getGeoPositionFormatted())
    }

    /**
     * Südpazifik
     */
    @Test
    fun getGeoPositionFormatted_south_west() {
        val n = NoticeEntity()
        n.latitude = -13.12341234
        n.longitude = -179.12341234
        assertEquals("13.12341° S 179.12341° W WGS 84", n.getGeoPositionFormatted())
    }

    @Test
    fun getDuration_less_than_1_minute() {
        val n = NoticeEntity()
        n.observationTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        n.endTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals(0, n.getDuration())
    }

    @Test
    fun getDuration_exactly_1_minute() {
        val n = NoticeEntity()
        n.observationTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        n.endTime = ZonedDateTime.of(2021, 12, 31, 13, 1, 0, 0, ZoneId.of("Europe/Berlin"))
        assertEquals(1, n.getDuration())
    }

    @Test
    fun getDurationFormatted_less_than_1_minute() {
        val n = NoticeEntity()
        n.observationTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        n.endTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("weniger als 1 Minute", n.getDurationFormatted())
    }

    @Test
    fun getDurationFormatted_exactly_1_minute() {
        val n = NoticeEntity()
        n.observationTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        n.endTime = ZonedDateTime.of(2021, 12, 31, 13, 1, 0, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("1 Minute", n.getDurationFormatted())
    }

    @Test
    fun getDurationFormatted_exactly_2_minutes() {
        val n = NoticeEntity()
        n.observationTime = ZonedDateTime.of(2021, 12, 31, 13, 0, 59, 0, ZoneId.of("Europe/Berlin"))
        n.endTime = ZonedDateTime.of(2021, 12, 31, 13, 2, 59, 0, ZoneId.of("Europe/Berlin"))
        assertEquals("2 Minuten", n.getDurationFormatted())
    }

}