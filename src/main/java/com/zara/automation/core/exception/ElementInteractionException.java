package com.zara.automation.core.exception;

/**
 * Thrown when a page-level element interaction fails in an unexpected way —
 * for example when a click, type, or scroll operation raises an unrecoverable error.
 */
public class ElementInteractionException extends AutomationException {

    /**
     * @param action  the action that failed (e.g. {@code "click"}, {@code "type"})
     * @param locator string representation of the target locator
     */
    public ElementInteractionException(String action, String locator) {
        super("Element interaction failed — action='" + action + "' locator=" + locator);
    }

    /**
     * @param action  the action that failed
     * @param locator string representation of the target locator
     * @param cause   the underlying Selenium exception
     */
    public ElementInteractionException(String action, String locator, Throwable cause) {
        super("Element interaction failed — action='" + action + "' locator=" + locator, cause);
    }
}
