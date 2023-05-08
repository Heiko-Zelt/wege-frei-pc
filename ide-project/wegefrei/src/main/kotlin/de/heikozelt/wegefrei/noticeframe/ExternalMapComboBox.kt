package de.heikozelt.wegefrei.noticeframe

import de.heikozelt.wegefrei.noticeframe.ExternalMap.Companion.EXTERNAL_MAPS
import java.awt.Desktop
import javax.swing.JComboBox

class ExternalMapComboBox(noticeFrame: NoticeFrame): JComboBox<ExternalMap>(EXTERNAL_MAPS) {

    init {
        addActionListener {
            val position = noticeFrame.getOffensePosition()
            position?.let {
                selectedItem?.let { item ->
                    if (item is ExternalMap) {
                        val uri = item.toURI(position)
                        val desktop: Desktop? = Desktop.getDesktop()
                        desktop?.browse(uri)
                    }
                }
            }
        }
    }
}