package de.heikozelt.wegefrei.delivery.webform

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition


class UrlDiffersExpectedCondition(private val url: String): ExpectedCondition<Boolean> {

    override fun apply(driver: WebDriver): Boolean {
        return url != driver.currentUrl
    }

    /*
    companion object {
        val LOG = LoggerFactory.getLogger(UrlDiffersExpectedCondition::class.java)
    }
    */
}