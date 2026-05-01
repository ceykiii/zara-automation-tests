package com.zara.automation.core.exception;

/**
 * Thrown when a page fails to reach the expected loaded state within the allotted timeout.
 * Typically raised when {@code isLoaded()} returns {@code false} after navigation.
 */
public class PageLoadException extends AutomationException {

    /**
     * @param pageName simple name of the page that failed to load (e.g. {@code "CartPage"})
     */
    public PageLoadException(String pageName) {
        super("Page did not load as expected: " + pageName);
    }

    /**
     * @param pageName simple name of the page that failed to load
     * @param cause    the underlying timeout or Selenium exception
     */
    public PageLoadException(String pageName, Throwable cause) {
        super("Page did not load as expected: " + pageName, cause);
    }
}
