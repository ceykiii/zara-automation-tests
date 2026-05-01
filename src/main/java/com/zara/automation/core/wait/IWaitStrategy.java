package com.zara.automation.core.wait;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Strategy contract for element-level wait operations.
 * Implementations may use explicit waits, fluent waits, or custom polling.
 */
public interface IWaitStrategy {

    /**
     * Waits until the element is present in the DOM (not necessarily visible).
     *
     * @param driver  active WebDriver
     * @param locator element locator
     * @return the located {@link WebElement}
     */
    WebElement waitForPresence(WebDriver driver, By locator);

    /**
     * Waits until the element is present <em>and</em> visible on the page.
     *
     * @param driver  active WebDriver
     * @param locator element locator
     * @return the visible {@link WebElement}
     */
    WebElement waitForVisibility(WebDriver driver, By locator);

    /**
     * Waits until the element is visible <em>and</em> enabled for interaction.
     *
     * @param driver  active WebDriver
     * @param locator element locator
     * @return the clickable {@link WebElement}
     */
    WebElement waitForClickability(WebDriver driver, By locator);

    /**
     * Waits until the element is no longer visible (or absent from the DOM).
     *
     * @param driver  active WebDriver
     * @param locator element locator
     * @return {@code true} when the element is no longer visible
     */
    boolean waitForInvisibility(WebDriver driver, By locator);
}
