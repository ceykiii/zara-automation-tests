package com.zara.automation.core.extension;

import com.zara.automation.core.driver.DriverManager;
import com.zara.automation.core.exception.AutomationException;
import com.zara.automation.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * JUnit 5 {@link TestWatcher} extension that captures a screenshot whenever a test fails.
 * Registered automatically via {@code @ExtendWith} in {@link com.zara.automation.core.base.BaseTest}.
 * The screenshot is saved to disk and attached to the Allure report.
 */
public class ScreenshotOnFailureExtension implements TestWatcher {

    private static final Logger log = LogManager.getLogger(ScreenshotOnFailureExtension.class);

    /**
     * Called by JUnit 5 after a test method throws an exception.
     * Captures a screenshot if a WebDriver is active on the current thread; skips silently otherwise.
     *
     * @param context JUnit extension context containing the test display name
     * @param cause   the exception that caused the test to fail
     */
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (!DriverManager.getInstance().isDriverActive()) {
            log.warn("No active driver — skipping failure screenshot for: {}", context.getDisplayName());
            return;
        }
        try {
            String path = ScreenshotUtils.takeScreenshot(
                    DriverManager.getInstance().getDriver(),
                    context.getDisplayName()
            );
            log.info("Failure screenshot saved: {}", path);
        } catch (AutomationException e) {
            log.error("Could not capture failure screenshot: {}", e.getMessage());
        }
    }
}
