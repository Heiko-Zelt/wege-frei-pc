package de.heikozelt.wegefrei.delivery.cologne

import de.heikozelt.wegefrei.delivery.WebForm
import de.heikozelt.wegefrei.entities.NoticeEntity
import de.heikozelt.wegefrei.json.Witness
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Spezieller Adapter, um Meldungen über das Web-Formular der Stadt Köln zu versenden
 */
class CologneWebForm(private val notice: NoticeEntity, private val witness: Witness): WebForm {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * parameters: notice id & sent time
     */
    private var successfullySentCallback: ((Int, ZonedDateTime) -> Unit)? = null

    /**
     * parameter: notice id
     */
    private var failedCallback: ((Int) -> Unit)? = null

    override fun setSuccessfullySentCallback(callback: ((Int, ZonedDateTime) -> Unit)) {
        successfullySentCallback = callback
    }

    override fun setFailedCallback(callback: (Int) -> Unit) {
        failedCallback = callback
    }

    /**
     * führt spezielle Validierungen für das Formular der Stadt Köln durch
     * (über die allgemeinen Validierungen hinaus)
     * todo Prio 2: Ist Veedel eins der 86 kölner Veedel?
     * todo Prio 2: Ist Fahrzeugart eine der vorgegebenen Arten?
     */
    override fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (notice.photoEntities.size > 3) {
            errors.add("Mittels Web-Formular der Stadt Köln können maximal 3 Fotos hochgeladen werden.")
        }
        return errors
    }

    /**
     * Füllt das Formular der Stadt Köln von oben bis unten, links nach rechts aus
     * todo Prio 2: Eigener Thread, damit GUI nicht einfriert
     */
    override fun sendNotice() {
        // todo: Treiber automatisch ermitteln oder Anwenderin wählen lassen
        val driver: WebDriver = FirefoxDriver()
        driver.get(FORM_URL);

        // fieldset: Angaben zur anzeigenerstattenden Person
        val salutationSelect: WebElement? = driver.findElement(By.id("antragsteller.herrn_frau"))
        val witnessSurnameInputField: WebElement? = driver.findElement(By.id("antragsteller.familienname"))
        val witnessGivenNameInputField: WebElement? = driver.findElement(By.id("antragsteller.vorname"))
        val witnessStreetInputField: WebElement? = driver.findElement(By.id("antragsteller.strasse_hausnr"))
        val witnessZipCodeInputField: WebElement? = driver.findElement(By.id("antragsteller.postleitzahl"))
        val witnessTownInputField: WebElement? = driver.findElement(By.id("antragsteller.ort"))
        val witnessEmailAddressInputField: WebElement? = driver.findElement(By.id("antragsteller.emailadresse"))
        val witnessPhoneNumberInputField: WebElement? = driver.findElement(By.id("antragsteller.telefon"))

        // fieldset: Angaben zur Verkehrsordnungswidrigkeit
        val offenseDate: WebElement? = driver.findElement(By.id("tat.datum"))
        val offenseTime: WebElement? = driver.findElement(By.id("tat.von"))
        val offenseEndDate: WebElement? = driver.findElement(By.id("tat.datum_ende"))
        val offenseEndTime: WebElement? = driver.findElement(By.id("tat.bis"))

        // fieldset: Tatort
        val offenseStreetInputField: WebElement? = driver.findElement(By.id("tat.strasse_hausnr"))
        val offenseZipCodeInputField: WebElement? = driver.findElement(By.id("tat.postleitzahl"))
        val offenseQuarterSelect: Select? = Select(driver.findElement(By.id("tat.stadtteil")))

        // fieldset: Kennzeichen des betroffenen Fahrzeuges
        val vehicleLicensePlateInputField: WebElement? = driver.findElement(By.id("auto.kennzeich"))
        val vehicleTypeSelect: WebElement? = driver.findElement(By.id("auto.art"))
        val vehicleCountryCodeInputField: WebElement? = driver.findElement(By.id("auto.laender"))
        val vehicleMakeInputField: WebElement? = driver.findElement(By.id("auto.marke"))
        val vehicleColorInputField: WebElement? = driver.findElement(By.id("auto.farbe"))

        // fieldset: Tatvorwurf
        val offenseTextArea: WebElement? = driver.findElement(By.id("tat.schilder"))

        // fieldset: Fotos
        val photoInputFields = arrayOf(
            driver.findElement(By.id("image.upload_1")),
            driver.findElement(By.id("image.upload_2")),
            driver.findElement(By.id("image.upload_3"))
        )

        // etc...
        val privacyProtectionLabel: WebElement? = driver.findElement(By.xpath("//label[@for='dsgvo']"))
        val assertLabel: WebElement? = driver.findElement(By.xpath("//label[@for='Pg2_Obj10']"))
        val expenseLabel: WebElement? = driver.findElement(By.xpath("//label[@for='Pg2_Obj9']"))

        // fieldset: Angaben zur anzeigenerstattenden Person
        salutationSelect?.sendKeys("Keine Angabe")
        witnessSurnameInputField?.sendKeys(witness.surname)
        witnessGivenNameInputField?.sendKeys(witness.givenName)
        witnessStreetInputField?.sendKeys(witness.street)
        witnessZipCodeInputField?.sendKeys(witness.zipCode)
        witnessTownInputField?.sendKeys(witness.town)
        witnessEmailAddressInputField?.sendKeys(witness.emailAddress)
        witnessPhoneNumberInputField?.sendKeys(witness.telephoneNumber)

        // fieldset: Angaben zur Verkehrsordnungswidrigkeit (Zeiten und Dauer)
        var startDate: String? = null
        notice.observationTime?.let {
            startDate = it.format(dateFormatter)
            offenseDate?.sendKeys(startDate)
            offenseTime?.sendKeys(it.format(timeFormatter))
        }
        notice.endTime?.let {
            val endDate = it.format(dateFormatter)
            if (endDate != startDate) {
                offenseEndDate?.sendKeys(endDate)
            }
            offenseEndTime?.sendKeys(it.format(timeFormatter))
        }

        // fieldset: Tatort
        offenseStreetInputField?.sendKeys(notice.street)
        offenseZipCodeInputField?.sendKeys(notice.zipCode)
        notice.quarter?.let {
            // Eine Option wird nur ausgewählt, wenn der Wert übereinstimmt.
            offenseQuarterSelect?.selectByValue(Veedel.convertQuarter(it))
        }

        // fieldset: Kennzeichen des betroffenen Fahrzeuges
        // todo Prio 2: Bricht ab, wenn das Kennzeichen nicht dem Format entspricht
        notice.licensePlate?.let {
            log.debug("license plate: ${notice.licensePlate}")
            vehicleLicensePlateInputField?.sendKeys(convertLicensePlate(it))
        }
        vehicleCountryCodeInputField?.clear()
        vehicleCountryCodeInputField?.sendKeys(notice.countrySymbol)
        vehicleTypeSelect?.sendKeys(notice.vehicleType)
        vehicleMakeInputField?.sendKeys(notice.vehicleMake)
        vehicleColorInputField?.sendKeys(notice.color)


        // fieldset: Tatvorwurf
        val statements = mutableListOf<String>()
        notice.offense?.let(statements::add)
        notice.note?.let(statements::add)
        if (notice.vehicleAbandoned) statements.add("Fahrzeug war verlassen")
        if (notice.warningLights) statements.add("Warnblinkanlage war eingeschaltet")
        if (notice.obstruction) statements.add("mit Behinderung")
        if (notice.endangering) statements.add("mit Gefährdung")
        if (notice.environmentalStickerMissing) statements.add("Umweltplakette fehlte/war ungültig")
        if (notice.vehicleInspectionExpired) {
            notice.getInspectionMonthYear()?.let {
                statements.add("HU-Plakette war abgelaufen. HU gültig bis: $it")
            }
        }
        notice.observationTime?.let {
            statements.add("Tatzeit: ${it.format(accurateTimeFormatter)}")
        }
        notice.endTime?.let {
            statements.add("Ende: ${it.format(accurateTimeFormatter)}")
        }
        statements.add("Dauer: ${notice.getDurationFormatted()}")
        //notice.getGeoPositionFormatted()?.let { statements.add("Geo-Position: $it") }
        offenseTextArea?.sendKeys(statements.joinToString(". "))

        // fieldset: Fotos
        notice.getPhotoEntitiesSorted().forEachIndexed { i, photo ->
            photoInputFields[i].sendKeys(photo.path)
        }

        // etc...
        privacyProtectionLabel?.click()
        assertLabel?.click()
        expenseLabel?.click()

        val wait = WebDriverWait(driver, Duration.ofMinutes(3))
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(FORM_URL)))
        // wait.until(numberOfWindowsToBe(0));
        println("Fertig.")
        driver.close()
        driver.quit()
        // Meldung muss vorher in der DB abgespeichert worden sein, damit id nicht null ist.
        notice.id?.let {
            successfullySentCallback?.invoke(it, ZonedDateTime.now())
        }


        // todo Prio 2: NoticeFrame disablen und warten bis Formular geschlossen wurde (gesendet oder abgebrochen)
        // todo Prio 1: warten und einen der beiden Callbacks aufrufen
        // todo Prio 3: PDF speichern
    }

    companion object {
        val FORM_URL =
            "https://formular-server.de/Koeln_FS/findform?shortname=32-F68_file_AnzVerkW&formtecid=3&areashortname=send_html"
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN)
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.GERMAN)
        val accurateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss z", Locale.GERMAN)

        /**
         * wandelt "MZ XY 1234" in "MZ-XY1234".
         * Mischformen in der Zeichensetzung sind auch erlaubt.
         * Annahmen: Eingabe enthält nur Großbuchstaben. Keine führenden oder folgenden Whitespaces.
         * Kennzeichen besteht aus Kürzel für Stadt/Landkreis, Leerzeichen oder Bindestrich, 1 oder mehrere Großbuchstaben, ggf. ein Leerzeichen und 1 oder mehrere Ziffern
         * Wenn das Kennzeichen nicht dem Muster entspricht, dann wird es unverändert zurückgeliefert.
         * todo: Landeskennzeichen berücksichtigen
         */
        fun convertLicensePlate(original: String): String {
            val regex = Regex("^([A-ZÄÖÜ]+)[ -]([A-ZÄÖÜ]+) ?(\\d+)$")
            val matchResult = regex.matchEntire(original)
            //log.debug("entire match: ${matchResult?.groups?.get(0)}")
            //log.debug("group value 1: ${matchResult?.groupValues?.get(1)}")
            return if (matchResult == null || matchResult.groups.size != 4) {
                original
            } else {
                "${matchResult.groupValues[1]}-${matchResult.groupValues[2]}${matchResult.groupValues[3]}"
            }
        }
    }
}