package com.zara.automation.core.driver;

import org.openqa.selenium.WebDriver;

/**
 * Lifecycle contract for WebDriver management across the test session.
 * Abstracts thread-local storage details from callers.
 */
public interface IDriverManager {

    /**
     * Initialises a new WebDriver for the given browser type and binds it
     * to the current thread.
     *
     * @param browserType browser identifier (e.g. {@code "chrome"})
     */
    void initDriver(String browserType);

    /**
     * Returns the WebDriver bound to the current thread.
     *
     * @return active {@link WebDriver} instance
     * @throws IllegalStateException if {@link #initDriver} has not been called on this thread
     */
    WebDriver getDriver();

    /**
     * Quits the WebDriver bound to the current thread and removes the thread-local binding.
     * Safe to call even if no driver is active.
     */
    void quitDriver();

    /**
     * @return {@code true} if a WebDriver is currently bound to this thread
     */
    boolean isDriverActive();
}
