package hello

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.ExpectedCondition
import org.slf4j.LoggerFactory


class MyExpectedCondition : ExpectedCondition<Boolean> {

    override fun apply(driver: WebDriver): Boolean {
        return try {
            driver.title;
            false;
        } catch (ex: WebDriverException) {
            //LOG.debug("WebDriverException in condition:", ex);
            true;
        }
    }


    companion object {
        val LOG = LoggerFactory.getLogger(DemoApp::class.java)
    }
}