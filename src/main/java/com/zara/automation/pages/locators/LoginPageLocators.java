package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

/**
 * Locators for the Zara login / authentication pages.
 * Covers all variants Zara may serve: email entry, OTP code entry, and password entry.
 */
public final class LoginPageLocators {

    private LoginPageLocators() {}

    /** Email address input on the initial login form. */
    public static final By EMAIL_INPUT   = By.cssSelector("[data-qa-input-qualifier='logonId']");
    /** Primary submit / continue button shared by the email and password forms. */
    public static final By SUBMIT_BTN    = By.cssSelector("[data-qa-id='logon-form-submit']");
    /** "Şifre ile giriş yapın" link that switches from OTP/code entry to password entry. */
    public static final By PASSWORD_LINK = By.xpath("//span[contains(normalize-space(),'Şifre ile giriş yapın')]");
    /** Password input on the password-entry form. */
    public static final By PASSWORD_INPUT = By.cssSelector("[data-qa-input-qualifier='password']");
    /** Inline error message shown for invalid credentials. */
    public static final By ERROR_MSG     = By.cssSelector("[data-qa-id='logon-error']");
    /** "Ok" button inside the intermittent Zara error dialog ("Maalesef bir sorun oluştu"). */
    public static final By ERROR_DIALOG_OK = By.cssSelector("dialog[role='alertdialog'] .mds-button--primary");
}
