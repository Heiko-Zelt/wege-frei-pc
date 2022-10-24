package de.heikozelt.wegefrei.json

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class SettingsTest {

    @Test
    fun save_and_read_empty_settings() {
        val testSettingsPath = Path(System.getProperty("user.home"), ".wege_frei_v1_test.settings.json")

        val originalSettings = Settings()
        assertEquals("", originalSettings.lookAndFeel)

        originalSettings.saveToFile(testSettingsPath)
        val readSettings = Settings.loadFromFile(testSettingsPath)

        assertEquals("", readSettings?.lookAndFeel)
    }
}