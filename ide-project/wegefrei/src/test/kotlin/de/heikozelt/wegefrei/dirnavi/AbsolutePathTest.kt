package de.heikozelt.wegefrei.dirnavi

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class AbsolutePathTest {

    @Test
    fun subDirectories() {
        val relativePath = Paths.get("src", "test")
        val absolutePath = relativePath.toAbsolutePath()
        val p = AbsolutePath.fromPath(absolutePath)
        val subDirs = p.subDirectories()
        Assertions.assertEquals(2, subDirs.size)
        Assertions.assertEquals("kotlin", subDirs[0])
        Assertions.assertEquals("resources", subDirs[1])
    }
}