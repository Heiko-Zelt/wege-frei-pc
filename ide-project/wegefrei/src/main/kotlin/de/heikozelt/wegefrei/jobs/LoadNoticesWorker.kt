package de.heikozelt.wegefrei.jobs

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.entities.Notice
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
: SwingWorker<MutableList<Notice>, MutableList<Notice>>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var notices: MutableList<Notice>? = null

    /**
     * This is done in own Thread
     */
    override fun doInBackground(): MutableList<Notice>? {
        log.info("doInBackground()")
        notices = LinkedList(databaseRepo.getAllNoticesDesc())
        return notices
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        notices?.let {
            noticesTableModel.setNoticesList(it)
        }
    }

}