package de.heikozelt.wegefrei.email.useragent

/**
 * Represents an email outbox / producer
 */
interface Outbox<T> {

    /**
     * Get the next email message to be sent or null,
     * if there is currently nothing to be sent
     * and sending loop/thread should be stopped.
     */
    fun next(): EmailMessage<T>?

    /**
     * @param sendSuccess true if sending was successful
     */
    fun sendCallback(message: EmailMessage<T>, sendSuccess: Boolean)
}