package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.json.Settings
import java.nio.file.Path

/**
 * load and save Settings from file system.
 * (or Windows registry?)
 */
interface SettingsRepo {

    /**
     * simple setter method to change the default path
     * todo: only in file repo
     */
    fun setPath(settingsPath: Path)

    /**
     * This factory method could be a constructor, but it's better to have a name for it,
     * which clearly tells what it does.
     * And there is already a constructor without parameters.
     * @param path Usually no path should be specified. But for unit tests a path should be given.
     */
    fun load(): Settings

    /**
     * @param path Usually no path should be specified. But for unit tests a path should be given.
     */
    fun save(settings: Settings)
}