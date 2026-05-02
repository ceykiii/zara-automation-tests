package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

/**
 * Locators for elements unique to the Zara home page and its global navigation.
 * Header-wide elements shared with all pages are in {@link BasePageLocators}.
 */
public final class HomePageLocators {

    private HomePageLocators() {}

    /** OneTrust "Accept all cookies" button on the consent banner. */
    public static final By COOKIE_ACCEPT    = By.id("onetrust-accept-btn-handler");
    /** OneTrust consent banner container — used to wait for its disappearance. */
    public static final By COOKIE_BANNER    = By.id("onetrust-banner-sdk");
    /** Main category navigation bar visible at the top of every page once loaded. */
    public static final By MAIN_NAV         = By.cssSelector(".layout-categories-header");

    /** Header link that navigates to the login / account page. */
    public static final By LOGIN_LINK       = By.xpath("(//a[@data-qa-id='layout-desktop-layout-logon-action'])[last()]");
    /** Hamburger / category-menu trigger button in the desktop navigation bar. */
    public static final By MEN_MENU_TRIGGER = By.cssSelector("[data-qa-id='layout-desktop-open-menu-trigger']");
    /** "ERKEK" (Men) tab inside the expanded navigation menu. */
    public static final By MEN_ERKEK_TAB    = By.xpath("//span[@class='layout-categories-category-name'][normalize-space()='ERKEK']");
    /** "TÜMÜNÜ GÖR" (View All) link inside the Men category menu. */
    public static final By MEN_VIEW_ALL     = By.xpath("//a[@data-qa-action='unfold-category'][.//span[normalize-space()='TÜMÜNÜ GÖR']]");
}
