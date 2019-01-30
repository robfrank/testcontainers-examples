package it.robfrank.testcontainers

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.remote.DesiredCapabilities
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL
import java.io.File

/**
 * Simple example of plain Selenium usage.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KotlinSeleniumTest {

    private val chrome: BrowserWebDriverContainer<Nothing> = BrowserWebDriverContainer<Nothing>().apply {
        withDesiredCapabilities(DesiredCapabilities.chrome())
        withRecordingMode(RECORD_ALL, File("target"))

        start()
    }

    @Test
    fun simplePlainSeleniumTest() {
        val driver = chrome.webDriver

        driver.get("https://wikipedia.org")
        val searchInput = driver.findElementByName("search")

        searchInput.sendKeys("Rick Astley")
        searchInput.submit()

        val otherPage = driver.findElementByPartialLinkText("Rickrolling")
        otherPage.click()

        val expectedTextFound = driver.findElementsByCssSelector("p")
                .asSequence()
                .any { element -> "meme" in element.text }

        assertThat(expectedTextFound)
                .`as`("The word 'meme' is found on a page about rickrolling")
                .isTrue()
    }
}