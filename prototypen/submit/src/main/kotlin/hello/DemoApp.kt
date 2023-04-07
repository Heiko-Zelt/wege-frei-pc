package hello

import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.time.Duration
import java.util.*
import java.util.logging.Level


private val LOG = LoggerFactory.getLogger(DemoApp::class.java)

@SpringBootApplication
open class DemoApp

fun main(args: Array<String>) {
    //SpringApplication.run(DemoApp::class.java, *args)
    runApplication<DemoApp>(*args)
    //Thread.sleep(10 * 1000)
    formidable()
}

/**
 * get a driver when available
 */
fun getWebDriver(): WebDriver? {
    if("Linux" == System.getProperty("os.name")) {
        // "webdriver.firefox.logfile"
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
    }
    System.setProperty("webdriver.chrome.silentOutput", "true");

    val driver = try {
        FirefoxDriver()
    } catch (ex: IllegalStateException) {
        try {
            EdgeDriver()
        } catch (ex: IllegalStateException) {
            try {
                ChromeDriver()
            } catch (ex: IllegalStateException) {
                try {
                    SafariDriver()
                    // throws: org.openqa.selenium.WebDriverException:
                    // Unable to find driver executable: /usr/bin/safaridriver
                } catch (ex: WebDriverException) {
                    null
                }
            }
        }
    }
    return driver
}

fun formidable() {
    val FORM_URL = "http://localhost:8080/form.html"
    val NAME = "Mustermann"
    val FILE_NAME = System.getProperty("user.dir") + File.separator + "20220718_130222.jpg"
    LOG.info("File name: $FILE_NAME")

    /*
    val fireProf = FirefoxProfile();
    fireProf.setPreference("webdriver.log.file", "/tmp/firefox_console.log");
    val fireOpt = FirefoxOptions()
    fireOpt.setProfile(fireProf)
    val driver: WebDriver = FirefoxDriver(fireOpt);
    */
    //val driver: WebDriver = FirefoxDriver()

    val driver = getWebDriver()
    if (driver == null) {
        LOG.error("No web driver found!")
        return
    }

    driver.get(FORM_URL);
    val nameInputField: WebElement? = driver.findElement(By.id("name"))
    nameInputField?.sendKeys(NAME)
    val fileInputField: WebElement? = driver.findElement(By.id("file"))
    // todo catch exception if file not found
    try {
        fileInputField?.sendKeys(FILE_NAME)
    } catch (ex: InvalidArgumentException) {
        LOG.info("invalid argument")
        LOG.info("Exception:", ex)
    }
    LOG.info("Formular ausgefüllt.\n.\n.\n")

    val wait = WebDriverWait(driver, Duration.ofSeconds(10))
    try {
        val result = wait.until(UrlDiffersOrClosedExpectedCondition(FORM_URL))
        //val result = wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(FORM_URL)))
        //val result = wait.until(MyExpectedCondition())
        LOG.info("Fertig mit Warten :-)")
        LOG.info("Result: $result")
    } catch (ex: TimeoutException) {
        LOG.info("Timeout");
    } catch (ex: Exception) {
        LOG.info("Fertig mit Exception :-(")
        // Exception: org.openqa.selenium.WebDriverException: Failed to decode response from marionette
        LOG.info("Exception:", ex)
    }


    try {
        LOG.info("Treiber beenden")
        driver.quit()
    } catch (ex: NoSuchSessionException) {
        LOG.info("Session ist (schon) geschlossen.")
    }

}