package com.zara.automation.pages;

import com.zara.automation.core.base.BasePage;
import com.zara.automation.pages.locators.LoginPageLocators;
import io.qameta.allure.Step;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the Zara login page.
 * Models the three-step authentication flow: email → password link → password.
 */
public class LoginPage extends BasePage {

    /** Creates a LoginPage backed by the currently active thread-local driver. */
    public LoginPage() {
        super();
        waitForPageToLoad();
        log.info("LoginPage opened — URL: {}", driver.getCurrentUrl());
    }

    /** @return {@code true} when the email input field is visible */
    @Override
    public boolean isLoaded() {
        return isDisplayed(LoginPageLocators.EMAIL_INPUT);
    }

    /**
     * Performs the Zara three-step login flow:
     *  1. Enter email → click "Continue"
     *  2. Click "Sign in with password"
     *  3. Enter password → click "Sign in"
     *
     * @param email    account email address
     * @param password account password
     * @return new HomePage instance after successful login
     */
    @Step("Log in as '{email}'")
    public HomePage loginAs(String email, String password) {
        log.info("Step 1: Entering email: {}", email);
        type(LoginPageLocators.EMAIL_INPUT, email);
        log.info("Clicking 'Continue' button");
        click(LoginPageLocators.SUBMIT_BTN);
        handleErrorDialogIfPresent();

        log.info("Step 2: Clicking 'Sign in with password' link");
        click(LoginPageLocators.PASSWORD_LINK);

        log.info("Step 3: Entering password");
        type(LoginPageLocators.PASSWORD_INPUT, password);
        log.info("Clicking 'Sign in' button");
        click(LoginPageLocators.SUBMIT_BTN);
        handleErrorDialogAfterSignIn();

        waitForUrlNotToContain("logon");
        log.info("Login successful — redirected URL: {}", driver.getCurrentUrl());

        return new HomePage();
    }

    /**
     * @return true if a login error message is displayed
     */
    public boolean isErrorDisplayed() {
        return isDisplayed(LoginPageLocators.ERROR_MSG);
    }

    /** After "Devam et": waits up to 6 s for password link or error dialog. */
    private void handleErrorDialogIfPresent() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(6))
                    .until(d -> isDisplayed(LoginPageLocators.PASSWORD_LINK)
                             || isDisplayed(LoginPageLocators.ERROR_DIALOG_OK));
        } catch (Exception ignored) {}
        dismissErrorDialogIfPresent("Devam et");
    }

    /** After "Sign in": waits up to 6 s for URL to leave logon or error dialog. */
    private void handleErrorDialogAfterSignIn() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(6))
                    .until(d -> !d.getCurrentUrl().contains("logon")
                             || isDisplayed(LoginPageLocators.ERROR_DIALOG_OK));
        } catch (Exception ignored) {}
        dismissErrorDialogIfPresent("Sign in");
    }

    private void dismissErrorDialogIfPresent(String step) {
        if (isDisplayed(LoginPageLocators.ERROR_DIALOG_OK)) {
            log.warn("Zara error dialog after '{}' — dismissing and retrying", step);
            jsClick(LoginPageLocators.ERROR_DIALOG_OK);
            waitForDialogGone();
            click(LoginPageLocators.SUBMIT_BTN);
        }
    }

    private void waitForDialogGone() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.invisibilityOfElementLocated(
                            LoginPageLocators.ERROR_DIALOG_OK));
        } catch (Exception ignored) {}
    }
}
