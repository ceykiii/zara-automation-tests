package com.zara.automation.core.base;

import com.zara.automation.config.ConfigReader;
import com.zara.automation.core.driver.DriverManager;
import com.zara.automation.core.exception.ElementInteractionException;
import com.zara.automation.core.exception.PageLoadException;
import com.zara.automation.core.wait.ExplicitWaitStrategy;
import com.zara.automation.core.wait.IWaitStrategy;
import com.zara.automation.pages.IPage;
import com.zara.automation.pages.locators.BasePageLocators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.qameta.allure.Step;
import java.time.Duration;
import java.util.function.Function;

/**
 * Template-Method base for all Page Objects.
 *
 * Provides a stable API over raw WebDriver so page classes stay declarative.
 * Global header elements (search, cart) live here because they are present
 * on every Zara page — any page object can use them without duplication (DRY).
 *
 * All timeouts are resolved from {@link ConfigReader} at construction time so
 * a single {@code .env} change propagates everywhere without touching code.
 */
public abstract class BasePage implements IPage {

    protected final WebDriver     driver;
    protected final IWaitStrategy waitStrategy;
    protected final Logger        log;

    private final int elementTimeoutSeconds;
    private final int conditionTimeoutSeconds;
    private final int pageLoadTimeoutSeconds;
    private final int dismissTimeoutSeconds;

    /**
     * Creates a page using the default {@link ExplicitWaitStrategy}.
     * Resolves the thread-local driver and loads timeout config.
     */
    protected BasePage() {
        this.driver       = DriverManager.getInstance().getDriver();
        this.waitStrategy = new ExplicitWaitStrategy();
        this.log          = LogManager.getLogger(this.getClass());
        ConfigReader config         = ConfigReader.getInstance();
        this.elementTimeoutSeconds   = config.getInt("EXPLICIT_WAIT_SECONDS",    15);
        this.conditionTimeoutSeconds = config.getInt("CONDITION_WAIT_SECONDS",   15);
        this.pageLoadTimeoutSeconds  = config.getInt("PAGE_LOAD_TIMEOUT_SECONDS", 30);
        this.dismissTimeoutSeconds   = config.getInt("DISMISS_WAIT_SECONDS",      2);
    }

    /**
     * Creates a page with a custom {@link IWaitStrategy} — useful for tests that
     * need a shorter or longer timeout than the default.
     *
     * @param waitStrategy wait strategy to use for all element lookups
     */
    protected BasePage(IWaitStrategy waitStrategy) {
        this.driver       = DriverManager.getInstance().getDriver();
        this.waitStrategy = waitStrategy;
        this.log          = LogManager.getLogger(this.getClass());
        ConfigReader config           = ConfigReader.getInstance();
        this.elementTimeoutSeconds   = config.getInt("EXPLICIT_WAIT_SECONDS",    15);
        this.conditionTimeoutSeconds = config.getInt("CONDITION_WAIT_SECONDS",   15);
        this.pageLoadTimeoutSeconds  = config.getInt("PAGE_LOAD_TIMEOUT_SECONDS", 30);
        this.dismissTimeoutSeconds   = config.getInt("DISMISS_WAIT_SECONDS",      2);
    }

    // ── Finders ──────────────────────────────────────────────────────────────

    /**
     * Waits until the element is present in the DOM (may not be visible).
     *
     * @param locator element locator
     * @return the located {@link WebElement}
     */
    protected WebElement findPresent(By locator) {
        return waitStrategy.waitForPresence(driver, locator);
    }

    /**
     * Waits until the element is present and visible on the page.
     *
     * @param locator element locator
     * @return the visible {@link WebElement}
     */
    protected WebElement find(By locator) {
        return waitStrategy.waitForVisibility(driver, locator);
    }

    /**
     * Waits until the element is visible and enabled for interaction.
     *
     * @param locator element locator
     * @return the clickable {@link WebElement}
     */
    protected WebElement findClickable(By locator) {
        return waitStrategy.waitForClickability(driver, locator);
    }

    // ── Interactions ─────────────────────────────────────────────────────────

    /**
     * Waits for the element to be clickable, then clicks it.
     *
     * @param locator element locator
     * @throws ElementInteractionException if the click fails for any reason
     */
    protected void click(By locator) {
        log.debug("click({})", locator);
        try {
            findClickable(locator).click();
        } catch (ElementInteractionException e) {
            throw e;
        } catch (Exception e) {
            throw new ElementInteractionException("click", locator.toString(), e);
        }
    }

    /**
     * Waits for the given element reference to be clickable, then clicks it.
     * Timeout driven by {@code EXPLICIT_WAIT_SECONDS}.
     *
     * @param element target element
     * @throws ElementInteractionException if the click fails for any reason
     */
    protected void click(WebElement element) {
        log.debug("click(WebElement)");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(elementTimeoutSeconds))
                    .until(ExpectedConditions.elementToBeClickable(element))
                    .click();
        } catch (Exception e) {
            throw new ElementInteractionException("click", element.toString(), e);
        }
    }

    /**
     * Clicks an element via JavaScript — use when a CSS overlay intercepts normal clicks.
     *
     * @param locator element locator
     * @throws ElementInteractionException if the JS click fails
     */
    protected void jsClick(By locator) {
        log.debug("jsClick({})", locator);
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", findPresent(locator));
        } catch (Exception e) {
            throw new ElementInteractionException("jsClick", locator.toString(), e);
        }
    }

    /**
     * Clears the field and types the given text.
     *
     * @param locator element locator
     * @param text    text to enter
     * @throws ElementInteractionException if sending keys fails
     */
    protected void type(By locator, String text) {
        log.debug("type({}, '{}')", locator, text);
        try {
            WebElement el = find(locator);
            el.clear();
            el.sendKeys(text);
        } catch (ElementInteractionException e) {
            throw e;
        } catch (Exception e) {
            throw new ElementInteractionException("type", locator.toString(), e);
        }
    }

    /**
     * Types the text into the field and immediately presses {@code ENTER}.
     *
     * @param locator element locator
     * @param text    text to enter
     */
    protected void typeAndSubmit(By locator, String text) {
        type(locator, text);
        driver.findElement(locator).sendKeys(Keys.ENTER);
    }

    /**
     * Returns the visible text of the element.
     *
     * @param locator element locator
     * @return trimmed visible text
     */
    protected String getText(By locator) {
        return find(locator).getText();
    }

    /**
     * Finds a child element within {@code parent} and returns its visible text.
     *
     * @param parent       parent element
     * @param childLocator locator scoped to {@code parent}
     * @return visible text of the child element
     */
    protected String getChildText(WebElement parent, By childLocator) {
        return parent.findElement(childLocator).getText();
    }

    /**
     * Returns whether the element is currently displayed, without throwing if absent.
     *
     * @param locator element locator
     * @return {@code true} if the element exists and is visible; {@code false} otherwise
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Global header operations (usable from any page) ───────────────────────

    /** Opens the site-wide search box by clicking the search icon. */
    @Step("Open search box")
    public void openSearchBox() {
        log.info("Opening search box");
        find(BasePageLocators.SEARCH_ICON).click();
        find(BasePageLocators.SEARCH_INPUT);
    }

    /**
     * Types {@code text} into the already-open search input.
     *
     * @param text search term to enter
     */
    @Step("Type '{text}' in search box")
    public void typeInSearchBox(String text) {
        log.info("Typing in search box: '{}'", text);
        find(BasePageLocators.SEARCH_INPUT).sendKeys(text);
    }

    /** Clears the search input using both {@code clear()} and a CTRL+A / DELETE chord. */
    @Step("Clear search box")
    public void clearSearchBox() {
        log.info("Clearing search box");
        WebElement input = find(BasePageLocators.SEARCH_INPUT);
        input.clear();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
    }

    /**
     * Presses ENTER in the search input to submit the query.
     *
     * @return new {@link com.zara.automation.pages.SearchResultsPage}
     */
    @Step("Submit search (Enter)")
    public com.zara.automation.pages.SearchResultsPage submitSearch() {
        log.info("Submitting search (Enter)");
        driver.findElement(BasePageLocators.SEARCH_INPUT).sendKeys(Keys.ENTER);
        return new com.zara.automation.pages.SearchResultsPage();
    }

    /**
     * Clicks the global cart icon and returns a new {@link com.zara.automation.pages.CartPage}.
     *
     * @return cart page
     */
    public com.zara.automation.pages.CartPage goToCartPage() {
        log.info("Navigating to cart via header icon");
        click(BasePageLocators.CART_ICON);
        return new com.zara.automation.pages.CartPage();
    }

    // ── Scrolling ─────────────────────────────────────────────────────────────

    /**
     * Scrolls the page so that {@code element} is in view.
     *
     * @param element target element
     */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Scrolls to {@code element}, moves the mouse pointer over it, then clicks.
     *
     * @param element target element
     */
    protected void scrollIntoViewAndClick(WebElement element) {
        scrollToElement(element);
        new Actions(driver).moveToElement(element).click().perform();
    }

    /**
     * Moves the mouse pointer over the element without clicking.
     *
     * @param locator element locator
     */
    protected void hoverOver(By locator) {
        log.debug("hoverOver({})", locator);
        new Actions(driver).moveToElement(find(locator)).perform();
    }

    /**
     * Moves the mouse pointer over the element and clicks.
     *
     * @param locator element locator
     */
    protected void hoverAndClick(By locator) {
        log.debug("hoverAndClick({})", locator);
        new Actions(driver).moveToElement(find(locator)).click().perform();
    }

    /**
     * Delegates to the wait strategy's {@code waitForInvisibility}.
     *
     * @param locator element locator
     * @return {@code true} when the element is no longer visible
     */
    protected boolean waitForInvisibility(By locator) {
        return waitStrategy.waitForInvisibility(driver, locator);
    }

    // ── Condition waits ──────────────────────────────────────────────────────

    /**
     * Blocks until the current URL contains {@code fragment}.
     * Timeout driven by {@code CONDITION_WAIT_SECONDS}.
     *
     * @param fragment expected URL substring
     */
    protected void waitForUrlToContain(String fragment) {
        new WebDriverWait(driver, Duration.ofSeconds(conditionTimeoutSeconds))
                .until(ExpectedConditions.urlContains(fragment));
    }

    /**
     * Blocks until the page title contains {@code fragment}.
     * Timeout driven by {@code CONDITION_WAIT_SECONDS}.
     *
     * @param fragment expected title substring
     */
    protected void waitForTitleToContain(String fragment) {
        new WebDriverWait(driver, Duration.ofSeconds(conditionTimeoutSeconds))
                .until(ExpectedConditions.titleContains(fragment));
    }

    /**
     * Blocks until the current URL no longer contains {@code fragment}.
     * Timeout driven by {@code CONDITION_WAIT_SECONDS}.
     *
     * @param fragment URL substring expected to disappear
     */
    protected void waitForUrlNotToContain(String fragment) {
        new WebDriverWait(driver, Duration.ofSeconds(conditionTimeoutSeconds))
                .until(d -> !d.getCurrentUrl().contains(fragment));
    }

    /**
     * Blocks until the given condition returns {@code true}.
     * Timeout driven by {@code CONDITION_WAIT_SECONDS}.
     *
     * @param condition custom predicate over the driver
     */
    protected void waitForCondition(Function<WebDriver, Boolean> condition) {
        new WebDriverWait(driver, Duration.ofSeconds(conditionTimeoutSeconds)).until(condition);
    }

    /**
     * Clicks the element if it becomes clickable within 2 seconds; silently ignores
     * timeouts — the element was not present or not clickable, which is expected.
     *
     * @param locator element locator
     */
    protected void dismissIfPresent(By locator) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(dismissTimeoutSeconds))
                    .until(ExpectedConditions.elementToBeClickable(locator))
                    .click();
            log.debug("dismissIfPresent: clicked {}", locator);
        } catch (Exception ignored) {}
    }

    /**
     * Blocks until at least one of the two locators becomes visible.
     * Timeout driven by {@code CONDITION_WAIT_SECONDS}.
     * Useful for branching on which overlay/button appears after an action.
     *
     * @param locatorA first candidate locator
     * @param locatorB second candidate locator
     */
    protected void waitForEitherVisible(By locatorA, By locatorB) {
        new WebDriverWait(driver, Duration.ofSeconds(conditionTimeoutSeconds))
                .until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(locatorA),
                        ExpectedConditions.visibilityOfElementLocated(locatorB)
                ));
    }

    // ── IPage ────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Polls {@code document.readyState} until {@code "complete"}.
     * Timeout driven by {@code PAGE_LOAD_TIMEOUT_SECONDS}.
     *
     * @throws PageLoadException if the page does not reach {@code "complete"} in time
     */
    @Override
    public void waitForPageToLoad() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(pageLoadTimeoutSeconds))
                    .until(d -> ((JavascriptExecutor) d)
                            .executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            throw new PageLoadException(getClass().getSimpleName(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getPageTitle() {
        return driver.getTitle();
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
