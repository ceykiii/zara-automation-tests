package com.zara.automation.pages;

import com.zara.automation.core.base.BasePage;
import com.zara.automation.pages.locators.SearchResultsPageLocators;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Page Object for the Zara search-results grid.
 * Provides queries over the product list and navigation to individual product pages.
 */
public class SearchResultsPage extends BasePage {

    /**
     * Creates a SearchResultsPage, waits for the DOM to reach readyState, then waits
     * for the first product card to be visible — not just present in the DOM — so that
     * subsequent interactions always hit fully rendered elements.
     */
    public SearchResultsPage() {
        super();
        waitForPageToLoad();
        try { find(SearchResultsPageLocators.PRODUCT_CARD); } catch (Exception ignored) {}
        log.info("SearchResultsPage initialised — URL: {}", driver.getCurrentUrl());
    }

    /**
     * @return {@code true} when the product grid has at least one item,
     *         or the "no results" message is displayed
     */
    @Override
    public boolean isLoaded() {
        try {
            return !productItems().isEmpty() || isDisplayed(SearchResultsPageLocators.NO_RESULTS_MSG);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    /**
     * @return {@code true} if at least one product card is displayed; {@code false} if the
     *         "no results" message is shown or the grid is empty
     */
    public boolean hasResults() {
        return !isDisplayed(SearchResultsPageLocators.NO_RESULTS_MSG) && !productItems().isEmpty();
    }

    /**
     * Reads the result count from the page counter element when available,
     * falling back to the size of the loaded product-card list.
     * Uses a direct DOM lookup (no wait) to avoid a 15-second
     * {@code waitForVisibility} stall when the counter flickers during async loading.
     *
     * @return number of search results
     */
    public int getResultCount() {
        try {
            String raw   = driver.findElement(SearchResultsPageLocators.RESULTS_COUNT).getText();
            int    count = Integer.parseInt(raw.replaceAll("[^0-9]", ""));
            if (count > 0) return count;
        } catch (Exception ignored) {}
        return productItems().size();
    }

    /**
     * Collects the display name of every product card currently rendered in the grid.
     *
     * @return list of product name strings
     */
    public List<String> getAllProductNames() {
        return productItems().stream()
                .map(el -> getChildText(el, SearchResultsPageLocators.PRODUCT_NAME))
                .collect(Collectors.toList());
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /**
     * Clicks the first product card and returns the resulting product detail page.
     *
     * @return {@link ProductDetailPage}
     */
    public ProductDetailPage openFirstProduct() {
        log.info("Opening first product from results");
        scrollIntoViewAndClick(productItems().get(0));
        return new ProductDetailPage();
    }

    /**
     * Clicks the product card at the given zero-based {@code index}.
     *
     * @param index zero-based position in the result grid
     * @return {@link ProductDetailPage}
     */
    public ProductDetailPage openProductAt(int index) {
        log.info("Opening product at index {}", index);
        scrollIntoViewAndClick(productItems().get(index));
        return new ProductDetailPage();
    }

    /**
     * Selects a random product from the currently loaded grid and navigates to its detail page.
     * The product list is fetched once and reused, avoiding a redundant DOM query.
     *
     * @return {@link ProductDetailPage}
     */
    @Step("Select random product from results")
    public ProductDetailPage openRandomProduct() {
        List<WebElement> items = productItems();
        int index = new Random().nextInt(items.size());
        log.info("Selecting random product: index={} out of {} results", index, items.size());
        scrollIntoViewAndClick(items.get(index));
        return new ProductDetailPage();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Fetches the current list of product card elements directly from the DOM.
     * Called on every use so the list always reflects the live page state.
     *
     * @return live list of product card {@link WebElement}s
     */
    private List<WebElement> productItems() {
        return driver.findElements(SearchResultsPageLocators.PRODUCT_CARD);
    }
}
