package com.zara.automation.pages.locators;

import org.openqa.selenium.By;

public final class LoginPageLocators {

    private LoginPageLocators() {}

    public static final By EMAIL_INPUT      = By.cssSelector("[data-qa-input-qualifier='logonId']");
    public static final By SUBMIT_BTN       = By.cssSelector("[data-qa-id='logon-form-submit']");
    public static final By PASSWORD_LINK    = By.xpath("//span[contains(normalize-space(),'Şifre ile giriş yapın')]");
    public static final By PASSWORD_INPUT   = By.cssSelector("[data-qa-input-qualifier='password']");
    public static final By ERROR_MSG        = By.cssSelector("[data-qa-id='logon-error']");
    /** "Ok" button inside the intermittent Zara error dialog ("Maalesef bir sorun oluştu"). */
    public static final By ERROR_DIALOG_OK  = By.cssSelector("dialog[role='alertdialog'] .mds-button--primary");
}
