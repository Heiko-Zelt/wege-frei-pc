package de.heikozelt.wegefrei.dirnavi

import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

class WindowsPath() : AbsolutePath() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    init {
        pathElements.add("Dieser PC")
    }

    constructor(path: Path) : this() {
        log.debug("WindowsPath(path=$path)")
        pathElements.add(path.root.toString().removeSuffix("\\"))
        path.iterator().forEach { pathElements.add(it.toString()) }
    }

    constructor(str: String) : this() {
        val parts = str.split("\\")
        parts.filter { it.isNotEmpty() }.forEach { pathElements.add(it) }
    }

    override fun asString(): String {
        return if (pathElements.size == 2) {
            pathElements[1] + "\\"
        } else {
            pathElements.subList(1, pathElements.size).joinToString("\\")
        }
    }

    /**
     * @return entweder Laufwerksbuchstaben ("C:", "D:") oder Unterverzeichnisse
     */
    override fun subDirectories(): List<String> {
        if(pathElements.size == 1) {
            val paths: Array<File> = File.listRoots()
            return paths.map { it.toString().removeSuffix("\\") }
        } else {
            log.debug("asString: ${asString()}")
            val dir = File(asString())
            val files: Array<File>? = dir.listFiles()
            return if (files == null) {
                log.debug("files == null")
                emptyList()
            } else {
                files.forEach {
                    log.debug("unfiltered: $it")
                }

                return files.filter { it.isDirectory && !it.isHidden }.map { it.name.toString() }.sorted()
            }
        }
    }
}