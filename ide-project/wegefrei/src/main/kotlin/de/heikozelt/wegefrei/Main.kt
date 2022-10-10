package de.heikozelt.wegefrei

import mu.KotlinLogging
import javax.swing.UIManager

val log = KotlinLogging.logger {}


fun main(args: Array<String>) {
    log.info("Wege frei!")
    log.debug("Program arguments: ${args.joinToString()}")

    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (e: Exception) {
        log.error("exception while setting look and feel", e)
    }

    val shutdownHook = Thread { log.info("exit") }
    Runtime.getRuntime().addShutdownHook(shutdownHook)

    App()

    log.debug("de.heikozelt.wegefrei.main function finished")
}



