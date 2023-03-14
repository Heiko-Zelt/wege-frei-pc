package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.email.EmailAddressEntity
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.entities.PhotoEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.Persistence
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.time.ZonedDateTime


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
            val jpql = "SELECT p FROM PhotoEntity p LEFT JOIN FETCH p.noticeEntities WHERE p.path = ?1"
            val qry = session.createQuery(jpql, PhotoEntity::class.java)
            qry.setParameter(1, path)
            photoEntity = qry.singleResultOrNull
            //photoEntity = session.find(PhotoEntity::class.java, path)
            tx.commit()
            if (photoEntity == null) {
                log.debug("image $path not found in database")
            }
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
        return photoEntity
    }

    fun findNoticeById(id: Int): NoticeEntity? {
        log.debug("findNoticeById($id)")
        val noticeEntity: NoticeEntity?
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            /*
            noticeEntity = session.find(NoticeEntity::class.java, id)
            log.debug("not so lazy: ${noticeEntity?.photoEntities?.size}")
             */
            val jpql = "SELECT n FROM NoticeEntity n LEFT JOIN FETCH n.photoEntities WHERE n.id = ?1"
            val qry = session.createQuery(jpql, NoticeEntity::class.java)
            qry.setParameter(1, id)
            noticeEntity = qry.singleResultOrNull
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
        return noticeEntity
    }

    /**
     * Liefert die nächste Nachricht, welche gesendet werden soll,
     * oder null, wenn es nichts (mehr) zu senden gibt.
     * Ermittelt wird aus allen zu sendenden Nachrichten,
     * die mit den (bis jetzt) wenigsten Sendefehlern.
     *
     * Blöder Algorithmus: Es könnte bedeuten,
     * dass versucht wird, dieselbe Nachricht direkt wieder zu senden,
     * obwohl noch andere warten.
     * todo Prio 3: besseren Algorithmus finden. Merken, bei welchen Nachrichten im aktuellen Lauf, das Senden fehl schlug.
     */
    fun findNextNoticeToSend(): NoticeEntity? {
        log.debug("findNextNoticeToSend()")
        val noticeEntity: NoticeEntity?
        val session = sessionFactory.openSession()
        val tx = session.beginTransaction()
        try {
            val jpql = """
                SELECT n FROM NoticeEntity n LEFT JOIN FETCH n.photoEntities
                  WHERE n.finalizedTime <> null
                  AND n.sentTime = null
                  ORDER BY n.sendFailures, n.finalizedTime""".trimIndent()
            val qry = session.createQuery(jpql, NoticeEntity::class.java)

            /*
            throws exception
            noticeEntity = qry.setMaxResults(1).singleResult
            */

            val list: List<NoticeEntity>  = qry.setMaxResults(1).resultList
            noticeEntity = list.firstOrNull()
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
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
    fun findAllNoticesDesc(): List<NoticeEntity> {
        log.debug("findAllNoticesDesc()")
        //Thread.sleep(5000) Simulation langsamer Datenbank
        val resultList: List<NoticeEntity>?
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            val jpql = "SELECT n FROM NoticeEntity n ORDER BY n.id DESC"
            resultList = session.createQuery(jpql, NoticeEntity::class.java).resultList
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
        log.debug("got result. size=${resultList?.size}")
        return resultList?:emptyList()
    }

    fun findAllNoticesIdsDesc(): List<Int> {
        log.debug("findAllNoticesIdsDesc()")
        //Thread.sleep(5000) Simulation langsamer Datenbank
        val resultList: List<Int>?
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            val jpql = "SELECT n.id FROM NoticeEntity n ORDER BY n.id DESC"
            resultList = session.createQuery(jpql, Int::class.java).resultList
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
        log.debug("got result. size=${resultList?.size}")
        return resultList ?: emptyList()
    }

    fun findAllEmailAddresses(): List<EmailAddressEntity>? {
        log.debug("findAllEmailAddresses()")
        //Thread.sleep(5000) Simulation langsamer Datenbank
        val resultList: List<EmailAddressEntity>?
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            val jpql = "SELECT e FROM EmailAddressEntity e ORDER BY e.address"
            resultList = session.createQuery(jpql, EmailAddressEntity::class.java).resultList
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
        log.debug("got result. size=${resultList?.size}")
        return resultList
    }

    fun insertEmailAddress(emailAddressEntity: EmailAddressEntity) {
        log.debug("insertEmailAddress(${emailAddressEntity.asText()})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            session.persist(emailAddressEntity)
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
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
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
    }

    fun insertNotice(noticeEntity: NoticeEntity) {
        log.debug("insertNotice(id=${noticeEntity.id})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            insertMissingPhotos(session, noticeEntity)
            session.persist(noticeEntity)
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
    }

    private fun insertMissingPhotos(session: Session, noticeEntity: NoticeEntity) {
        noticeEntity.photoEntities.forEach { photo ->
            photo.path?.let { path ->
                val photoDb = session.find(PhotoEntity::class.java, photo.path)
                if (photoDb == null) {
                    log.debug("hash and persist photo")
                    photo.hash = sha1(path)
                    session.persist(photo)
                }
            }
        }
    }

    /**
    private fun deleteOrphanedPhotos(session: Session, noticeEntity: NoticeEntity) {
        noticeEntity.photoEntities.forEach { photo ->
            log.debug("found photo. path=${photo.path}")
            if (photo.noticeEntities.size == 0) {
                log.debug("REMOVE ORPHANED PHOTO")
                session.remove(photo)
            }
        }
    }
    */

    fun updateNotice(noticeEntity: NoticeEntity) {
        log.debug("updateNotice(id=${noticeEntity.id})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            insertMissingPhotos(session, noticeEntity)
            session.merge(noticeEntity)
            deleteOrphanedPhotos(session)
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
    }

    /**
     * markiert eine Meldung/Nachricht als erfolgreich gesendet. /
     * changes fields, which represent state change after an email message was sent.
     * These are sentTime and messageId.
     * Problem #1: Mapping from Message to Notice
     * Possible solutions:
     * <ol>
     *     <li>my solution: add notice/external id to EmailMessage</li>
     *     <li>other solution: generate message id and assign it to the notice entity when finalizing message
     *       (but the message id is usually generated later, when an email message ist sent)</li>
     * </ol>
     * Problem #2: saving sentTime
     * my solution:
     * set sent time on client side in class EmailMessage/MimeMessage, when starting to send the message and log this time.
     */
    fun updateNoticeSent(noticeID: Int, sentTime: ZonedDateTime, messageID: ByteArray) {
        log.debug("updateNoticeSent(id=${noticeID})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            val existing = session.find(NoticeEntity::class.java, noticeID)
            existing.sentTime = sentTime
            existing.messageId = messageID
            session.merge(existing)
            tx.commit()
        } finally {
          if (tx.isActive) tx.rollback()
          if (session.isOpen) session.close()
        }
    }

    fun updateNoticeSendFailed(noticeID: Int) {
        log.debug("updateNoticeSendFailed(id=${noticeID})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            val existing = session.find(NoticeEntity::class.java, noticeID);
            existing.sendFailures++
            session.merge(existing)
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
    }


    fun updateEmailAddress(emailAddressEntity: EmailAddressEntity) {
        log.debug("updateEmailAddress(address=${emailAddressEntity.address})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            session.merge(emailAddressEntity)
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
    }

    /**
     * If the primary key changes, merge/update is not possible with JPA.
     */
    fun replaceEmailAddress(oldAddress: String, newEmailAddressEntity: EmailAddressEntity) {
        log.debug("replaceEmailAddress(oldAddress=$oldAddress, newAddress=${newEmailAddressEntity.address})")
        val session = sessionFactory.openSession()
        val tx = session.beginTransaction()
        try {
            val emailAddress = session.find(EmailAddressEntity::class.java, oldAddress)
            if (emailAddress == null) {
                log.warn("Can't delete email address because it was not found in the database.")
            } else {
                session.remove(emailAddress)
                session.persist(newEmailAddressEntity)
            }
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
    }

    private fun deleteOrphanedPhotos(session: Session) {
        val select = "SELECT p FROM PhotoEntity p WHERE p NOT IN (SELECT n.photoEntities FROM NoticeEntity n)"
        //val jpql = "SELECT p FROM PhotoEntity p WHERE NOT EXITS (SELECT n FROM NoticeEntity n WHERE p MEMBER OF n.photoEntities)"
        //val jpql = "SELECT p FROM PhotoEntity p WHERE COUNT(p.noticeEntities) = 0"
        val result = session.createQuery(select, PhotoEntity::class.java).resultList
        result?.forEach {
            log.info("found orphaned photo: path=${it.path}, deleting it")
            session.remove(it)
        }
    }

    /**
     * todo Prio 1: bug: notice can't be removed, if a photo is part of another notice
     * foreign key violation!
     * solution: delete photos "manually", if they have no notices
     */
    fun deleteNotice(noticeId: Int) {
        log.debug("deleteNotice(id=${noticeId})")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            val notice = session.find(NoticeEntity::class.java, noticeId)
            if (notice == null) {
                log.warn("Can't delete notice because it was not found in the database.")
            } else {
                log.debug("PHOTOS.SIZE=${notice.photoEntities.size}")
                notice.photoEntities.forEach { it.noticeEntities.remove(notice) }
                session.remove(notice)
                deleteOrphanedPhotos(session)
            }
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
    }

    /**
     * todo Prio 1: bug: notice can't be removed, if a photo is part of another notice
     * foreign key violation!
     * solution: delete photos "manually", if they have no notices
     */
    fun deleteEmailAddress(address: String) {
        log.debug("deleteEmailAddress(address=$address)")
        val session = sessionFactory.openSession()
        log.debug("session: $session")
        val tx = session.beginTransaction()
        try {
            val emailAddress = session.find(EmailAddressEntity::class.java, address)
            if (emailAddress == null) {
                log.warn("Can't delete email address because it was not found in the database.")
            } else {
                session.remove(emailAddress)
            }
            tx.commit()
        } finally {
            if (tx.isActive) tx.rollback()
            if (session.isOpen) session.close()
        }
    }

    fun close() {
        if (em.isOpen) {
            log.debug("closing entity manager")
            em.close()
            log.debug("entity manager closed")
        }
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
            return DatabaseRepo("jdbc:h2:file:$directory/wege_frei_v1_0_3")
        }

        fun fromMemory(): DatabaseRepo {
            LOG.info("use in memory database")
            return DatabaseRepo("jdbc:h2:mem:wege_frei_v1_0_3")
        }

    }
}