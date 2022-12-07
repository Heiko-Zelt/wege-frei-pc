package de.heikozelt.wegefrei.noticeframe

import com.sun.jna.platform.FileUtils
import de.heikozelt.wegefrei.model.Photo
import org.slf4j.LoggerFactory
import java.io.File

class MaxiPhotoPanel(
    noticeFrame: NoticeFrame,
    photo: Photo
) : BasePhotoViewer(noticeFrame, photo) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        actionButton.text = "hinzufügen"
        actionButton.addActionListener {
            log.debug("addActionListener(photo: pos.latitude=${photo.getGeoPosition()?.latitude})")
            noticeFrame.selectPhoto(photo)
        }
        // Annahme, wenn es Meta-Daten zu dem Foto in der Datenbank gibt,
        // dann ist es auch in einer Meldung referenziert und umgekehrt.
        val visible = photo.getPhotoEntity() == null
        deleteButton.isVisible = visible
        if (visible) {
            deleteButton.addActionListener {
                val path = photo.getPath()
                log.debug("move photo file $path to trash bin")
                val fileUtils = FileUtils.getInstance()
                if(!fileUtils.hasTrash()) {
                    val home = File(System.getProperty("user.home"))
                    val ubuntuTrash = File(home, ".local/share/Trash/files")
                    if(ubuntuTrash.isDirectory) {
                        System.setProperty("fileutils.trash", ubuntuTrash.absolutePath)
                        // todo Prio 4: Datei info/*.trashinfo anlegen, sonst gibt es kein Menüpunkt "Restore From Trash" im trash bin context menu
                        // todo Prio 1: mit Mac und Windows testen
                    }
                }
                fileUtils.moveToTrash(File(path.toString()))
                // Exception in thread "AWT-EventQueue-0" java.io.IOException:
                // No trash location found (define fileutils.trash to be the path to the trash)

                // todo Prio 1: remove from browser list
            }
        }

    }
}