package de.heikozelt.wegefrei.email.useragent

import java.time.ZonedDateTime

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
     * mark message as being sent
     */
    fun sentSuccessfulCallback(externalID: T, sentTime: ZonedDateTime, messageID: ByteArray)

    /**
     * log failure
     */
    fun sendFailedCallback(externalID: T)
}