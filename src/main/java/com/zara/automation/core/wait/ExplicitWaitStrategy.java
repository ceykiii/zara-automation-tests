package com.zara.automation.core.wait;

import com.zara.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * {@link IWaitStrategy} implementation backed by Selenium's {@link WebDriverWait}.
 * Timeout defaults to {@code EXPLICIT_WAIT_SECONDS} from {@code .env}, overridable per-instance.
 */
public class ExplicitWaitStrategy implements IWaitStrategy {

    private final int timeoutSeconds;

    /**
     * Creates a strategy using the {@code EXPLICIT_WAIT_SECONDS} config value (default 15 s).
     */
    public ExplicitWaitStrategy() {
        this.timeoutSeconds = ConfigReader.getInstance().getInt("EXPLICIT_WAIT_SECONDS", 15);
    }

    /**
     * Creates a strategy with a custom timeout.
     *
     * @param timeoutSeconds maximum wait duration in seconds
     */
    public ExplicitWaitStrategy(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public WebElement waitForPresence(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    @Override
    public WebElement waitForVisibility(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Override
    public WebElement waitForClickability(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    @Override
    public boolean waitForInvisibility(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    private WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }
}
