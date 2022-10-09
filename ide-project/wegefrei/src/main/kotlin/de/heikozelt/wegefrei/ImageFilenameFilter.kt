package de.heikozelt.wegefrei

import java.io.File
import java.io.FilenameFilter

class ImageFilenameFilter: FilenameFilter {
    override fun accept(dir: File?, filename: String?): Boolean {
        if(filename == null) {
            return false
        }
        val lower = filename.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
    }
}