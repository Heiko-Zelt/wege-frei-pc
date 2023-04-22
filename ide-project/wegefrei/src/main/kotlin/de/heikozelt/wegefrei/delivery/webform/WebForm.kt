package de.heikozelt.wegefrei.delivery.webform

import org.openqa.selenium.WebDriver

interface WebForm {

    fun setSuccessfullySentCallback(callback: () -> Unit)

    fun setFailedCallback(callback: () -> Unit)

    fun setDriver(webDriver: WebDriver)

    fun validate(): List<String>

    fun sendNotice()
}