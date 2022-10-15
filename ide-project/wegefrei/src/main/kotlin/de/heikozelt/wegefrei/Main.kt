package de.heikozelt.wegefrei

import org.slf4j.LoggerFactory
import java.awt.EventQueue

private val LOG = LoggerFactory.getLogger("de.heikozelt.wegefrei.MainKt")

fun main(args: Array<String>) {
    LOG.info("Wege frei!")
    LOG.debug("Program arguments: ${args.joinToString()}")

    /*
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (e: Exception) {
        LOG.error("exception while setting look and feel", e)
    }
     */

    val shutdownHook = Thread { LOG.info("exit") }
    Runtime.getRuntime().addShutdownHook(shutdownHook)

    EventQueue.invokeLater { App() }

    LOG.debug("de.heikozelt.wegefrei.main function finished")
}



