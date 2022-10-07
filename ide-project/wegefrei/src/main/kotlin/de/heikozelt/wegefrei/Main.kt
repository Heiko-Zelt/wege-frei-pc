package de.heikozelt.wegefrei

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.gui.MainFrame
import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

val log = KotlinLogging.logger {}

val PHOTO_DIR = "/media/veracrypt1/_Fotos/2022/03"

val VEHICLE_MAKES = arrayOf("--", "Abarth", "Alfa Romeo", "Aston Martin", "Audi",
    "Bentley", "BMW", "Bugatti",
    "Cadillac", "Chevrolet", "Chrysler", "Citroën", "Crysler",
    "Dacia", "Daewoo", "Daihatsu", "Dodge", "DS", "Ducati",
    "Ferrari", "Fiat", "Ford",
    "Harley-Davidson", "Honda", "Hyundai",
    "Isuzu", "Jaguar", "Jeep", "Kawasaki", "Kia", "KTM",
    "Lada", "Lamborghini", "Lancia", "Land Rover", "Lexus", "Lotus",
    "Maserati", "Mazda", "Mercedes", "MG", "Mini", "Mitsubishi", "Nissan", "Opel",
    "Peugeot", "Piaggio", "Polestar", "Porsche", "Renault", "Rolls-Royce",
    "Saab", "Scania", "Seat", "Škoda", "Smart", "SsangYong", "Subaru", "Suzuki",
    "Tesla", "Toyota",
    "Vauxhall", "Volkswagen", "Volvo", "Yamaha")

val databaseService = DatabaseService()

fun main(args: Array<String>) {
    log.info("Wege frei!")
    log.debug("Program arguments: ${args.joinToString()}")

    val shutdownHook = Thread { log.info("exit") }
    Runtime.getRuntime().addShutdownHook(shutdownHook)

    val n = Notice()
    val f = MainFrame(n)

    log.debug("de.heikozelt.wegefrei.main function finished")
}

fun scanForNewPhotos() {
    log.info("scanning for new images...")

    val dir = File(PHOTO_DIR)
    if (!dir.isDirectory) {
        log.error(PHOTO_DIR + "ist kein Verzeichnis.")
        return
    }
    val filenames = dir.list(ImageFilenameFilter())
    for (filename in filenames) {
        log.debug(filename)
        if (databaseService.getPhotoByFilename(filename) == null) {
            log.debug("image in filesystem is new")

            val photo = readPhotoMetadata(File(PHOTO_DIR, filename))
            //    ProofPhoto(filename, null, null, null)
            if (photo != null) {
                databaseService.addPhoto(photo)
            }
        } else {
            log.debug("image in filesystem is already in database")
        }
    }
}

fun readPhotoMetadata(file: File): Photo? {
    var latitude: Float? = null
    var longitude: Float? = null
    var date: Date? = null
    val metadata = ImageMetadataReader.readMetadata(file)
    log.debug("metadata: $metadata")

    val gpsDirs = metadata.getDirectoriesOfType(GpsDirectory::class.java)
    for(gpsDir in gpsDirs) {
        val geoLocation: GeoLocation? = gpsDir.geoLocation
        if(geoLocation != null && !geoLocation.isZero) {
            log.debug("latitude: ${geoLocation.latitude}, longitude: ${geoLocation.longitude}")
            latitude = geoLocation.latitude.toFloat()
            longitude = geoLocation.longitude.toFloat()

        }
    }
    val exifDirs = metadata.getDirectoriesOfType(ExifSubIFDDirectory::class.java)
    for(exifDir in exifDirs) {
        date = exifDir.getDateOriginal()
        if(date != null) {
            log.debug("date: ${date}")
        }
    }
    val datTim = if(date == null) {
        null
    } else {
        ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        //Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
    return Photo(file.name, latitude, longitude, datTim, null)
}


