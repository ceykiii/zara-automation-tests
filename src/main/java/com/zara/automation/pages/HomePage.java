package com.zara.automation.pages;

import com.zara.automation.core.base.BasePage;
import com.zara.automation.pages.locators.BasePageLocators;
import com.zara.automation.pages.locators.HomePageLocators;
import io.qameta.allure.Step;

/**
 * Page Object for the Zara home page. Entry point for login, navigation,
 * search, and cart access from the global header.
 */
public class HomePage extends BasePage {

    /** Creates a HomePage backed by the currently active thread-local driver. */
    public HomePage() {
        super();
        waitForPageToLoad();
        log.info("HomePage initialised — URL: {}", driver.getCurrentUrl());
    }

    /** @return {@code true} when the global header search icon is visible */
    @Override
    public boolean isLoaded() {
        return isDisplayed(BasePageLocators.SEARCH_ICON);
    }

    /**
     * Accepts the cookie consent banner if it is visible.
     *
     * @return this HomePage instance for method chaining
     */
    public HomePage acceptCookies() {
        if (isDisplayed(HomePageLocators.COOKIE_ACCEPT)) {
            log.info("Accepting cookie consent");
            click(HomePageLocators.COOKIE_ACCEPT);
            waitForInvisibility(HomePageLocators.COOKIE_BANNER);
        }
        return this;
    }

    /**
     * Navigates to the login page via the header login link.
     *
     * @return new LoginPage instance
     */
    @Step("Navigate to login page")
    public LoginPage navigateToLogin() {
        log.info("Clicking 'Login' link");
        hoverAndClick(HomePageLocators.LOGIN_LINK);
        return new LoginPage();
    }

    /**
     * Opens the top navigation menu, clicks the Men tab, then clicks "View All"
     * to land on the men's catalogue listing page.
     */
    @Step("Open navigation menu → Men → View All")
    public void navigateToMenSection() {
        log.info("Opening navigation menu");
        click(HomePageLocators.MEN_MENU_TRIGGER);
        log.info("Clicking men's tab");
        click(HomePageLocators.MEN_ERKEK_TAB);
        log.info("Clicking 'View All'");
        click(HomePageLocators.MEN_VIEW_ALL);
        waitForPageToLoad();
        log.info("Men's catalogue loaded: {}", driver.getCurrentUrl());
    }

    /**
     * Opens the search box, types the query and submits.
     *
     * @param query search term to enter
     * @return new SearchResultsPage instance
     */
    public SearchResultsPage searchFor(String query) {
        log.info("Searching for: '{}'", query);
        openSearchBox();
        typeInSearchBox(query);
        return submitSearch();
    }

    /**
     * @return true if the main navigation bar is visible
     */
    public boolean isNavigationVisible() {
        return isDisplayed(HomePageLocators.MAIN_NAV);
    }

    /**
     * Navigates to the cart page via the header cart icon.
     *
     * @return new CartPage instance
     */
    public CartPage navigateToCart() {
        return goToCartPage();
    }
}
