package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.model.Photo
import de.heikozelt.wegefrei.model.SelectedPhotos
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
    private var selectedPhotos: SelectedPhotos? = null
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
        /*
        if(value == null) {
            val label = JLabel("value == null")
            label.preferredSize = Dimension(Styles.THUMBNAIL_SIZE, Styles.THUMBNAIL_SIZE)
            log.error("value == null")
            return label
        }
        */

        var active = false
        selectedPhotos?.getPhotos()?.let { set ->
            value?.getPhotoEntity()?.let {entity ->
                active = entity in set
            }
        }

        return MiniPhotoPanel(noticeId, value, active, isSelected)
    }

    fun setSelectedPhotos(selectedPhotos: SelectedPhotos) {
        this.selectedPhotos = selectedPhotos
    }
}