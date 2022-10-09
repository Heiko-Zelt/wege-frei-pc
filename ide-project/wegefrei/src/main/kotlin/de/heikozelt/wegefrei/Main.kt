package de.heikozelt.wegefrei

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.gui.MainFrame
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.gui.NoticesFrame
import mu.KotlinLogging
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

val log = KotlinLogging.logger {}


fun main(args: Array<String>) {
    log.info("Wege frei!")
    log.debug("Program arguments: ${args.joinToString()}")

    val shutdownHook = Thread { log.info("exit") }
    Runtime.getRuntime().addShutdownHook(shutdownHook)

    App()

    log.debug("de.heikozelt.wegefrei.main function finished")
}



