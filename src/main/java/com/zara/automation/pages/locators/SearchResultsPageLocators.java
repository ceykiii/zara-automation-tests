package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

/**
 * Locators for the Zara search-results grid page.
 */
public final class SearchResultsPageLocators {

    private SearchResultsPageLocators() {}

    /** Each product tile card in the search-results grid. */
    public static final By PRODUCT_CARD    = By.cssSelector(".product-grid-product");
    /** Counter element showing the total number of search results. */
    public static final By RESULTS_COUNT  = By.cssSelector("[data-qa-id='search-grid-results-count']");
    /** Message shown when the search query returns zero products. */
    public static final By NO_RESULTS_MSG = By.cssSelector("[data-qa-id='search-no-results']");
    /** Product name label inside a product card tile. */
    public static final By PRODUCT_NAME   = By.cssSelector(".product-grid-product-info__name");
}
