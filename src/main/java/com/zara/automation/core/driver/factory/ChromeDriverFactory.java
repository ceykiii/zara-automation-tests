package com.zara.automation.core.driver.factory;

import com.zara.automation.config.ConfigReader;
import com.zara.automation.core.driver.IDriverFactory;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * {@link IDriverFactory} implementation for Google Chrome.
 * Uses WebDriverManager to resolve the ChromeDriver binary automatically,
 * and applies options from {@link ConfigReader} (headless mode, locale, etc.).
 */
public class ChromeDriverFactory implements IDriverFactory {

    private static final Logger log = LogManager.getLogger(ChromeDriverFactory.class);
    private final ConfigReader config = ConfigReader.getInstance();

    /**
     * Downloads/resolves the ChromeDriver binary via WebDriverManager, applies
     * {@link #buildOptions()}, and returns a ready-to-use {@link ChromeDriver}.
     *
     * @return fully configured Chrome {@link WebDriver}
     */
    @Override
    public WebDriver createDriver() {
        log.info("Initialising Chrome WebDriver");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(buildOptions());
        log.info("Chrome WebDriver ready");
        return driver;
    }

    /**
     * Assembles {@link ChromeOptions} from config values: headless flag, locale,
     * sandbox/GPU arguments, and automation-detection suppression flags.
     *
     * @return configured {@link ChromeOptions}
     */
    private ChromeOptions buildOptions() {
        ChromeOptions options = new ChromeOptions();
        boolean headless = config.getBoolean("BROWSER_HEADLESS", false);

        if (headless) {
            options.addArguments("--headless=new");
            log.info("Chrome running in headless mode");
        }

        String windowSize = config.get("BROWSER_WINDOW_SIZE", "1920,1080");

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=" + windowSize);
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--lang=" + config.get("BROWSER_LOCALE", "tr-TR"));

        // Sites like Zara hide certain elements when navigator.webdriver=true
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        return options;
    }
}
