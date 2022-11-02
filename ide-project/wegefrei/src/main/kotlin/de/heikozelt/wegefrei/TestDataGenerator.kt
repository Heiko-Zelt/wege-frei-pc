package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.model.ListVehicleMakes.Companion.VEHICLE_MAKES
import de.heikozelt.wegefrei.model.VehicleColor.Companion.COLORS
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

class TestDataGenerator {
    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        @JvmStatic
        fun main(args: Array<String>) {
            LOG.info("TestDataGenerator.main()")
            val repo = DatabaseRepo.fromDirectory("~")
            for (i in 0 until 500) {
                val notice = Notice()
                notice.apply {
                    observationTime = ZonedDateTime.now()
                    licensePlate = "AA XX 00$i"
                    vehicleMake = VEHICLE_MAKES[i % VEHICLE_MAKES.size]
                    color = COLORS[i % COLORS.size].colorName
                    latitude = 49 + i.toFloat() / 11
                    longitude = 8 + i.toFloat() / 13
                }
                repo.insertNotice(notice)
            }
        }
    }
}