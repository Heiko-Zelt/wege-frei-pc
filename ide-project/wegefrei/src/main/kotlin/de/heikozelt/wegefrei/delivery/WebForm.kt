package de.heikozelt.wegefrei.delivery

import java.time.ZonedDateTime

interface WebForm {

    fun setSuccessfullySentCallback(callback: ((Int, ZonedDateTime) -> Unit))

    fun setFailedCallback(callback: (Int) -> Unit)

    fun validate(): List<String>

    fun sendNotice()
}