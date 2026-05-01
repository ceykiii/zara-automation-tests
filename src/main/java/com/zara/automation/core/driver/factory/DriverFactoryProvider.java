package com.zara.automation.core.driver.factory;

import com.zara.automation.core.driver.IDriverFactory;
import com.zara.automation.core.exception.DriverException;

/**
 * Static factory that maps a browser name string to the correct {@link IDriverFactory}
 * implementation. Add a new {@code else-if} branch here when support for a new browser
 * is introduced — callers never need to change.
 */
public final class DriverFactoryProvider {

    private DriverFactoryProvider() {}

    /**
     * Returns the {@link IDriverFactory} matching the given browser identifier.
     *
     * @param browserType case-insensitive browser name (e.g. {@code "chrome"})
     * @return the corresponding factory instance
     * @throws IllegalArgumentException if no factory exists for {@code browserType}
     */
    public static IDriverFactory getFactory(String browserType) {
        if ("chrome".equalsIgnoreCase(browserType.trim())) {
            return new ChromeDriverFactory();
        }
        throw new DriverException("Unsupported browser: " + browserType);
    }
}
