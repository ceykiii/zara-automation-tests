package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

public final class BasePageLocators {

    private BasePageLocators() {}

    public static final By SEARCH_ICON  = By.cssSelector("[data-qa-id='header-search-text-link']");
    public static final By SEARCH_INPUT = By.cssSelector("#search-home-form-combo-input");
    public static final By CART_ICON    = By.cssSelector("[data-qa-qualifier='header-shoppingbag']");
}
