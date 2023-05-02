package de.heikozelt.wegefrei.db

import de.heikozelt.wegefrei.db.entities.NoticeEntity

/**
 * is called whenever there are changes in the notices database table
 */
interface NoticesObserver {
    fun noticeInserted(noticeEntity: NoticeEntity)

    fun noticeUpdated(noticeEntity: NoticeEntity)

    fun noticeDeleted(noticeEntity: NoticeEntity)
}