package de.heikozelt.wegefrei.fileutils

import com.sun.jna.platform.FileUtils
import java.io.File

class GnomeFileUtils: FileUtils() {

    override fun hasTrash(): Boolean {
        return true
    }

    override fun moveToTrash(vararg files: File?) {
        val builder = ProcessBuilder()
        val commandLine = mutableListOf(EXECUTABLE_PATH, "trash")
        val paths = files.map { it?.path.toString() }
        commandLine.addAll(paths)
        builder.command(commandLine)
        builder.start()
    }

    companion object {
        const val EXECUTABLE_PATH = "/usr/bin/gio"
    }
}