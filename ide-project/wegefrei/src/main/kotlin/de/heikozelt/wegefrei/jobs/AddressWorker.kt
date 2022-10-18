package de.heikozelt.wegefrei.jobs

import com.beust.klaxon.Klaxon
import de.heikozelt.wegefrei.gui.NoticeForm
import de.heikozelt.wegefrei.json.NominatimResponse
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.SwingWorker

/**
 * Findet zu einer GeoPosition die Adresse,
 * und aktualisiert die Eingabe-Felder im Formular.
 * Es wird ein Web Service / eine API aufgerufen.
 * Es kann also lange dauern, bis das Ergebnis eintrifft.
 * Deswegen in einem eigenen Hintergrund-Thread.
 */
class AddressWorker(
    private val position: GeoPosition,
    private val noticeForm: NoticeForm
)
: SwingWorker<NominatimResponse?, NominatimResponse?>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var nominatimResponse: NominatimResponse? = null

    /**
     * This is done in own Thread
     */
    override fun doInBackground(): NominatimResponse? {
        log.info("doInBackground()")
        val lat = "%.8f".format(position.latitude)
        val lon = "%.8f".format(position.longitude)
        val url =
            URL("https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$lat&lon=$lon&email=hz@heikozelt.de")
        log.debug("url: $url")
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        println(connection.responseCode)
        println(connection.getHeaderField("Content-Type"))
        val text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
        log.debug(text)

        nominatimResponse = Klaxon().parse<NominatimResponse>(text)
        log.debug("displayName: ${nominatimResponse?.displayName}")
        log.debug("road: ${nominatimResponse?.address?.road}")
        log.debug("houseNumber: ${nominatimResponse?.address?.houseNumber}")
        return nominatimResponse
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        log.debug("done()")
        nominatimResponse?.address?.let {
            log.debug("adr")
            if (it.road != null) {
                log.debug("road")
                var street = it.road
                if (it.houseNumber != null) {
                    log.debug("house number")
                    street += " " + it.houseNumber
                }
                log.debug("street")
                noticeForm.getNoticeFormFields().setStreet(street)
            }
            if (it.postcode != null) {
                log.debug("postcode")
                noticeForm.getNoticeFormFields().setZipCode(it.postcode)
            }
            if (it.city != null) {
                noticeForm.getNoticeFormFields().setTown(it.city)
            }
        }
    }

}