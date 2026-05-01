package com.zara.automation.core.exception;

/**
 * Thrown when a WebDriver cannot be created, accessed, or is in an invalid state.
 * Examples: unsupported browser type, calling {@code getDriver()} before {@code initDriver()}.
 */
public class DriverException extends AutomationException {

    /**
     * @param message description of the driver problem
     */
    public DriverException(String message) {
        super(message);
    }

    /**
     * @param message description of the driver problem
     * @param cause   the underlying exception (e.g. WebDriverManager failure)
     */
    public DriverException(String message, Throwable cause) {
        super(message, cause);
    }
}
