package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class NoticeTest {

    @Test
    fun constructor_with_no_arguments() {
        val n = Notice()
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
            assertNull(duration)
            assertFalse(environmentalStickerMissing)
            assertFalse(vehicleInspectionExpired)
            assertNull(vehicleInspectionYear)
            assertNull(vehicleInspectionMonth)
            assertFalse(vehicleAbandoned)
            assertEquals(0, photos.size)
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
        val photoTime2 = ZonedDateTime.of(2021, 12, 30, 19, 49, 59, 0, ZoneId.of("CET"))
        val expectedSentTime = ZonedDateTime.of(2021, 12, 31, 19, 49, 59, 0, ZoneId.of("CET"))

        val photos = hashSetOf(
            Photo("img1.jpeg", 50.111f, 8.111f, photoTime1),
            Photo("img2.jpeg", 50.222f, 8.222f, photoTime2)
        )

        val n = Notice(
            123,
            photoTime1,
            expectedSentTime,
            "D",
            "WI JA 233",
            "Honda",
            "rot",
            50.123f,
            8.123f,
            "Wilhelmstraße 13",
            "65193",
            "Wiesbaden",
            "gegenüber Bäcker",
            3,
            1,
            true,
            true,
            true,
            true,
            true,
            2010,
            12,
            false,
            "verwarngeldstelle@notice-j-unit-test.de",
            photos
        )
        n.apply {
            assertEquals(123, id)
            assertEquals("D", countrySymbol)
            assertEquals("WI JA 233", licensePlate)
            assertEquals("Honda",vehicleMake)
            assertEquals("rot", color)
            assertEquals(50.123f, latitude)
            assertEquals(8.123f, longitude)
            assertEquals("Wilhelmstraße 13", street)
            assertEquals("65193", zipCode)
            assertEquals("Wiesbaden", town)
            assertEquals(photoTime1, observationTime)
            assertEquals(3, duration)
            assertFalse(environmentalStickerMissing)
            assertTrue(vehicleInspectionExpired)
            assertEquals(2010, vehicleInspectionYear)
            assertEquals(12, vehicleInspectionMonth)
            assertTrue(vehicleAbandoned)
            assertEquals("verwarngeldstelle@notice-j-unit-test.de", recipient)
            assertEquals(2, photos.size)
        }
    }
}