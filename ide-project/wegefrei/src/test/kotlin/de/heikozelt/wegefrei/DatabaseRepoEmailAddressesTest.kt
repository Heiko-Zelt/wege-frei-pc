package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.email.EmailAddressEntity
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class DatabaseRepoEmailAddressesTest {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     *
     */
    @Test
    fun findAllEmailAddresses() {
        val addresses = databaseRepo?.findAllEmailAddresses()
        assertNotNull(addresses)
        addresses?.let {
            assertEquals(20, addresses.size)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        private var databaseRepo: DatabaseRepo? = null

        @BeforeAll @JvmStatic
        fun inserts() {
            LOG.debug("BeforeAll()")
            databaseRepo = DatabaseRepo.fromMemory()

            for (i in 0 until 10) {
                val adr = EmailAddressEntity(
                    "verkehrsueberwachung$i@junit-test-stadt.de",
                    "Verkehrsüberwachung$i JUnit-Test-Stadt"
                )
                databaseRepo?.insertEmailAddress(adr)
            }
            for (i in 0 until 10) {
                val adr = EmailAddressEntity(
                    "ueberwachung$i@test-stadt.de",
                )
                databaseRepo?.insertEmailAddress(adr)
            }
            databaseRepo?.logStatistics()
        }

        @AfterAll @JvmStatic
        fun close_db() {
            databaseRepo?.close()
        }
    }
}