package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

public final class ProductDetailPageLocators {

    private ProductDetailPageLocators() {}

    public static final By PRODUCT_NAME       = By.cssSelector("[data-qa-qualifier='product-detail-info-name']");
    public static final By PRODUCT_PRICE      = By.cssSelector(".money-amount__main");
    public static final By ADD_TO_CART_BTN    = By.cssSelector("[data-qa-action='add-to-cart']");
    public static final By SIZE_OPTION        = By.cssSelector(".size-selector-sizes-size--enabled .size-selector-sizes-size__button");
    public static final By SMART_SIZE_DISMISS = By.cssSelector("[data-qa-id='zds-alert-dialog-cancel-button']");
    public static final By VIEW_CART_BTN       = By.cssSelector("[data-qa-action='nav-to-cart']");
    public static final By DESCRIPTION        = By.cssSelector(".product-detail-description__content");
}
