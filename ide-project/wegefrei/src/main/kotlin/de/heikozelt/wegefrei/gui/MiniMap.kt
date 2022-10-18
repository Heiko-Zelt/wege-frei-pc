package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.gui.Styles.Companion.HIGHLIGHT_BORDER
import de.heikozelt.wegefrei.gui.Styles.Companion.NORMAL_BORDER
import de.heikozelt.wegefrei.model.SelectedPhotosObserver
import org.slf4j.LoggerFactory

/**
 * Beispiele für components:
 * { } leer
 * { photoMarker(0) } merkwürdiger Fall
 * { addressMarker } keine Geo-Location in Foto? manuell gesetzt?
 * { photoMarker(2), photoMarker(1), photoMarker(0) } merkwürdiger Fall
 * { addressMarker, photoMarker(2), photoMarker(1), photoMarker(0) } Perfekter Use Case
 * Sie werden in umgekehrter Reihenfolge gezeichnet.
 * Letzter Marker unten, erster Marker ganz obendrauf.
 *
 * Die Karte ist Anfangs beim Konstruktor-Aufruf komplett leer.
 * Erst mit load()-Data wird ggf. ein Adress-Marker gesetzt und ggf. Foto-Markers hinzugefügt.
 * Die Foto-Markers werden indirekt über das Observer-Pattern hinzugefügt oder entfernt.
 *
 * todo: Prio 1: Gemeinsamkeiten von MiniMap und MaxiMap in BaseMap-Klasse extrahieren
 */
class MiniMap(
    private val noticeFrame: NoticeFrame
) : BaseMap(noticeFrame), SelectedPhotosObserver {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var borderVisible = false

    init {
        log.debug("init")
        border = NORMAL_BORDER
    }

    fun displayBorder(visible: Boolean) {
        if (visible && !borderVisible) {
            border = HIGHLIGHT_BORDER
            revalidate()
            borderVisible = true
        } else if (!visible && borderVisible) {
            border = NORMAL_BORDER
            revalidate()
            borderVisible = false
        }
    }


}