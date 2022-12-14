package de.heikozelt.wegefrei.model

import org.slf4j.LoggerFactory
import javax.swing.DefaultListSelectionModel

class BrowserListSelectionModel(
    private val selectedPhotosListModel: SelectedPhotosListModel,
    private val listModel: BrowserListModel
): DefaultListSelectionModel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * Photo is disabled / not selectable if it is grayed out / already in SelectedPhotos
     */
    override fun setSelectionInterval(index0: Int, index1: Int) {
        log.debug("setSelectionInterval($index0, $index1)")
        val photo = listModel.getElementAt(index0)
        //log.debug("entity: ${photo?.getPhotoEntity()} in ${selectedPhotos.getPhotos()}?")

        photo?.let {
            if(it !in selectedPhotosListModel.getSelectedPhotos()) {
                super.setSelectionInterval(index0, index1)
            }
        } ?: run {
            super.setSelectionInterval(index0, index1)
        }
    }
}