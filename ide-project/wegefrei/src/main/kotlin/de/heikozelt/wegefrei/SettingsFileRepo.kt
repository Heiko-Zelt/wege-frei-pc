package de.heikozelt.wegefrei

import com.beust.klaxon.Klaxon
import de.heikozelt.wegefrei.json.Settings
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * load and save Settings from file system.
 * (or Windows registry?)
 */
class SettingsFileRepo: SettingsRepo {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * The filename includes a major software version number.
     * This makes it easy to have multiple major software versions installed in parallel.
     * If the file format changes significantly, the major software version should change too.
     */
    private var settingsPath = Path(System.getProperty("user.home"), ".wege_frei_v1.settings.json")

    /**
     * simple setter method to change the default path
     */
    override fun setPath(settingsPath: Path) {
        this.settingsPath = settingsPath
    }

    /**
     * This factory method could be a constructor, but it's better to have a name for it,
     * which clearly tells what it does.
     * And there is already a constructor without parameters.
     * @param path Usually no path should be specified. But for unit tests a path should be given.
     */
    override fun load(): Settings {
        return try {
            val file = File(settingsPath.toString())
            val text = file.inputStream().readBytes().toString(Charsets.UTF_8)
            val parseResult = Klaxon().parse<Settings>(text)
            if (parseResult == null) {
                log.error("Settings file could not be parsed. Using default settings.")
                Settings()
            } else {
                parseResult
            }
        } catch (ex: FileNotFoundException) {
            log.info("Settings file not found. Using default settings.")
            Settings()
        }
    }

    /**
     * @param path Usually no path should be specified. But for unit tests a path should be given.
     */
    override fun save(settings: Settings) {
        //todo: Prio 3 it would be nice to have pretty print with line breaks and indents like in Gson
        log.debug("save settings to file ${settingsPath.toString()}")
        val text = Klaxon().toJsonString(settings)
        val file = File(settingsPath.toString())
        file.createNewFile() // if file already exists will do nothing
        file.writeText(text)
    }
}