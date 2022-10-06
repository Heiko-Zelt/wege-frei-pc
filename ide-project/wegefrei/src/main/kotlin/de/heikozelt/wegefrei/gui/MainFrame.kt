package de.heikozelt.wegefrei.gui

import com.beust.klaxon.Klaxon
import de.heikozelt.wegefrei.databaseService
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import de.heikozelt.wegefrei.json.NominatimResponse
import de.heikozelt.wegefrei.model.SelectedPhotos
import mu.KotlinLogging
import org.jxmapviewer.viewer.GeoPosition
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.*

/**
 * Haupt-Fenster zum Bearbeiten einer Meldung
 * (mit Dispatcher-Funktionen)
 * Instanziierung
 * <ol>
 *   <li>ohne Parameter zum Bearbeiten einer neuen Meldung. notice.id ist null.</li>
 *   <li>Instanziierung mit Notice als Parameter zum Bearbeiten einer bestehenden Meldung. notice.id enthält eine Zahl.</li>
 * </ol>
 */
class MainFrame(private val notice: Notice = Notice()) : JFrame() {

    private val log = KotlinLogging.logger {}

    private val selectedPhotos = SelectedPhotos()

    private var mainToolBar = MainToolBar()
    private var allPhotosPanel = AllPhotosPanel(this, "20220301_184952.jpg", selectedPhotos)
    private var selectedPhotosPanel = SelectedPhotosPanel(this, selectedPhotos)
    private var noticeForm = NoticeForm(this, selectedPhotos)
    private var zoomPanel: ZoomPanel

    // todo sinnvoll initialisieren anhand der vorhandenen Fotos
    /*
    private val frankfurt = GeoPosition(50.11, 8.68)
    private val wiesbaden = GeoPosition(50, 5, 0, 8, 14, 0)
    private val mainz = GeoPosition(50, 0, 0, 8, 16, 0)
    private val posi = GeoPosition(50.1, 8.5)

    private val photoMarkers = LinkedList<PhotoMarker>()
    private val addressMarker: AddressMarker = AddressMarker(posi)
     */

    init {
        log.debug("notice id: ${notice.id}")
        if(notice.id == null) {
            title = "Neue Meldung - Wege frei!"
        } else {
            title = "Meldung #${notice.id} - Wege frei!"
        }

        selectedPhotos.registerObserver(selectedPhotosPanel)
        selectedPhotos.registerObserver(allPhotosPanel)

        /*
        photoMarkers.add(PhotoMarker(0, frankfurt))
        photoMarkers.add(PhotoMarker(1, wiesbaden))
        photoMarkers.add(PhotoMarker(2, mainz))

         */

        background = Color.green

        val selectedPhoto1 = databaseService.getPhotoByFilename("20220301_184952.jpg")
        val selectedPhoto2 = databaseService.getPhotoByFilename("20220301_185001.jpg")
        val selectedPhoto3 = databaseService.getPhotoByFilename("20220301_185010.jpg")
        if (selectedPhoto1 != null) {
            selectedPhotos.add(selectedPhoto1)
        }
        if (selectedPhoto2 != null) {
            selectedPhotos.add(selectedPhoto2)
        }
        if (selectedPhoto3 != null) {
            selectedPhotos.add(selectedPhoto3)
        }

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE;

        layout = GridBagLayout();
        val constraints = GridBagConstraints()
        constraints.anchor = WEST
        constraints.fill = BOTH
        constraints.weightx = 1.0
        constraints.weighty = 0.04

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridwidth = 2
        add(mainToolBar, constraints)

        constraints.gridy++
        constraints.weighty = 0.18
        add(allPhotosPanel, constraints)

        constraints.gridy++
        //doppelt selectedPhotosPanel = SelectedPhotosPanel(this, selectedPhotos)
        add(selectedPhotosPanel, constraints)

        constraints.gridy++
        constraints.weightx = 0.5
        constraints.weighty = 0.60
        constraints.gridwidth = 1

        add(noticeForm, constraints)

        constraints.gridx = 1
        constraints.fill = BOTH
        zoomPanel = ZoomPanel(this)
        add(zoomPanel, constraints)

        setSize(1000, 700)
        isVisible = true
    }

    /**
     * wählt ein Foto aus

    fun selectPhoto(miniPhotoPanel: MiniPhotoPanel) {
    log.debug("select photo")
    allPhotosPanel.deactivatePhoto(miniPhotoPanel)
    val photo = miniPhotoPanel.getPhoto()
    selectedPhotosPanel.addPhoto(photo)
    log.debug("selected photo: $photo")
    log.debug("zoomed photo: ${zoomPanel.getMaxiPhoto()}")
    zoomPanel.showSelectedPhoto(photo)
    allPhotosPanel.hideBorder()
    selectedPhotosPanel.showBorder(photo)
    }
     */

    /**
     * wählt ein Foto aus
     */
    fun selectPhoto(photo: Photo) {
        log.debug("select photo")
        selectedPhotos.add(photo)
        //allPhotosPanel.deactivatePhoto(photo)
        log.debug("selected photo: $photo")
        log.debug("zoomed photo: ${zoomPanel.getMaxiPhoto()}")
        zoomPanel.showSelectedPhoto(photo)
        selectedPhotosPanel.showBorder(photo)

        //val index = selectedPhotosPanel.indexOfPhoto(photo)
        val index = selectedPhotos.getPhotos().indexOf(photo)
        //noticeForm.getMiniMap().addMarker(index, photo)
    }

    /**
     * entfernt ein Foto aus der Auswahl für die Meldung
    fun unselectPhoto(photoPanel: MiniSelectedPhotoPanel) {
    log.debug("unselect photo")
    selectedPhotosPanel.removePhoto(photoPanel)
    allPhotosPanel.activatePhoto(photoPanel.getPhoto())
    val photo = photoPanel.getPhoto()
    zoomPanel.showPhoto(photo)
    allPhotosPanel.showBorder(photo)
    selectedPhotosPanel.hideBorder()
    }
     */

    /**
     * entfernt ein Foto aus der Auswahl für die Meldung
     */
    fun unselectPhoto(photo: Photo) {
        log.debug("unselect photo")
        //val index = selectedPhotosPanel.indexOfPhoto(photo)
        val index = selectedPhotos.getPhotos().indexOf(photo)
        //noticeForm.getMiniMap().removeMarker(index)
        //selectedPhotosPanel.removePhoto(photo)
        selectedPhotos.remove(photo)
        zoomPanel.showPhoto(photo)
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich eine große Landkarte an
     */
    fun showMaxiMap() {
        log.debug("show maxi map")
        noticeForm.getMiniMap().displayBorder(true)
        zoomPanel.showMap()
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich ein großes Foto an
     */
    fun showPhoto(miniPhotoPanel: MiniPhotoPanel) {
        log.debug("show photo")
        zoomPanel.showPhoto(miniPhotoPanel.getPhoto())
        noticeForm.getMiniMap().displayBorder(false)
        allPhotosPanel.showBorder(miniPhotoPanel)
        selectedPhotosPanel.hideBorder()
    }

    /**
     * zeigt im Zoom-Bereich ein großes bereits ausgewähltes Foto an
     */
    fun showSelectedPhoto(miniSelectedPhotoPanel: MiniSelectedPhotoPanel) {
        log.debug("show selected photo")
        zoomPanel.showSelectedPhoto(miniSelectedPhotoPanel.getPhoto())
        noticeForm.getMiniMap().displayBorder(false)
        allPhotosPanel.hideBorder()
        selectedPhotosPanel.showBorder(miniSelectedPhotoPanel)
    }


    fun findAddress(position: GeoPosition) {
        val lat = "%.8f".format(position.latitude)
        val lon = "%.8f".format(position.longitude)
        val url =
            URL("https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$lat&lon=$lon&email=hz@heikozelt.de")
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        println(connection.responseCode)
        println(connection.getHeaderField("Content-Type"))
        val text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
        log.debug(text)

        val nominatimResponse = Klaxon().parse<NominatimResponse>(text)
        log.debug("displayName: ${nominatimResponse?.displayName}")
        log.debug("houseNumber: ${nominatimResponse?.address?.houseNumber}")

        nominatimResponse?.address?.let {
            if (it.road != null) {
                var street = it.road
                if (it.houseNumber != null) {
                    street += " " + it.houseNumber
                }
                noticeForm.setStreet(street)
            }
            if (it.postcode != null) {
                noticeForm.setZipCode(it.postcode)
            }
            if (it.city != null) {
                noticeForm.setTown(it.city)
            }
        }
    }

    companion object {
        val NORMAL_BORDER = BorderFactory.createLineBorder(Color.black)
        val HIGHLIGHT_BORDER = BorderFactory.createLineBorder(Color.yellow)
        val NO_BORDER = BorderFactory.createEmptyBorder()

        val TEXT_COLOR = Color.white
        val PHOTO_MARKER_BACKGROUND = Color(101, 162, 235)

        val TOOLBAR_BACKGROUND = Color(50, 50, 50)
        val ALL_PHOTOS_BACKGROUND = Color(20, 20, 20)
        val SELECTED_PHOTOS_BACKGROUND = Color(50, 50, 50)
        val FORM_BACKGROUND = Color(20, 20, 20)
        val ZOOM_PANEL_BACKGROUND = Color(35, 35, 35)
    }
}