package de.heikozelt.wegefrei.assertj

import de.heikozelt.wegefrei.WegeFrei
import org.assertj.swing.core.BasicRobot
import org.assertj.swing.core.Robot
import org.assertj.swing.core.matcher.JButtonMatcher.withText
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager
import org.assertj.swing.finder.WindowFinder.findFrame
import org.assertj.swing.fixture.FrameFixture
import org.assertj.swing.launcher.ApplicationLauncher.application
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.slf4j.LoggerFactory

class FirstSwingTest {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var noticesWindow: FrameFixture? = null
    private var rob: Robot? = null

    // is called from @Before setUp()
    @BeforeEach
    fun setUp() {
        log.debug("setUp()")
        application(WegeFrei::class.java).start()

        rob = BasicRobot.robotWithCurrentAwtHierarchy()
        assertNotNull(rob)
        //rob?.waitForIdle()
        //Thread.sleep(3000)
        noticesWindow = findFrame(NoticesFrameMatcher()).using(rob)
    }

    @Test
    fun click_on_new_notice_window_opens() {
        assertNotNull(noticesWindow)
        noticesWindow?.button(withText("neue Meldung erfassen"))?.click()

        val noticeWindow = findFrame(NewNoticeFrameMatcher()).using(rob)
        noticeWindow?.button(withText("Abbrechen"))?.click()
    }

    @Test
    fun click_on_address_button_nothing_happens_in_gui() {
        assertNotNull(noticesWindow)
        noticesWindow?.button(withText("Adressbuch"))?.click()
    }

    @AfterEach
    fun tearDown() {
        // Cleans up any used resources (keyboard, mouse, open windows and ScreenLock) used by this robot.
        noticesWindow?.cleanUp()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        @BeforeAll
        @JvmStatic
        fun setUpOnce() {
            LOG.debug("setUpOnce()")
            FailOnThreadViolationRepaintManager.install()
        }

        @AfterAll
        @JvmStatic
        fun tearDownOnce() {
            LOG.debug("tearDownOnce()")
            FailOnThreadViolationRepaintManager.uninstall()
        }
    }

}