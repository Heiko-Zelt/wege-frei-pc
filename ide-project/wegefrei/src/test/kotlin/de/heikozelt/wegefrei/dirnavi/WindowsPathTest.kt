package de.heikozelt.wegefrei.dirnavi

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class WindowsPathTest {

    @Test
    fun fromPath() {
        if ("Windows" in System.getProperty("os.name")) {
            val p = AbsolutePath.fromPath(Paths.get("C:\\Benutzer\\Hi"))
            assertTrue(p is WindowsPath)
            assertEquals(4, p.getSize())
            assertEquals("Dieser PC", p[0])
            assertEquals("C:", p[1])
            assertEquals("Benutzer", p[2])
            assertEquals("Hi", p[3])
        } else {
            println("not Windows")
        }
    }

    @Test
    fun fromString() {
        val p = AbsolutePath.fromString("C:\\Benutzer\\Hi")
        assertTrue(p is WindowsPath)
        assertEquals(4, p.getSize())
        assertEquals("Dieser PC", p[0])
        assertEquals("C:", p[1])
        assertEquals("Benutzer", p[2])
        assertEquals("Hi", p[3])
    }

    @Test
    fun asString() {
        val p = AbsolutePath.fromString("C:\\Benutzer\\Hi")
        assertTrue(p is WindowsPath)
        assertEquals("C:\\Benutzer\\Hi", p.asString())
    }

    @Test
    fun subDirectories_DriveLetters() {
        if ("Windows" in System.getProperty("os.name")) {
            println("Windows")
            val p = WindowsPath()
            val subDirs = p.subDirectories()
            assertEquals(5, subDirs.size)
            assertEquals("C:", subDirs[0])
            assertEquals("D:", subDirs[1])
        } else {
            println("not Windows")
        }
    }

    @Test
    fun truncate() {
        val p = AbsolutePath.fromString("C:\\Benutzer\\Hi")
        assertTrue(p is WindowsPath)
        p.truncate(2)
        assertEquals(2, p.getSize())
        assertEquals("Dieser PC", p[0])
        assertEquals("C:", p[1])
    }
}