package de.heikozelt.wegefrei.jobs

import com.beust.klaxon.Klaxon
import de.heikozelt.wegefrei.json.NominatimResponse
import de.heikozelt.wegefrei.noticeframe.NoticeForm
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
     * This is done in a background thread
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
        log.debug("text: $text")

        try {
            nominatimResponse = Klaxon().parse<NominatimResponse>(text)
        } catch(ex: Exception) {
            log.error("parsing exception", ex)
        }
        if(nominatimResponse == null) {
            log.error("parsing error")
        }
        if(nominatimResponse?.nominatimAddress == null) {
            log.error("parsing error - no address")
        }
        log.debug("displayName: ${nominatimResponse?.displayName}")
        log.debug("road: ${nominatimResponse?.nominatimAddress?.road}")
        log.debug("houseNumber: ${nominatimResponse?.nominatimAddress?.houseNumber}")
        return nominatimResponse
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        log.debug("done()")
        nominatimResponse?.nominatimAddress?.let { adr ->
            adr.getStreetAndHouseNumber()?.let {
                noticeForm.getNoticeFormFields().setStreet(it)
            }
            adr.postcode?.let {
                noticeForm.getNoticeFormFields().setZipCode(it)
            }
            adr.getCityOrTown()?.let {
              noticeForm.getNoticeFormFields().setTown(it)
            }
        }
    }

}