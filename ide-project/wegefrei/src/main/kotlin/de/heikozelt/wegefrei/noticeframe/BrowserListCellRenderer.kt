package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import de.heikozelt.wegefrei.model.SelectedPhotosListModel
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

class BrowserListCellRenderer(): ListCellRenderer<Photo?> {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var selectedPhotosListModel: SelectedPhotosListModel? = null
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

        var active = false
        selectedPhotosListModel?.getSelectedPhotos()?.let { set ->
            active = value in set
        }

        return MiniPhotoPanel(noticeId, value, active, isSelected, -1)
    }

    fun setSelectedPhotos(selectedPhotosListModel: SelectedPhotosListModel) {
        this.selectedPhotosListModel = selectedPhotosListModel
    }
}