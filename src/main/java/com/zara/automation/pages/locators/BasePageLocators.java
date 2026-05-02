package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

/**
 * Locators shared across all Zara pages via the global header.
 * Elements here are present on every page and are used directly from {@link com.zara.automation.core.base.BasePage}.
 */
public final class BasePageLocators {

    private BasePageLocators() {}

    /** Search icon in the global header that opens the search overlay. */
    public static final By SEARCH_ICON  = By.cssSelector("[data-qa-id='header-search-text-link']");
    /** Text input inside the search overlay (visible after clicking the search icon). */
    public static final By SEARCH_INPUT = By.cssSelector("#search-home-form-combo-input");
    /** Shopping bag icon in the global header that navigates to the cart page. */
    public static final By CART_ICON    = By.cssSelector("[data-qa-qualifier='header-shoppingbag']");
}
