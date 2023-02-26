package de.heikozelt.wegefrei.email.useragent

/**
 * Represents an email outbox / producer
 */
interface Outbox {

    /**
     * Get the next email message to be sent or null,
     * if there is currently nothing to be sent
     * and sending loop/thread should be stopped.
     */
    fun next(): EmailMessage?

    /**
     * @param sendSuccess true if sending was successful
     */
    fun sendCallback(message: EmailMessage, sendSuccess: Boolean)
}