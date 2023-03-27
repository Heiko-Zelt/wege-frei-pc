import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

fun main(args: Array<String>) {
    val FORM_URL = "https://formular-server.de/Koeln_FS/findform?shortname=32-F68_file_AnzVerkW&formtecid=3&areashortname=send_html"
    //val FORM_URL = "file:///home/heiko/koeln/Anzeige einer Verkehrsordnungswidrigkeit.html"
    //val FORM_URL = "file:///home/heiko/koeln/Anzeige_local.html"
    val WITNESS_SURNAME = "Mustermann"
    val WITNESS_GIVEN_NAME = "Martina"
    val WITNESS_STREET = "Musterstraße 1"
    val WITNESS_ZIP_CODE = "12345"
    val WITNESS_TOWN = "Musterstadt"
    val WITNESS_EMAIL_ADDRESS = "martina.mustermann@test-web-online.de"
    val WITNESS_PHONE_NUMBER = "0123456789"
    val OFFENSE_DATE = "31.12.1999"
    val OFFENSE_TIME = "23:59"
    val OFFENSE_END_DATE = "1.1.2000"
    val OFFENSE_END_TIME = "0:1"
    val OFFENSE_STREET = "Domplatte"
    val OFFENSE_ZIP_CODE = "50667"
    val OFFENSE_TOWN = "Köln"
    val OFFENSE_QUARTER = "Altstadt / Nord"
    val OFFENSE = "Dies ist nur ein Test.\nParken im Rhein.\nBitte diese Anzeige ignorieren/löschen!"
    val VEHICLE_LICENSE_PLATE = "K-XY1234"
    val VEHICLE_COUNTRY = "XYZ"
    val VEHICLE_MAKE = "Volkswagen"
    val VEHICLE_TYPE = "PKW"
    val VEHICLE_COLOR = "Silber"
    val IMAGE_PATH = "/media/heiko/Paradies64/Falschparker/2023/03/23/20230323_180018.jpg"

    println("Hello World!")
    println("Program arguments: ${args.joinToString()}")

    val driver: WebDriver  = FirefoxDriver();
    driver.get(FORM_URL);

    val witnessSurnameInputField: WebElement? = driver.findElement(By.id("antragsteller.familienname"))
    val witnessGivenNameInputField: WebElement? = driver.findElement(By.id("antragsteller.vorname"))
    val witnessStreetInputField: WebElement? = driver.findElement(By.id("antragsteller.strasse_hausnr"))
    val witnessZipCodeInputField: WebElement? = driver.findElement(By.id("antragsteller.postleitzahl"))
    val witnessTownInputField: WebElement? = driver.findElement(By.id("antragsteller.ort"))
    val witnessEmailAddressInputField: WebElement? = driver.findElement(By.id("antragsteller.emailadresse"))
    val witnessPhoneNumberInputField: WebElement? = driver.findElement(By.id("antragsteller.telefon"))
    val offenseDate: WebElement? = driver.findElement(By.id("tat.datum"))
    val offenseTime: WebElement? = driver.findElement(By.id("tat.von"))
    val offenseEndDate: WebElement? = driver.findElement(By.id("tat.datum_ende"))
    val offenseEndTime: WebElement? = driver.findElement(By.id("tat.bis"))
    val offenseStreetInputField: WebElement? = driver.findElement(By.id("tat.strasse_hausnr"))
    val offenseZipCodeInputField: WebElement? = driver.findElement(By.id("tat.postleitzahl"))
    val offenseQuarterSelect: WebElement? = driver.findElement(By.id("tat.stadtteil"))
    val offenseTextArea: WebElement? = driver.findElement(By.id("tat.schilder"))
    val vehicleLicensePlateInputField: WebElement? = driver.findElement(By.id("auto.kennzeich"))
    val vehicleTypeSelect: WebElement? = driver.findElement(By.id("auto.art"))
    val vehicleCountryCodeInputField: WebElement? = driver.findElement(By.id("auto.laender"))
    val vehicleMakeInputField: WebElement? = driver.findElement(By.id("auto.marke"))
    val vehicleColorInputField: WebElement? = driver.findElement(By.id("auto.farbe"))
    val privacyProtectionLabel: WebElement? = driver.findElement(By.xpath("//label[@for='dsgvo']"))
    val photo1InputField: WebElement? = driver.findElement(By.id("image.upload_1"))
    val assertLabel: WebElement? = driver.findElement(By.xpath("//label[@for='Pg2_Obj10']"))
    val expenseLabel: WebElement? = driver.findElement(By.xpath("//label[@for='Pg2_Obj9']"))

    //val formSubmitButton: WebElement? = driver.findElement(By.id("abdiepost"))

    witnessSurnameInputField?.sendKeys(WITNESS_SURNAME)
    witnessGivenNameInputField?.sendKeys(WITNESS_GIVEN_NAME)
    witnessStreetInputField?.sendKeys(WITNESS_STREET)
    witnessZipCodeInputField?.sendKeys(WITNESS_ZIP_CODE)
    witnessTownInputField?.sendKeys(WITNESS_TOWN)
    witnessEmailAddressInputField?.sendKeys(WITNESS_EMAIL_ADDRESS)
    witnessPhoneNumberInputField?.sendKeys(WITNESS_PHONE_NUMBER)
    offenseDate?.sendKeys(OFFENSE_DATE)
    offenseTime?.sendKeys(OFFENSE_TIME)
    offenseEndDate?.sendKeys(OFFENSE_END_DATE)
    offenseEndTime?.sendKeys(OFFENSE_END_TIME)

    offenseStreetInputField?.sendKeys(OFFENSE_STREET)
    offenseZipCodeInputField?.sendKeys(OFFENSE_ZIP_CODE)
    offenseQuarterSelect?.sendKeys(OFFENSE_QUARTER)
    offenseTextArea?.sendKeys(OFFENSE)

    vehicleLicensePlateInputField?.sendKeys(VEHICLE_LICENSE_PLATE)
    vehicleCountryCodeInputField?.clear()
    vehicleCountryCodeInputField?.sendKeys(VEHICLE_COUNTRY)
    vehicleTypeSelect?.sendKeys(VEHICLE_TYPE)
    vehicleMakeInputField?.sendKeys(VEHICLE_MAKE)
    vehicleColorInputField?.sendKeys(VEHICLE_COLOR)
    photo1InputField?.sendKeys(IMAGE_PATH)
    privacyProtectionLabel?.click()
    assertLabel?.click()
    expenseLabel?.click()
    //formSubmitButton?.click()

    val wait = WebDriverWait(driver, Duration.ofMinutes(3))
    wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(FORM_URL)))
    println("Fertig :-)")
}