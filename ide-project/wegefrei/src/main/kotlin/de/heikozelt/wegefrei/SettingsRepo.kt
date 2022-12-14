package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.json.Settings

/**
 * A very simple interface to load and save Settings
 * from/to file system, Windows registry, memory or wherever.
 * Currently, 2 implementations exist.
 */
interface SettingsRepo {
    /**
     * load settings
     */
    fun load(): Settings

    /**
     * save settings
     */
    fun save(settings: Settings)
}