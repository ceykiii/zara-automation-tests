package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

public final class CartPageLocators {

    private CartPageLocators() {}

    public static final By EMPTY_CART_MSG = By.cssSelector(".shop-cart-view__empty-state");
    public static final By CART_TOTAL     = By.cssSelector(".shop-cart-summary__total .money-amount__main");
    public static final By CART_ITEMS     = By.cssSelector(".shop-cart-item");
    public static final By ITEM_NAME      = By.cssSelector(".shop-cart-item__name");
    public static final By CHECKOUT_BTN   = By.cssSelector("[data-qa-qualifier='shop-cart-checkout-button']");

    public static final By FIRST_ITEM_PRICE             = By.cssSelector(".shop-cart-item:first-child .money-amount__main");
    public static final By FIRST_ITEM_LINK              = By.cssSelector(".shop-cart-item:first-child a.shop-cart-item-image__link");
    public static final By FIRST_ITEM_QUANTITY_VALUE    = By.cssSelector("input.zds-quantity-selector__units");
    public static final By FIRST_ITEM_DELETE_BTN        = By.cssSelector("[data-qa-action='remove-order-item']");
}
