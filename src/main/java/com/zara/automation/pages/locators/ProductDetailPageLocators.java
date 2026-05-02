package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

/**
 * Locators for the Zara product detail page (PDP).
 */
public final class ProductDetailPageLocators {

    private ProductDetailPageLocators() {}

    /** Heading element that shows the product's display name. */
    public static final By PRODUCT_NAME       = By.cssSelector("[data-qa-qualifier='product-detail-info-name']");
    /** Main price element on the product detail page. */
    public static final By PRODUCT_PRICE      = By.cssSelector(".money-amount__main");
    /** "Add to cart" button that opens the size-selection drawer. */
    public static final By ADD_TO_CART_BTN    = By.cssSelector("[data-qa-action='add-to-cart']");
    /** First enabled size button inside the size-selection drawer. */
    public static final By SIZE_OPTION        = By.cssSelector(".size-selector-sizes-size--enabled .size-selector-sizes-size__button");
    /** Cancel / dismiss button on the smart-size recommendation dialog. */
    public static final By SMART_SIZE_DISMISS = By.cssSelector("[data-qa-id='zds-alert-dialog-cancel-button']");
    /** "View cart" button shown after an item is successfully added to the cart. */
    public static final By VIEW_CART_BTN      = By.cssSelector("[data-qa-action='nav-to-cart']");
    /** Product description text block on the detail page. */
    public static final By DESCRIPTION        = By.cssSelector(".product-detail-description__content");
}
