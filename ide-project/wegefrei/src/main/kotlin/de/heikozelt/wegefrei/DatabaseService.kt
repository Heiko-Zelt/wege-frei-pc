package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.entities.ProofPhoto
import jakarta.persistence.EntityManager
import jakarta.persistence.Persistence
import log


class DatabaseService {
    private val em: EntityManager

    init {
        val factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)
        em = factory.createEntityManager()
    }

    fun getImageByFilename(filename: String): ProofPhoto? {
        val photo = em.find(ProofPhoto::class.java, filename)
        if(photo == null) {
            log.debug("image $filename not found in database")
        } else {
            log.debug("image $filename found in database")
        }
        return photo
    }

    fun addProofPhoto(photo: ProofPhoto) {
        //em.merge(photo)
        em.transaction.begin()
        em.persist(photo)
        em.transaction.commit()
    }

    companion object {
        private const val PERSISTENCE_UNIT_NAME = "wegefrei"
    }
}