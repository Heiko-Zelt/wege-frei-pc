package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.entities.Photo
import jakarta.persistence.EntityManager
import jakarta.persistence.Persistence
import org.slf4j.LoggerFactory
import java.util.*


class DatabaseService {
    private val em: EntityManager
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        val factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)
        em = factory.createEntityManager()
    }

    fun getPhotoByFilename(filename: String): Photo? {
        log.debug("getPhotoByFilename($filename)")
        val photo = em.find(Photo::class.java, filename)
        if(photo == null) {
            log.debug("image $filename not found in database")
        } else {
            log.debug("image $filename found in database")
            log.debug("latitude: ${photo.latitude}")
            log.debug("longitude: ${photo.longitude}")
        }
        return photo
    }

    /**
     * liefert ein sortiertes Set von Fotos
     */
    fun getPhotos(firstPhotoFilename: String, limit: Int): Set<Photo> {
        val resultList: List<Photo> = em.createQuery("SELECT ph FROM Photo ph WHERE ph.filename >= :filename ORDER BY ph.filename", Photo::class.java)
            .setParameter("filename", firstPhotoFilename).setMaxResults(limit).resultList
        return TreeSet(resultList)
    }

    /**
     * liefert ein sortiertes Set von Meldungen
     */
    fun getAllNotices(): List<Notice> {
        val resultList: List<Notice> = em.createQuery("SELECT n FROM Notice n ORDER BY n.id", Notice::class.java).resultList
        return resultList
    }

    /**
     * liefert ein umgekehrt sortiertes Set von Meldungen
     * neueste (mit der höchsten ID) zuerst
     */
    fun getAllNoticesDesc(): List<Notice> {
        log.debug("getAllNoticesDesc()")
        //Thread.sleep(5000) Simulation langsamer Datenbank
        val resultList: List<Notice> = em.createQuery("SELECT n FROM Notice n ORDER BY n.id DESC", Notice::class.java).resultList
        log.debug("got result")
        return resultList
    }

    fun insertPhoto(photo: Photo) {
        //em.merge(photo)
        em.transaction.begin()
        em.persist(photo)
        em.transaction.commit()
    }

    fun insertNotice(notice: Notice) {
        em.transaction.begin()
        em.persist(notice)
        em.transaction.commit()
    }

    fun updateNotice(notice: Notice) {
        em.transaction.begin()
        em.merge(notice)
        em.transaction.commit()
    }

    fun deleteNotice(notice: Notice) {
        em.transaction.begin()
        em.remove(notice)
        em.transaction.commit()
    }

    companion object {
        /**
         * The filename includes a major software version number.
         * This makes it easy to have multiple major software versions installed in parallel.
         * If the file format changes significantly, the major software version should change too.
         */
        private const val PERSISTENCE_UNIT_NAME = "wegefrei"
    }
}