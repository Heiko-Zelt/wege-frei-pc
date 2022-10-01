import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.GpsDirectory
import de.heikozelt.wegefrei.DatabaseService
import de.heikozelt.wegefrei.ImageFilenameFilter
import de.heikozelt.wegefrei.MainFrame
import de.heikozelt.wegefrei.entities.ProofPhoto
import mu.KotlinLogging
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import java.io.File
import javax.imageio.ImageIO


val log = KotlinLogging.logger {}

val PATH = "/media/veracrypt1/_Fotos/2022/03"

val databaseService = DatabaseService()

fun main(args: Array<String>) {
    log.info("Wege frei!")
    log.debug("Program arguments: ${args.joinToString()}")

    val shutdownHook = Thread { log.info("exit") }
    Runtime.getRuntime().addShutdownHook(shutdownHook)

    val f = MainFrame()

    log.debug("main function finished")
}

fun scanForNewImages() {
    log.info("scanning for new images...")

    val dir = File(PATH)
    if (!dir.isDirectory) {
        log.error(PATH + "ist kein Verzeichnis.")
        return
    }
    val filenames = dir.list(ImageFilenameFilter())
    for (filename in filenames) {
        log.debug(filename)
        if (databaseService.getImageByFilename(filename) == null) {
            log.debug("image in filesystem is new")

            val photo = readPhoto(File(PATH, filename))
            //    ProofPhoto(filename, null, null, null)
            if (photo != null) {
                databaseService.addProofPhoto(photo)
            }
        } else {
            log.debug("image in filesystem is already in database")
        }
    }
}

fun readPhoto(file: File): ProofPhoto? {
    var longitude: Float? = null
    var latitude: Float? = null
    val metadata = ImageMetadataReader.readMetadata(file)
    log.debug("metadata: $metadata")
    val gpsDirs = metadata.getDirectoriesOfType(GpsDirectory::class.java)
    for(gpsDir in gpsDirs) {
        val geoLocation = gpsDir.geoLocation
        if(geoLocation != null && !geoLocation.isZero) {
            log.debug("longitude: ${geoLocation.longitude}, latitude: ${geoLocation.latitude}")
            longitude = geoLocation.longitude.toFloat()
            latitude = geoLocation.latitude.toFloat()
        }
    }
    // todo dateTime
    return ProofPhoto(file.name, longitude, latitude, null)
}


