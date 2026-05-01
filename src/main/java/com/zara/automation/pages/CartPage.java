package com.zara.automation.pages;

import com.zara.automation.core.base.BasePage;
import com.zara.automation.pages.locators.CartPageLocators;
import com.zara.automation.pages.locators.ProductDetailPageLocators;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Zara shopping cart page.
 * Covers cart item queries, quantity management, and checkout navigation.
 */
public class CartPage extends BasePage {

    /** Creates a CartPage, waits for the page to load, and logs the current item count. */
    public CartPage() {
        super();
        waitForPageToLoad();
        log.info("CartPage initialised — items in cart: {}", cartItems().size());
    }

    /**
     * @return {@code true} when at least one cart item row is present,
     *         or the empty-cart state element is visible
     */
    @Override
    public boolean isLoaded() {
        try {
            return !cartItems().isEmpty() || isDisplayed(CartPageLocators.EMPTY_CART_MSG);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    /** @return number of distinct item rows currently in the cart */
    public int getItemCount() {
        return cartItems().size();
    }

    /** @return {@code true} when the empty-cart state element is visible */
    public boolean isCartEmpty() {
        return isDisplayed(CartPageLocators.EMPTY_CART_MSG);
    }

    /** @return cart grand-total price text */
    public String getCartTotal() {
        return getText(CartPageLocators.CART_TOTAL);
    }

    /** @return price text of the first item in the cart */
    public String getFirstItemPrice() {
        String price = getText(CartPageLocators.FIRST_ITEM_PRICE);
        log.info("First item price in cart: '{}'", price);
        return price;
    }

    /**
     * @return quantity of the first item; falls back to 1 if the input is not found
     */
    public int getFirstItemQuantity() {
        try {
            String raw = find(CartPageLocators.FIRST_ITEM_QUANTITY_VALUE).getAttribute("value");
            return Integer.parseInt(raw.trim().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 1;
        }
    }

    /** @return list of product names currently in the cart */
    public List<String> getItemNames() {
        return cartItems().stream()
                .map(item -> getChildText(item, CartPageLocators.ITEM_NAME))
                .collect(Collectors.toList());
    }

    /**
     * @param productName name to look up (case-insensitive)
     * @return {@code true} if any cart item matches the given name
     */
    public boolean containsProduct(String productName) {
        return getItemNames().stream()
                .anyMatch(name -> name.equalsIgnoreCase(productName));
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    /**
     * Increases the quantity of the first cart item by 1.
     * Zara has no in-cart quantity stepper, so this navigates back to the product
     * page, adds the same size again, then navigates directly to the cart URL
     * instead of relying on the flaky add-to-cart notification button.
     *
     * @return this CartPage instance for method chaining
     */
    @Step("Increase first item quantity by 1")
    public CartPage increaseFirstItemQuantity() {
        String cartUrl = driver.getCurrentUrl();

        log.info("Clicking first cart item — navigating to product page");
        click(CartPageLocators.FIRST_ITEM_LINK);
        waitForPageToLoad();

        log.info("Clicking 'Add to cart' button — size drawer opens");
        click(ProductDetailPageLocators.ADD_TO_CART_BTN);
        log.info("Selecting first available size");
        click(ProductDetailPageLocators.SIZE_OPTION);

        // Wait for confirmation that the item was accepted (smart-size or nav-to-cart)
        waitForEitherVisible(ProductDetailPageLocators.SMART_SIZE_DISMISS, ProductDetailPageLocators.VIEW_CART_BTN);
        if (isDisplayed(ProductDetailPageLocators.SMART_SIZE_DISMISS)) {
            log.info("Smart-size dialog — clicking 'No, thanks'");
            click(ProductDetailPageLocators.SMART_SIZE_DISMISS);
        }

        // Navigate directly to cart — avoids the flaky notification overlay
        log.info("Navigating directly to cart: {}", cartUrl);
        driver.get(cartUrl);
        waitForPageToLoad();

        log.info("Returned to cart — quantity: {}", getFirstItemQuantity());
        return this;
    }

    /**
     * Removes the first item from the cart and waits for the UI to reflect the change.
     *
     * @return this CartPage instance for method chaining
     */
    @Step("Remove first item from cart")
    public CartPage removeFirstItem() {
        int countBefore = cartItems().size();
        log.info("Removing first item from cart (current count: {})", countBefore);
        click(CartPageLocators.FIRST_ITEM_DELETE_BTN);

        waitForCondition(d ->
                d.findElements(CartPageLocators.CART_ITEMS).size() < countBefore
                || !d.findElements(CartPageLocators.EMPTY_CART_MSG).isEmpty()
        );
        log.info("Item removed. Cart is now empty: {}", isCartEmpty());
        return this;
    }

    /** Clicks the checkout button. */
    public void proceedToCheckout() {
        log.info("Proceeding to checkout");
        click(CartPageLocators.CHECKOUT_BTN);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Fetches the current list of cart item elements directly from the DOM.
     * Called on every use so the list always reflects the live page state.
     *
     * @return live list of cart item {@link WebElement}s
     */
    private List<WebElement> cartItems() {
        return driver.findElements(CartPageLocators.CART_ITEMS);
    }
}
