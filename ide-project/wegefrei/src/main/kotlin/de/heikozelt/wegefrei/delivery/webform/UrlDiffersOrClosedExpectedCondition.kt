package de.heikozelt.wegefrei.delivery.webform

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.ExpectedCondition
import org.slf4j.LoggerFactory


class UrlDiffersOrClosedExpectedCondition(private val url: String): ExpectedCondition<WaitResult?> {

    override fun apply(driver: WebDriver): WaitResult? {
        return try {
            // URL changed (because form was submitted)?
            if(url == driver.currentUrl) {
                null
            } else {
                WaitResult.URL_DIFFERS
            }

        } catch (ex: WebDriverException) {
            // Browser Window Closed
            //LOG.debug("WebDriverException in condition:", ex);
            WaitResult.WINDOW_CLOSED
        }
    }

    companion object {
        val LOG = LoggerFactory.getLogger(UrlDiffersOrClosedExpectedCondition::class.java)
    }
}