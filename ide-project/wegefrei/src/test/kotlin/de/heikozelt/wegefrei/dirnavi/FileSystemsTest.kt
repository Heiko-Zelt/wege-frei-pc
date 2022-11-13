package de.heikozelt.wegefrei.dirnavi

import org.junit.jupiter.api.Test
import java.io.File
import javax.swing.filechooser.FileSystemView

class FileSystemsTest {

    @Test
    fun roots() {
        val fsv: FileSystemView? = FileSystemView.getFileSystemView()
        val paths: Array<File> = File.listRoots()
        for (path in paths) {
            println("Drive Name: $path")
            println("Description: " + fsv?.getSystemTypeDescription(path))
        }
    }
}