package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

public final class HomePageLocators {

    private HomePageLocators() {}

    public static final By COOKIE_ACCEPT = By.id("onetrust-accept-btn-handler");
    public static final By COOKIE_BANNER = By.id("onetrust-banner-sdk");
    public static final By MAIN_NAV      = By.cssSelector(".layout-categories-header");

    public static final By LOGIN_LINK   = By.xpath("(//a[@data-qa-id='layout-desktop-layout-logon-action'])[last()]");
    public static final By MEN_MENU_TRIGGER = By.cssSelector("[data-qa-id='layout-desktop-open-menu-trigger']");
    public static final By MEN_ERKEK_TAB   = By.xpath("//span[@class='layout-categories-category-name'][normalize-space()='ERKEK']");
    public static final By MEN_VIEW_ALL     = By.xpath("//a[@data-qa-action='unfold-category'][.//span[normalize-space()='TÜMÜNÜ GÖR']]");
}
