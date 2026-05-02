package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

/**
 * Locators for the Zara shopping cart page.
 */
public final class CartPageLocators {

    private CartPageLocators() {}

    /** Element displayed when the cart contains no items. */
    public static final By EMPTY_CART_MSG = By.cssSelector(".shop-cart-view__empty-state");
    /** Grand-total price shown in the cart summary section. */
    public static final By CART_TOTAL     = By.cssSelector(".shop-cart-summary__total .money-amount__main");
    /** Each individual item row in the cart list. */
    public static final By CART_ITEMS     = By.cssSelector(".shop-cart-item");
    /** Product name label inside a cart item row. */
    public static final By ITEM_NAME      = By.cssSelector(".shop-cart-item__name");
    /** Checkout button that initiates the purchase flow. */
    public static final By CHECKOUT_BTN   = By.cssSelector("[data-qa-qualifier='shop-cart-checkout-button']");

    /** Price of the first item in the cart. */
    public static final By FIRST_ITEM_PRICE          = By.cssSelector(".shop-cart-item:first-child .money-amount__main");
    /** Link on the first cart item's image — navigates back to the product detail page. */
    public static final By FIRST_ITEM_LINK           = By.cssSelector(".shop-cart-item:first-child a.shop-cart-item-image__link");
    /** Quantity input field of the first cart item. */
    public static final By FIRST_ITEM_QUANTITY_VALUE = By.cssSelector("input.zds-quantity-selector__units");
    /** Delete / remove button for the first cart item. */
    public static final By FIRST_ITEM_DELETE_BTN     = By.cssSelector("[data-qa-action='remove-order-item']");
}
