package com.zara.automation.core.exception;

/**
 * Thrown when test data cannot be read or written — for example, a missing Excel file,
 * an out-of-range cell reference, or a failure to write the product-info output file.
 */
public class TestDataException extends AutomationException {

    /**
     * @param message description of the data problem
     */
    public TestDataException(String message) {
        super(message);
    }

    /**
     * @param message description of the data problem
     * @param cause   the underlying I/O or parse exception
     */
    public TestDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
