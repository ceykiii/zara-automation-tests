package com.zara.automation.core.base;

import com.zara.automation.config.ConfigReader;
import com.zara.automation.core.driver.DriverManager;
import com.zara.automation.core.extension.ScreenshotOnFailureExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;

/**
 * Abstract base for all JUnit 5 test classes.
 *
 * Handles browser lifecycle ({@code @BeforeEach}/{@code @AfterEach}), URL navigation,
 * and consent-cookie injection so individual tests start from a clean, popup-free page.
 * Ordered E2E tests can override {@code setUp}/{@code tearDown} as no-ops and call
 * {@link #initBrowser()} once from {@code @BeforeAll} instead.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseTest {

    protected static final Logger log    = LogManager.getLogger(BaseTest.class);
    protected final ConfigReader  config = ConfigReader.getInstance();

    /**
     * Runs before each test method. Logs the test display name and calls {@link #initBrowser()}.
     * Ordered test classes override this as a no-op to share a single browser session.
     *
     * @param testInfo JUnit-injected metadata about the running test
     */
    @BeforeEach
    public void setUp(TestInfo testInfo) {
        log.info("Starting  [{}]", testInfo.getDisplayName());
        initBrowser();
    }

    /**
     * Runs after each test method. Logs completion and quits the WebDriver.
     * Ordered test classes override this as a no-op to keep the browser open across steps.
     *
     * @param testInfo JUnit-injected metadata about the running test
     */
    @AfterEach
    public void tearDown(TestInfo testInfo) {
        log.info("Finished  [{}]", testInfo.getDisplayName());
        DriverManager.getInstance().quitDriver();
    }

    /**
     * Launches Chrome, opens the base URL and injects the consent cookie.
     * Exposed as {@code protected} so ordered test classes can call it from
     * {@code @BeforeAll} without duplicating setup logic.
     */
    protected void initBrowser() {
        String browser = config.get("BROWSER", "chrome");
        String baseUrl  = config.get("BASE_URL", "https://www.zara.com/tr/");
        log.info("  browser={} | url={}", browser, baseUrl);

        DriverManager.getInstance().initDriver(browser);
        WebDriver driver = DriverManager.getInstance().getDriver();

        driver.get(baseUrl);
        waitForPageLoad(driver);

        injectConsentCookies(driver);
        driver.navigate().refresh();
        waitForPageLoad(driver);
        suppressOneTrustOverlays(driver);

        log.info("  Page ready — consent cookie active");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Injects two OneTrust cookies to fully suppress all consent dialogs:
     * <ul>
     *   <li>{@code OptanonAlertBoxClosed} — tells OneTrust the banner was dismissed</li>
     *   <li>{@code OptanonConsent} — records full consent with {@code interactionCount=1}
     *       so OneTrust never shows the preferences panel again</li>
     * </ul>
     *
     * @param driver active WebDriver to inject the cookies into
     */
    private void injectConsentCookies(WebDriver driver) {
        String domain = config.get("COOKIE_DOMAIN", ".zara.com");
        String now    = Instant.now().toString();

        driver.manage().addCookie(new Cookie.Builder("OptanonAlertBoxClosed", now)
                .domain(domain).path("/").isSecure(false).sameSite("Lax").build());

        driver.manage().addCookie(new Cookie.Builder("OptanonConsent",
                "isGpcEnabled=0&interactionCount=1&isIABGlobal=false" +
                "&groups=C0001:1,C0002:1,C0003:1,C0004:1&AwaitingReconsent=false")
                .domain(domain).path("/").isSecure(false).sameSite("Lax").build());

        log.debug("OneTrust consent cookies injected for domain '{}'", domain);
    }

    /**
     * Uses JavaScript to forcefully remove any OneTrust overlay elements that survive
     * the cookie injection — specifically the dark-filter backdrop that can intercept
     * clicks even after the banner was dismissed.
     *
     * @param driver active WebDriver
     */
    private void suppressOneTrustOverlays(WebDriver driver) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "['#onetrust-banner-sdk','#onetrust-pc-sdk','.onetrust-pc-dark-filter']" +
                ".forEach(function(s){var e=document.querySelector(s);if(e)e.remove();});"
            );
            log.debug("OneTrust overlay elements suppressed via JS");
        } catch (Exception ignored) {}
    }

    /**
     * Polls {@code document.readyState} until it equals {@code "complete"}.
     * Timeout driven by {@code PAGE_LOAD_TIMEOUT_SECONDS}.
     * Used during browser setup before {@code PageFactory} elements are available.
     *
     * @param driver active WebDriver to poll
     */
    private void waitForPageLoad(WebDriver driver) {
        int timeout = config.getInt("PAGE_LOAD_TIMEOUT_SECONDS", 30);
        new WebDriverWait(driver, Duration.ofSeconds(timeout))
                .until(d -> ((JavascriptExecutor) d)
                        .executeScript("return document.readyState").equals("complete"));
    }
}
