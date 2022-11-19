package de.heikozelt.wegefrei.jobs

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.model.NoticesTableModel
import org.slf4j.LoggerFactory
import java.util.*
import javax.swing.SwingWorker

/**
 * Läd die Meldungen aus der Datenbank
 * und übermittelt sie an das Datenmodell
 * der Tabelle im Übersichts-Fenster.
 */
class LoadNoticesWorker(
    private val databaseRepo: DatabaseRepo,
    private val noticesTableModel: NoticesTableModel
)
: SwingWorker<MutableList<NoticeEntity>, MutableList<NoticeEntity>>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var noticeEntities: MutableList<NoticeEntity>? = null

    /**
     * This is done in own Thread
     */
    override fun doInBackground(): MutableList<NoticeEntity>? {
        log.info("doInBackground()")
        noticeEntities = LinkedList(databaseRepo.findAllNoticesDesc())
        log.debug("finished in background. size=${noticeEntities?.size}")
        return noticeEntities
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        noticeEntities?.let {
            //noticesTableModel.setNoticesList(it)
        }
    }

}