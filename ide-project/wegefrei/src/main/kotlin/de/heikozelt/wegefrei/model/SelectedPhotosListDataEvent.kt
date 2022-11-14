package de.heikozelt.wegefrei.model

import javax.swing.event.ListDataEvent

/**
 * Extend ListDataEvent with photos.
 * If photos are removed from the list, Listeners need to know which have been removed
 */
class SelectedPhotosListDataEvent(
    source: Any,
    type: Int,
    index0: Int,
    index1: Int,
    val photos: List<Photo>
): ListDataEvent(source, type, index0, index1)