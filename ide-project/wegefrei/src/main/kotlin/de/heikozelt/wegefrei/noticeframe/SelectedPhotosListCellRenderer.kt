package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory
import java.awt.Component
import javax.swing.JList
import javax.swing.ListCellRenderer

/**
 * Strategie:
 * <ol>
 *   <li>Erst mal ein leeres Panel (ggf. mit Dateiname) anzeigen</li>
 *   <li>Dann im Hintergrund die sonstigen Foto-Metadaten und Pixeldaten laden</li>
 *   <li>AbstractListModel.fireContentsChanged()</li>
 *   <li>Zuletzt das vollständige Panel anzeigen</li>
 * <ol>
 */

class SelectedPhotosListCellRenderer: ListCellRenderer<Photo?> {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * contains the notice id of the notice currently edited or null if it is a new notice not yet saved to the database
     */
    private var noticeId: Int? = null

    fun setNoticeId(noticeId: Int) {
        this.noticeId = noticeId
    }

    override fun getListCellRendererComponent(
        list: JList<out Photo?>?,
        value: Photo?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {

        return MiniPhotoPanel(noticeId, value, false, isSelected, index)
    }

}