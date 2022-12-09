package de.heikozelt.wegefrei.fileutils

import com.sun.jna.platform.FileUtils
import de.heikozelt.wegefrei.fileutils.GnomeFileUtils.Companion.EXECUTABLE_PATH
import java.io.File


fun getFileUtilsInstance(): FileUtils? {
    val executable = File(EXECUTABLE_PATH)
    return if (executable.canExecute()) { // Ubuntu
        GnomeFileUtils()
    } else { // Windows, Mac or Default
        FileUtils.getInstance()
    }
}