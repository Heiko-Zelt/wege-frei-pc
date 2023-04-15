package de.heikozelt.wegefrei.dirnavi

import java.io.File
import java.nio.file.Path

class LinuxPath(): AbsolutePath() {

    constructor(path: Path): this() {
        pathElements.add("/")
        path.iterator().forEach { pathElements.add(it.toString())}
    }

    constructor(str: String): this() {
        pathElements.add("/")
        val parts = str.split("/")
        parts.filter { it.isNotEmpty() }.forEach { pathElements.add(it)}
    }

    override fun asString(): String {
        return pathElements.subList(1, pathElements.size).joinToString("/", "/")
    }

    override fun subDirectories(): List<String> {
        val dir = File(asString())
        val files: Array<File>? = dir.listFiles()
        return if (files == null) {
            emptyList()
        } else {
            return files.filter { it.isDirectory && !it.isHidden }.map { it.name.toString() }.sorted()
        }
    }
}