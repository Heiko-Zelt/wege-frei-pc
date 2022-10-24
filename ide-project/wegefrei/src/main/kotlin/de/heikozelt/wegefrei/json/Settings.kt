package de.heikozelt.wegefrei.json

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import javax.swing.UIManager
import kotlin.io.path.Path


/**
 * @param lookAndFeel: java class name example: "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" or "javax.swing.plaf.metal.MetalLookAndFeel"
 */
class Settings (
    var witness: Witness = Witness(),

    @Json(name = "look_and_feel")
    var lookAndFeel: String = "",

    @Json(name = "photos_directory")
    var photosDirectory: String = "~",

    @Json(name = "database_directory")
    var databaseDirectory: String = "~"
) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * @param path Usually no path should be specified. But for unit tests a path should be given.
     */
    fun saveToFile(path: Path = SETTINGS_PATH) {
        //todo: Prio 3 it would be nice to have pretty print with line breaks and indents like in Gson
        log.debug("save settings to file ${path.toString()}")
        val text = Klaxon().toJsonString(this)
        val file = File(path.toString())
        file.createNewFile() // if file already exists will do nothing
        file.writeText(text)
    }

    /**
     * To set the look'n'feel the className-Field is needed
     */
    fun getLookAndFeelInfo(): UIManager.LookAndFeelInfo? {
        return UIManager.getInstalledLookAndFeels().firstOrNull { it.name == lookAndFeel }
    }

    companion object {

        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        /**
         * The filename includes a major software version number.
         * This makes it easy to have multiple major software versions installed in parallel.
         * If the file format changes significantly, the major software version should change too.
         */
        private val SETTINGS_PATH = Path(System.getProperty("user.home"), ".wege_frei_v1.settings.json")

        /**
         * This factory method could be a constructor, but it's better to have a name for it,
         * which clearly tells what it does.
         * And there is already a constructor without parameters.
         * @param path Usually no path should be specified. But for unit tests a path should be given.
         */
        fun loadFromFile(path: Path = SETTINGS_PATH): Settings {
            return try {
                val file = File(path.toString())
                val text = file.inputStream().readBytes().toString(Charsets.UTF_8)
                val parseResult = Klaxon().parse<Settings>(text)
                if(parseResult == null) {
                    LOG.error("Settings file could not be parsed. Using default settings.")
                    Settings()
                } else {
                    parseResult
                }
            } catch(ex: FileNotFoundException) {
                LOG.info("Settings file not found. Using default settings.")
                Settings()
            }
        }

        fun lookAndFeelNames(): List<String> {
            return UIManager.getInstalledLookAndFeels().map { it.name }
        }
    }
}