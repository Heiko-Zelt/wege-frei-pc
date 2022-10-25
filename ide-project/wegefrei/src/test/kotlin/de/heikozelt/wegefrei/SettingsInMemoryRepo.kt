package de.heikozelt.wegefrei

import com.beust.klaxon.Klaxon
import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory

/**
 * Load and save Settings from memory.
 * This is very useful for unit testing.
 */
class SettingsInMemoryRepo : SettingsRepo {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * In Memory JSON representation
     */
    private var text = "{}"

    /**
     * Load settings from memory.
     * They should contain default values, if not saved before.
     */
    override fun load(): Settings {
        val parseResult = Klaxon().parse<Settings>(text)
        return if (parseResult == null) {
            log.error("Settings could not be parsed. Using default settings.")
            Settings()
        } else {
            parseResult
        }
    }

    /**
     * @param settings Settings to be saved in memory.
     */
    override fun save(settings: Settings) {
        text = Klaxon().toJsonString(settings)
    }
}