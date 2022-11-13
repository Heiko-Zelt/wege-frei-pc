package de.heikozelt.wegefrei.dirnavi

import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.Paths

abstract class AbsolutePath {
    protected val pathElements = mutableListOf<String>()

    fun getSize(): Int {
        return pathElements.size
    }

    operator fun get(level: Int): String {
        return pathElements[level]
    }

    fun iterator(): Iterator<String> {
        return pathElements.iterator()
    }

    fun truncate(level: Int) {
        val numberOfElementsToRemove = pathElements.size - level
        for(i in 0 until numberOfElementsToRemove) {
            pathElements.removeLast()
        }
    }

    abstract fun asString(): String

    fun asPath(): Path {
        return Paths.get(asString())
    }

    abstract fun subDirectories(): List<String>

    fun append(element: String) {
        pathElements.add(element)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        fun fromPath(path: Path): AbsolutePath {
            return if(path.toString().startsWith("/")) {
                LinuxPath(path)
            } else {
                WindowsPath(path)
            }
        }

        fun fromString(str: String): AbsolutePath {
            return if(str.startsWith("/")) {
                LinuxPath(str)
            } else {
                WindowsPath(str)
            }
        }
    }
}