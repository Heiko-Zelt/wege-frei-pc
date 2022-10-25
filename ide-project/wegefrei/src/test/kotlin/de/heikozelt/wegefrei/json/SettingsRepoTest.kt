package de.heikozelt.wegefrei.json

import de.heikozelt.wegefrei.SettingsFileRepo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class SettingsRepoTest {

    @Test
    fun save_and_read_empty_settings() {
        val testSettingsPath = Path(System.getProperty("user.home"), ".wege_frei_v1_test.settings.json")
        val originalSettings = Settings()
        assertEquals("", originalSettings.lookAndFeel)
        val settingsRepo = SettingsFileRepo()
        settingsRepo.setPath(testSettingsPath)
        settingsRepo.save(originalSettings)
        val readSettings = settingsRepo.load()
        assertEquals("", readSettings.lookAndFeel)
    }
}