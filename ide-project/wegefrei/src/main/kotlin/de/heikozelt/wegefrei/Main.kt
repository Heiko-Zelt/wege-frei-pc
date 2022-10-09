package de.heikozelt.wegefrei

import mu.KotlinLogging

val log = KotlinLogging.logger {}


fun main(args: Array<String>) {
    log.info("Wege frei!")
    log.debug("Program arguments: ${args.joinToString()}")

    val shutdownHook = Thread { log.info("exit") }
    Runtime.getRuntime().addShutdownHook(shutdownHook)

    App()

    log.debug("de.heikozelt.wegefrei.main function finished")
}



