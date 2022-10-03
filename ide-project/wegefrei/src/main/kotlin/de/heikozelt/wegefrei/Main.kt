package de.heikozelt.wegefrei

import com.drew.imaging.ImageMetadataReader
import com.drew.lang.GeoLocation
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.gui.MainFrame
import de.heikozelt.wegefrei.entities.Photo
import mu.KotlinLogging
import java.io.File
import java.util.*

val log = KotlinLogging.logger {}

val PHOTO_DIR = "/media/veracrypt1/_Fotos/2022/03"

val COLORS = arrayOf( "--", "blau", "braun", "gelb", "grau", "grün", "rot", "schwarz", "silber", "weiß")

val COUNTRY_SYMBOLS = arrayOf("--", "A - Österreich", "AL - Albanien", "AND - Andorra", "B - Belgien", "BG - Bulgarien",
    "BIH - Bosnien-Herzegowina", "BY - Belaruz", "CH - Schweiz", "CY - Zypern", "CZ - Tschechische Republik", "D - Deutschland")

val VEHICLE_MAKES = arrayOf("--", "Abarth", "Alfa Romeo", "Aston Martin", "Audi",
    "Bentley", "BMW", "Bugatti",
    "Cadillac", "Chevrolet", "Chrysler", "Citroën", "Crysler",
    "Dacia", "Daewoo", "Daihatsu", "Dodge", "DS", "Ducati",
    "Ferrari", "Fiat", "Ford",
    "Harley-Davidson", "Honda", "Hyundai",
    "Isuzu", "Jaguar", "Jeep", "Kawasaki", "Kia", "KTM",
    "Lada", "Lamborghini", "Lancia", "Land Rover", "Lexus", "Lotus",
    "Maserati", "Mazda", "Mercedes", "MG", "Mini", "Mitsubishi", "Nissan", "Opel",
    "Peugeot", "Piaggio", "Porsche", "Renault", "Rolls-Royce",
    "Saab", "Scania", "Seat", "Škoda", "Smart", "SsangYong", "Subaru", "Suzuki",
    "Tesla", "Toyota",
    "Vauxhall", "Volkswagen", "Volvo", "Yamaha")

val databaseService = DatabaseService()

fun main(args: Array<String>) {
    log.info("Wege frei!")
    log.debug("Program arguments: ${args.joinToString()}")

    val shutdownHook = Thread { log.info("exit") }
    Runtime.getRuntime().addShutdownHook(shutdownHook)

    val f = MainFrame()

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
    var longitude: Float? = null
    var latitude: Float? = null
    var date: Date? = null
    val metadata = ImageMetadataReader.readMetadata(file)
    log.debug("metadata: $metadata")

    val gpsDirs = metadata.getDirectoriesOfType(GpsDirectory::class.java)
    for(gpsDir in gpsDirs) {
        val geoLocation: GeoLocation? = gpsDir.geoLocation
        if(geoLocation != null && !geoLocation.isZero) {
            log.debug("longitude: ${geoLocation.longitude}, latitude: ${geoLocation.latitude}")
            longitude = geoLocation.longitude.toFloat()
            latitude = geoLocation.latitude.toFloat()
        }
    }
    val exifDirs = metadata.getDirectoriesOfType(ExifSubIFDDirectory::class.java)
    for(exifDir in exifDirs) {
        date = exifDir.getDateOriginal()
        if(date != null) {
            log.debug("date: ${date}")
        }
    }

    return Photo(file.name, longitude, latitude, date, null)
}


