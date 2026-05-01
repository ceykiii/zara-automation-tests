package com.zara.automation.core.driver;

import org.openqa.selenium.WebDriver;

/**
 * Factory contract for creating browser-specific WebDriver instances.
 * Each implementation encapsulates all driver setup details for a single browser.
 */
public interface IDriverFactory {

    /**
     * Creates and returns a fully configured {@link WebDriver} instance.
     *
     * @return ready-to-use WebDriver for the target browser
     */
    WebDriver createDriver();
}
