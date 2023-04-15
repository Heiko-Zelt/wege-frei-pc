package de.heikozelt.wegefrei.dirnavi

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class LinuxPathTest {

    @Test
    fun fromString() {
        val p = AbsolutePath.fromString("/home/heiko")
        Assertions.assertTrue(p is LinuxPath)
        assertEquals("/", p[0])
        assertEquals("home", p[1])
        assertEquals("heiko", p[2])
    }

    @Test
    fun asString() {
        val p = AbsolutePath.fromString("/home/heiko")
        Assertions.assertTrue(p is LinuxPath)
        assertEquals("/home/heiko", p.asString())
    }

    @Test
    fun truncate() {
        val p = AbsolutePath.fromString("/home/heiko")
        Assertions.assertTrue(p is LinuxPath)
        p.truncate(2)
        assertEquals(2, p.getSize())
        assertEquals("/", p[0])
        assertEquals("home", p[1])
    }
}