package de.heikozelt.wegefrei

import com.beust.klaxon.Klaxon
import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory
import java.nio.file.Path

/**
 * load and save Settings from file system.
 * (or Windows registry?)
 */
class SettingsInMemoryRepo : SettingsRepo {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * In Memory JSON representation
     */
    private var text = "{}"

    /**
     * simple setter method to change the default path
     */
    override fun setPath(settingsPath: Path) {
        // do nothing
    }

    /**
     * This factory method could be a constructor, but it's better to have a name for it,
     * which clearly tells what it does.
     * And there is already a constructor without parameters.
     * @param path Usually no path should be specified. But for unit tests a path should be given.
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
     * @param path Usually no path should be specified. But for unit tests a path should be given.
     */
    override fun save(settings: Settings) {
        text = Klaxon().toJsonString(settings)
    }
}