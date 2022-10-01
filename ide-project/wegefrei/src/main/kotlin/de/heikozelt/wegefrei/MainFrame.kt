package de.heikozelt.wegefrei

import mu.KotlinLogging
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter
import org.jxmapviewer.viewer.*
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import javax.swing.*

class MainFrame: JFrame("Wege frei!") {

    private val log = KotlinLogging.logger {}

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE;

        //layout = GridLayout(0,2, 20, 20);
        layout = GridBagLayout();
        val constraints = GridBagConstraints()
        constraints.anchor = WEST
        constraints.fill = BOTH
        constraints.weightx=0.5
        constraints.weighty=0.1

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridwidth = 2
        add(MainToolBar(), constraints)

        constraints.gridy++
        val countrySymbolLabel = JLabel("Länderkennzeichen:")
        constraints.gridx = 0
        constraints.gridwidth = 1
        add(countrySymbolLabel, constraints)
        val countrySymbolTextField = JTextField()
        //countrySymbolTextField.maximumSize = Dimension(100,10)
        //countrySymbolTextField.minimumSize = Dimension(100,10)
        //countrySymbolTextField.preferredSize = Dimension(100,10)
        constraints.gridx = 1
        add(countrySymbolTextField, constraints)

        constraints.gridy++
        val licensePlateLabel = JLabel("Kfz-Kennzeichen:")
        constraints.gridx = 0
        add(licensePlateLabel, constraints)
        val licensePlateTextField = JTextField()
        constraints.gridx = 1
        add(licensePlateTextField, constraints)

        constraints.gridy++
        val carMakeLabel = JLabel("Automarke:")
        constraints.gridx = 0
        add(carMakeLabel, constraints)
        val carMakeTextField = JTextField()
        constraints.gridx = 1
        add(carMakeTextField, constraints)

        constraints.gridy++
        val colorLabel = JLabel("Farbe:")
        constraints.gridx = 0
        add(colorLabel, constraints)
        val colorTextField = JTextField()
        constraints.gridx = 1
        add(colorTextField, constraints)

        constraints.gridy++
        val coordinatesLabel = JLabel("Koordinaten:")
        constraints.gridx = 0
        add(coordinatesLabel, constraints)

        val map = JXMapViewer()
        val info = OSMTileFactoryInfo()
        map.tileFactory = DefaultTileFactory(info)
        val mm = PanMouseInputListener(map)
        val mw = ZoomMouseWheelListenerCenter(map)
        map.addMouseListener(mm)
        map.addMouseMotionListener(mm)
        map.addMouseWheelListener (mw)

        val frankfurt = GeoPosition(50.11, 8.68)
        val wiesbaden = GeoPosition(50, 5, 0, 8, 14, 0)
        val mainz = GeoPosition(50, 0, 0, 8, 16, 0)

        val fitPoints = HashSet<GeoPosition>()
        fitPoints.add(frankfurt)
        fitPoints.add(wiesbaden)
        fitPoints.add(mainz)
        log.debug("zoom: " + map.zoom)
        map.size = Dimension(200,200)
        map.zoomToBestFit(fitPoints, 0.9)
        log.debug("zoom: " + map.zoom)

        //map.zoom = 11 // kleine Zahl = Details, große Zahl = Übersicht
        //map.addressLocation = frankfurt

        val waypoints = HashSet<Waypoint>()
        waypoints.add(DefaultWaypoint(frankfurt))
        waypoints.add(DefaultWaypoint(wiesbaden))
        waypoints.add(DefaultWaypoint(mainz))
        val painter = WaypointPainter<Waypoint>()
        painter.waypoints = waypoints
        map.overlayPainter = painter
        map.preferredSize = Dimension(200, 200)

        constraints.gridx = 1
        constraints.weighty= 1.0
        add(map, constraints)

        constraints.gridy++
        val streetLabel = JLabel("Straße, Hausnr:")
        constraints.gridx = 0
        constraints.weighty=0.1
        add(streetLabel, constraints)
        val streetTextField = JTextField()
        constraints.gridx = 1
        add(streetTextField, constraints)

        constraints.gridy++
        val zipCodeLabel = JLabel("PLZ:")
        constraints.gridx = 0
        add(zipCodeLabel, constraints)
        val zipCodeTextField = JTextField()
        constraints.gridx = 1
        add(zipCodeTextField, constraints)

        constraints.gridy++
        val townLabel = JLabel("Ort:")
        constraints.gridx = 0
        add(townLabel, constraints)
        val townTextField = JTextField()
        constraints.gridx = 1
        add(townTextField, constraints)

        constraints.gridy++
        val offenseDateLabel = JLabel("Datum:")
        constraints.gridx = 0
        add(offenseDateLabel, constraints)
        val offenseDateTextField = JTextField()
        constraints.gridx = 1
        add(offenseDateTextField, constraints)

        constraints.gridy++
        val offenseTimeLabel = JLabel("Uhrzeit:")
        constraints.gridx = 0
        add(offenseTimeLabel, constraints)
        val offenseTimeTextField = JTextField()
        constraints.gridx = 1
        add(offenseTimeTextField, constraints)

        constraints.gridy++
        val durationLabel = JLabel("Dauer:")
        constraints.gridx = 0
        add(durationLabel, constraints)
        val durationTextField = JTextField()
        constraints.gridx = 1
        add(durationTextField, constraints)

        constraints.gridy++
        val environmentalStickerCheckBox = JCheckBox("Umweltplakette fehlt")
        constraints.gridx = 0
        add(environmentalStickerCheckBox, constraints)

        val vehicleInspectionStickerCheckBox = JCheckBox("HU Plakette abgelaufen")
        constraints.gridx = 1
        add(vehicleInspectionStickerCheckBox, constraints)

        constraints.gridy++
        val abandonedCheckBox = JCheckBox("Fahrzeug war verlassen")
        constraints.gridx = 0
        add(abandonedCheckBox, constraints)

        constraints.gridy++
        val b = JButton("click")
        constraints.gridx = 0
        add(b, constraints)

        setSize(700, 700)
        isVisible = true
    }
}