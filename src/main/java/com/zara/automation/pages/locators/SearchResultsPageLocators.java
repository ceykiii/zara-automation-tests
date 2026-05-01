package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

public final class SearchResultsPageLocators {

    private SearchResultsPageLocators() {}

    public static final By PRODUCT_CARD    = By.cssSelector(".product-grid-product");
    public static final By RESULTS_COUNT  = By.cssSelector("[data-qa-id='search-grid-results-count']");
    public static final By NO_RESULTS_MSG = By.cssSelector("[data-qa-id='search-no-results']");
    public static final By PRODUCT_NAME   = By.cssSelector(".product-grid-product-info__name");
}
