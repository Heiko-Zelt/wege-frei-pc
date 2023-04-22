package de.heikozelt.wegefrei.delivery.webform

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.gui.showValidationErrors
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.safari.SafariDriver
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

class WebFormWorkflow(private val notice: NoticeEntity, private var webForm: WebForm, private var dbRepo: DatabaseRepo): Thread() {

    init {
        webForm.setSuccessfullySentCallback(::successfullySentCallback)
        webForm.setFailedCallback(::failedCallback)
    }

    /**
     * In Hintergrund-Thread ausführen, denn das Ausfüllen des Formulars dauert relativ lange und
     * dann wird auch noch gewartet, bis die Anwenderin auf Absenden klickt.
     */
    override fun run() {
        val webDriver = getWebDriver()
        if(webDriver == null) {
            LOG.error("kein Webdriver")
            return
        }
        webForm.setDriver(webDriver)
        val errors = webForm.validate()
        if(errors.isEmpty()) {
            webForm.sendNotice()
        } else {
            showValidationErrors(errors)
        }
    }

    fun validateAndSend() {
        start()
    }

    fun successfullySentCallback() {
        //dbRepo.updateNoticeFinalizedAndSent(noticeId, sentTime)
        notice.finalizedTime = ZonedDateTime.now()
        notice.sentTime = notice.finalizedTime
        LOG.debug("successfullySentCallback() id: ${notice.id}, sentTime: ${notice.sentTime}")
        dbRepo.updateNotice(notice)
        // todo: Fenster schließen und Status in NoticesFrame updaten
    }

    fun failedCallback() {
        notice.sendFailures++
        LOG.debug("failedCallback() id: ${notice.id}, sendFailures: ${notice.sendFailures}")
        dbRepo.updateNotice(notice)
    }

    companion object {

        val LOG = LoggerFactory.getLogger(WebFormWorkflow::class.java)

        /**
         * get a web driver when available
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
    }
}