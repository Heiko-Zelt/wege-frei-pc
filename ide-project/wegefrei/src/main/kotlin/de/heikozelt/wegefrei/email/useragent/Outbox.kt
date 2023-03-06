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
     * log failure and inform user
     * @param externalID NoticeID or null if next() didn't return an EmailMessage
     */
    fun sendFailedCallback(externalID: T?, exception: Throwable)
}