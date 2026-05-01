package com.zara.automation.pages;

import com.zara.automation.core.base.BasePage;
import com.zara.automation.pages.locators.ProductDetailPageLocators;
import io.qameta.allure.Step;

/**
 * Page Object for a Zara product detail page (PDP).
 * Provides name/price/description queries and the add-to-cart action.
 */
public class ProductDetailPage extends BasePage {

    /**
     * Creates a ProductDetailPage, waits for the page to finish loading, then probes
     * for the product name element to confirm the PDP is ready.
     */
    public ProductDetailPage() {
        super();
        waitForPageToLoad();
        try { findPresent(ProductDetailPageLocators.PRODUCT_NAME); } catch (Exception ignored) {}
        log.info("ProductDetailPage initialised — URL: {}", driver.getCurrentUrl());
    }

    /** @return {@code true} when the product name heading is visible on the page */
    @Override
    public boolean isLoaded() {
        return isDisplayed(ProductDetailPageLocators.PRODUCT_NAME);
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    /** @return product name text from the detail page header */
    public String getProductName() {
        return getText(ProductDetailPageLocators.PRODUCT_NAME);
    }

    /** @return main price text displayed on the product detail page */
    public String getProductPrice() {
        return getText(ProductDetailPageLocators.PRODUCT_PRICE);
    }

    /** @return product description text */
    public String getDescription() {
        return getText(ProductDetailPageLocators.DESCRIPTION);
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    /**
     * Clicks 'Add to cart', selects the first available size, dismisses the
     * smart-size dialog if it appears, then navigates to the cart page.
     *
     * @return new CartPage instance
     */
    @Step("Add product to cart and navigate to cart page")
    public CartPage addToCartAndGoToCart() {
        log.info("Clicking 'Add to cart' button — size drawer will open");
        click(ProductDetailPageLocators.ADD_TO_CART_BTN);
        log.info("Selecting first available size from drawer");
        click(ProductDetailPageLocators.SIZE_OPTION);
        log.info("Waiting for nav-to-cart or smart-size dialog");
        waitForEitherVisible(ProductDetailPageLocators.SMART_SIZE_DISMISS, ProductDetailPageLocators.VIEW_CART_BTN);
        if (isDisplayed(ProductDetailPageLocators.SMART_SIZE_DISMISS)) {
            log.info("Smart-size dialog — clicking 'No, thanks'");
            find(ProductDetailPageLocators.SMART_SIZE_DISMISS).click();
        }
        // jsClick bypasses the add-to-cart notification overlay that intercepts regular clicks
        log.info("Clicking 'View cart'");
        jsClick(ProductDetailPageLocators.VIEW_CART_BTN);
        return new CartPage();
    }
}
