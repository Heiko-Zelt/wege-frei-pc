package de.heikozelt.wegefrei.delivery.webform

import org.openqa.selenium.WebDriver
import java.time.ZonedDateTime

interface WebForm {

    fun setSuccessfullySentCallback(callback: ((Int, ZonedDateTime) -> Unit))

    fun setFailedCallback(callback: (Int) -> Unit)

    fun setDriver(webDriver: WebDriver)

    fun validate(): List<String>

    fun sendNotice()
}