package de.heikozelt.wegefrei.assertj

import de.heikozelt.wegefrei.SettingsInMemoryRepo
import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.json.Settings
import de.heikozelt.wegefrei.json.Tls
import de.heikozelt.wegefrei.settingsframe.SettingsFrame
import org.assertj.swing.core.matcher.JButtonMatcher.withText
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.fixture.FrameFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.io.path.Path


/**
 * Einstellungen-Fenster testen
 */
class SettingsFrameTest {
    private val path = Path("src/test/resources/wege_frei_v1_assertj_junit_test.settings.json")
    private var settings: Settings? = null
    private var settingsFrame: SettingsFrame? = null
    private val settingsRepo = SettingsInMemoryRepo()
    private var window: FrameFixture? = null
    private var app: WegeFrei? = null

    @BeforeEach
    fun setUp() {
        assertNotNull(settingsRepo)
        //doNothing().`when`(settingsRepo.saveToFile(any()))

        settings = settingsRepo.load()
        assertNotNull(settings)
        app = WegeFrei(settingsRepo)

        assertNotNull(app)

        //doNothing().`when`(mockApp).settingsChanged(isA(Settings::class.java))
        //doNothing().`when`(mockApp).settingsChanged(uninitialized())
        //doNothing().`when`(mockApp).changeLookAndFeel()

        app?.let {
            val funcy = { SettingsFrame(it) }
            settingsFrame = GuiActionRunner.execute(funcy)
        }
        window = FrameFixture(settingsFrame)
        assertNotNull(window)
        //-Dassertj.swing.keyboard.locale=en
        window!!.target().inputContext.selectInputMethod(Locale("de", "DE"))
        window?.show()
        settingsFrame?.setSettings(settings)
    }

    @Test
    fun open_settings_edit_email_address_save_and_close() {
        //window?.textBox("emailTextField")?.deleteText()
        //window?.textBox("emailTextField")?.enterText("!§%&/()=?` junit-test@heikozelt.de") Tastatur-Layout stimmt nicht
        window?.textBox("emailTextField")?.setText("changed@heikozelt.de")
        val closeButtonFix = window?.button(withText("Ok"))
        assertNotNull(closeButtonFix)
        closeButtonFix?.click()
        //window?.requireNotVisible()
        val newSettings = app?.getSettings()
        assertNotNull(newSettings)
        assertEquals("changed@heikozelt.de", newSettings?.witness?.emailAddress)
    }

    @Test
    fun open_settings_change_tls_encryption_save_and_close() {
        window?.comboBox("tlsComboBox")?.selectItem("StartTLS-verschlüsselt")
        val closeButtonFix = window?.button(withText("Ok"))
        assertNotNull(closeButtonFix)
        closeButtonFix?.click()
        //window?.requireNotVisible()
        val newSettings = app?.getSettings()
        assertNotNull(newSettings)
        assertEquals(Tls.START_TLS, newSettings?.emailServerConfig?.tls)
    }

    @AfterEach
    fun tearDown() {
        window?.cleanUp()
    }
}