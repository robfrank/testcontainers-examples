package it.robfrank.testcontainers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openqa.selenium.remote.DesiredCapabilities
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File

/**
 * Simple example of plain Selenium usage.
 */
@Testcontainers
class KotlinSeleniumTest {

    @Container
    private val chrome: BrowserWebDriverContainer<Nothing> = BrowserWebDriverContainer<Nothing>()
        .apply {
            withCapabilities(DesiredCapabilities.chrome())
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
