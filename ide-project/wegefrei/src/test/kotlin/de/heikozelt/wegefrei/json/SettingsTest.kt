package de.heikozelt.wegefrei.json

import de.heikozelt.wegefrei.email.useragent.EmailServerConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SettingsTest {

    @Test
    fun clone_deep_copy() {
        val original = Settings(
            Witness(
                "Peter",
                "Müller",
                "pete@ori.ginal",
                "Mainstreet 1",
                "12345",
                "Frankfurt",
                "+49 0123/4567-89"
            ),
            1,
            EmailServerConfig(
                "smtp.ori.ginal",
                25,
                "pete",
                Tls.START_TLS
            ),
            "Metal",
            "/home/joe/wegefrei/photos",
            "/home/joe/wegefrei/database"
        )
        val klone = original.clone()

        assertFalse(original === klone)
        assertFalse(original.witness === klone.witness)
        assertFalse(original.emailServerConfig === klone.emailServerConfig)
        assertEquals(original.witness.emailAddress, klone.witness.emailAddress)
        assertEquals("pete@ori.ginal", original.witness.emailAddress)

        klone.witness.emailAddress = "changed"

        assertEquals("pete@ori.ginal", original.witness.emailAddress)

        assertFalse(original.witness.emailAddress === klone.witness.emailAddress)
        assertNotEquals(original.witness.emailAddress, klone.witness.emailAddress)
    }

    @Test
    fun data_class_equals() {
        val settings1 = Settings(
            Witness(
                "Joe",
                "Ünsever",
                "joe@ori.ginal",
                "Mainstreet 1",
                "12345",
                "Frankfurt",
                "+49 0123/4567-89"
            ),
            1,
            EmailServerConfig(
                "smtp.ori.ginal",
                25,
                "joe",
                Tls.START_TLS
            ),
            "Metal",
            "/home/joe/wegefrei/photos",
            "/home/joe/wegefrei/database"
        )
        val settings2 = Settings(
            Witness(
                "Joe",
                "Ünsever",
                "joe@ori.ginal",
                "Mainstreet 1",
                "12345",
                "Frankfurt",
                "+49 0123/4567-89"
            ),
            1,
            EmailServerConfig(
                "smtp.ori.ginal",
                25,
                "joe",
                Tls.START_TLS
            ),
            "Metal",
            "/home/joe/wegefrei/photos",
            "/home/joe/wegefrei/database"
        )
        assertEquals(settings1, settings2)
    }
}