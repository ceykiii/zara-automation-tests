package com.zara.automation.core.exception;

/**
 * Base unchecked exception for all framework-level failures.
 *
 * Extend this class for domain-specific exceptions so callers can choose
 * to catch either the specific type or this base type.
 */
public class AutomationException extends RuntimeException {

    /**
     * @param message human-readable description of what went wrong
     */
    public AutomationException(String message) {
        super(message);
    }

    /**
     * @param message human-readable description of what went wrong
     * @param cause   the underlying exception that triggered this one
     */
    public AutomationException(String message, Throwable cause) {
        super(message, cause);
    }
}
