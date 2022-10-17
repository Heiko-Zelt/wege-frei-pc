package de.heikozelt.wegefrei.assertj

import org.assertj.swing.core.GenericTypeMatcher
import org.slf4j.LoggerFactory
import java.awt.Frame

class NoticesFrameMatcher : GenericTypeMatcher<Frame>(Frame::class.java) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    override fun isMatching(component: Frame): Boolean {
        log.debug("isMatching()")
        return "Meldungen - Wege frei!" == component.title && component.isShowing
    }
}