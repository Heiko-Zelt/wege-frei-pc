package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.entities.PhotoEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.Persistence
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.slf4j.LoggerFactory
import java.nio.file.Path


class DatabaseRepo(jdbcUrl: String) {
    private val em: EntityManager
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val sessionFactory: SessionFactory

    init {
        val persistenceMap = hashMapOf<String, String>()
        persistenceMap["jakarta.persistence.jdbc.url"] = jdbcUrl
        val factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, persistenceMap)
        em = factory.createEntityManager()
        val session = em.unwrap(Session::class.java)
        sessionFactory = session.sessionFactory
    }

    fun findPhotoByPath(path: Path): PhotoEntity? {
        log.debug("findPhotoByPath(path=$path)")
        return findPhotoByPath(path.toString())
    }

    fun findPhotoByPath(path: String): PhotoEntity? {
        log.debug("findPhotoByPath(str=$path)")
        val photoEntity: PhotoEntity?
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            photoEntity = session.find(PhotoEntity::class.java, path)
            tx.commit()
            if(photoEntity == null) {
                log.debug("image $path not found in database")
            }
        } finally {
            if(tx.isActive) tx.rollback()
            if(session.isOpen) session.close()
        }
        return photoEntity
    }

    fun logStatistics() {
        val nQuery = em.createQuery("select count(*) from NoticeEntity")
        val nCount = nQuery.singleResult
        log.info("NoticeEntity row count: $nCount")
        val pQuery = em.createQuery("select count(*) from PhotoEntity")
        val pCount = pQuery.singleResult
        log.info("PhotoEntity row count: $pCount")

        val stats = sessionFactory.statistics
        log.info("isStatisticsEnabled: ${stats.isStatisticsEnabled}")
        log.info("transactionCount: ${stats.transactionCount}")
        log.info("successfulTransactionCount: ${stats.successfulTransactionCount}")
        log.info("connecCount: ${stats.connectCount}")
        stats.logSummary()
    }

    fun findNoticeById(id: Int): NoticeEntity? {
        log.debug("findNoticeById($id)")
        val noticeEntity: NoticeEntity?
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            noticeEntity = session.find(NoticeEntity::class.java, id)
            tx.commit()
        } finally {
            if(tx.isActive) tx.rollback()
            if(session.isOpen) session.close()
        }
        return noticeEntity
    }

    /**
     * liefert ein sortiertes Set von Fotos
    fun findPhotos(firstPhotoFilename: String, limit: Int): Set<PhotoEntity> {
        val resultList: List<PhotoEntity> = em.createQuery("SELECT ph FROM PhotoEntity ph WHERE ph.filename >= :filename ORDER BY ph.filename", PhotoEntity::class.java)
            .setParameter("filename", firstPhotoFilename).setMaxResults(limit).resultList
        return TreeSet(resultList)
    }
    */

    /**
     * liefert ein umgekehrt sortiertes Set von Meldungen
     * neueste (mit der höchsten ID) zuerst
     */
    fun findAllNoticesDesc(): List<NoticeEntity>? {
        log.debug("getAllNoticesDesc()")
        //Thread.sleep(5000) Simulation langsamer Datenbank
        val resultList: List<NoticeEntity>?
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            resultList = session.createQuery("SELECT n FROM NoticeEntity n ORDER BY n.id DESC", NoticeEntity::class.java).resultList
            tx.commit()
        } finally {
            if(tx.isActive) tx.rollback()
            if(session.isOpen) session.close()
        }
        log.debug("got result. size=${resultList?.size}")
        return resultList
    }

    fun findAllNoticesIds(): List<Int>? {
        log.debug("getAllNoticesIds()")
        //Thread.sleep(5000) Simulation langsamer Datenbank
        val resultList: List<Int>?
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            resultList = session.createQuery("SELECT n.id FROM NoticeEntity n ORDER BY n.id DESC", Int::class.java).resultList
            tx.commit()
        } finally {
            if(tx.isActive) tx.rollback()
            if(session.isOpen) session.close()
        }
        log.debug("got result. size=${resultList?.size}")
        return resultList
    }

    fun insertPhoto(photoEntity: PhotoEntity) {
        log.debug("insertPhoto(path=${photoEntity.path})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            session.persist(photoEntity)
            tx.commit()
        } finally {
            if(tx.isActive) tx.rollback()
            if(session.isOpen) session.close()
        }
    }

    fun insertNotice(noticeEntity: NoticeEntity) {
        log.debug("insertNotice(id=${noticeEntity.id})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            //todo Illegal attempt to associate a collection with two open sessions: Collection :
            // [de.heikozelt.wegefrei.entities.PhotoEntity.noticeEntities
            session.persist(noticeEntity)
            tx.commit()
        } finally {
            if(tx.isActive) tx.rollback()
            if(session.isOpen) session.close()
        }
    }

    fun updateNotice(noticeEntity: NoticeEntity) {
        log.debug("updateNotice(id=${noticeEntity.id})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            session.merge(noticeEntity)
            tx.commit()
        } finally {
            if(tx.isActive) tx.rollback()
            if(session.isOpen) session.close()
        }
    }

    fun deleteNotice(noticeEntity: NoticeEntity) {
        log.debug("deleteNotice(id=${noticeEntity.id})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            //val mergedEntity = session.merge(noticeEntity)
            //session.remove(mergedEntity)
            session.remove(if (session.contains(noticeEntity)) noticeEntity else session.merge(noticeEntity))
            //session.remove(noticeEntity)
            tx.commit()
        } finally {
            if(tx.isActive) tx.rollback()
            if(session.isOpen) session.close()
        }

    }

    fun close() {
        if(em.isOpen) {
            log.debug("closing entity manager")
            em.close()
            log.debug("entity manager closed")
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        /**
         * The filename includes a major software version number.
         * This makes it easy to have multiple major software versions installed in parallel.
         * If the file format changes significantly, the major software version should change too.
         */
        private const val PERSISTENCE_UNIT_NAME = "wegefrei"

        fun fromDirectory(directory: String): DatabaseRepo {
            LOG.info("use database in directory: $directory")
            return DatabaseRepo("jdbc:h2:file:$directory/wege_frei_v1")
        }

        fun fromMemory(): DatabaseRepo {
            LOG.info("use in memory database")
            return DatabaseRepo("jdbc:h2:mem:wege_frei_v1")
        }
    }
}