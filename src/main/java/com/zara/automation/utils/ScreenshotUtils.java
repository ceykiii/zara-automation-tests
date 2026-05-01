package com.zara.automation.utils;

import com.zara.automation.config.ConfigReader;
import com.zara.automation.core.exception.AutomationException;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR =
            ConfigReader.getInstance().get("SCREENSHOT_DIR", "target/screenshots");
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private ScreenshotUtils() {}

    /**
     * Captures a PNG screenshot, saves it to {@code target/screenshots/} AND attaches
     * it to the Allure report as an inline image.
     *
     * @param driver   active WebDriver instance
     * @param testName used to build the file name (special characters are sanitised)
     * @return absolute path of the saved file
     * @throws AutomationException if the file cannot be written to disk
     */
    public static String takeScreenshot(WebDriver driver, String testName) {
        attachToAllure(driver);

        String timestamp     = LocalDateTime.now().format(TIMESTAMP_FMT);
        String sanitizedName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
        Path dir             = Paths.get(SCREENSHOT_DIR);
        Path destination     = dir.resolve(sanitizedName + "_" + timestamp + ".png");

        try {
            Files.createDirectories(dir);
            Path source = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            log.info("Screenshot → {}", destination.toAbsolutePath());
            return destination.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Screenshot failed: {}", e.getMessage());
            throw new AutomationException("Failed to save screenshot for test: " + testName, e);
        }
    }

    /**
     * Captures the current browser viewport as a PNG byte array and attaches it
     * to the Allure report via the {@link Attachment} annotation.
     * Called automatically from {@link #takeScreenshot} on every test failure.
     *
     * @param driver active WebDriver capable of taking screenshots
     * @return PNG image bytes consumed by the Allure listener
     */
    @Attachment(value = "Failure Screenshot", type = "image/png")
    private static byte[] attachToAllure(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
