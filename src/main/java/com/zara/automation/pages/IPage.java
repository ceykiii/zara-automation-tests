package com.zara.automation.pages;

/**
 * Common contract for all Page Object classes.
 */
public interface IPage {

    /** @return true when the page's primary content is present and visible */
    boolean isLoaded();

    /** Blocks until {@code document.readyState} equals {@code "complete"}. */
    void waitForPageToLoad();

    /** @return the current browser tab title */
    String getPageTitle();

    /** @return the current browser URL */
    String getCurrentUrl();
}
