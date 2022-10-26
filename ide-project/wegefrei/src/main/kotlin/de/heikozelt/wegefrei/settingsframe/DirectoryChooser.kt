package de.heikozelt.wegefrei.settingsframe

import org.slf4j.LoggerFactory
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JLabel

/**
 * Choose directory dialog.
 * @param label The text of the Label holds the directory path.
 * @param title dialog window title.
 */
class DirectoryChooser(private val label: JLabel, title: String): JFileChooser() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        fileSelectionMode = DIRECTORIES_ONLY
        currentDirectory = File(label.text)
        dialogTitle = title
        isAcceptAllFileFilterUsed = true
        val returnVal = showOpenDialog(this)
        if (returnVal == APPROVE_OPTION) {
            val file = selectedFile;
            log.debug("return value: $returnVal, file: $file")
            label.text = file.path
        }
    }

}