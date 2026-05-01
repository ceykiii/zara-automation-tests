package com.zara.automation.core.driver;

import com.zara.automation.config.ConfigReader;
import com.zara.automation.core.driver.factory.DriverFactoryProvider;
import com.zara.automation.core.exception.DriverException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

/**
 * Per-thread Singleton WebDriver manager.
 *
 * ThreadLocal ensures each parallel test thread owns its own driver instance,
 * preventing race conditions without any external synchronisation.
 */
public final class DriverManager implements IDriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);

    private static final ThreadLocal<WebDriver> driverHolder  = new ThreadLocal<>();
    private static final ThreadLocal<DriverManager> selfHolder = new ThreadLocal<>();

    private final ConfigReader config = ConfigReader.getInstance();

    private DriverManager() {}

    /**
     * Returns the {@link DriverManager} bound to the current thread, creating one if absent.
     *
     * @return thread-local singleton instance
     */
    public static DriverManager getInstance() {
        if (selfHolder.get() == null) {
            selfHolder.set(new DriverManager());
        }
        return selfHolder.get();
    }

    /**
     * Creates a new WebDriver via {@link com.zara.automation.core.driver.factory.DriverFactoryProvider},
     * applies implicit-wait and page-load-timeout from config, maximises the window,
     * and binds the driver to this thread. Quits any previously active driver first.
     *
     * @param browserType browser identifier passed to the factory (e.g. {@code "chrome"})
     */
    @Override
    public void initDriver(String browserType) {
        if (driverHolder.get() != null) {
            log.warn("Driver already active on thread '{}'. Quitting first.", threadName());
            quitDriver();
        }

        WebDriver driver = DriverFactoryProvider.getFactory(browserType).createDriver();

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(config.getInt("IMPLICIT_WAIT_SECONDS", 10)));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(config.getInt("PAGE_LOAD_TIMEOUT_SECONDS", 30)));
        driver.manage().window().maximize();

        driverHolder.set(driver);
        log.info("Driver initialised on thread '{}'", threadName());
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if {@link #initDriver} has not been called on this thread
     */
    @Override
    public WebDriver getDriver() {
        WebDriver driver = driverHolder.get();
        if (driver == null) {
            throw new DriverException(
                    "No active WebDriver on thread '" + threadName() +
                    "'. Call initDriver() before getDriver()."
            );
        }
        return driver;
    }

    /** Quits and removes the driver bound to this thread. No-op if no driver is active. */
    @Override
    public void quitDriver() {
        WebDriver driver = driverHolder.get();
        if (driver != null) {
            driver.quit();
            driverHolder.remove();
            selfHolder.remove();
            log.info("Driver quit on thread '{}'", threadName());
        }
    }

    /** @return {@code true} if a WebDriver is currently bound to this thread */
    @Override
    public boolean isDriverActive() {
        return driverHolder.get() != null;
    }

    /** @return the name of the thread currently executing, used for log messages */
    private static String threadName() {
        return Thread.currentThread().getName();
    }
}
