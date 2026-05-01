package com.zara.automation.tests;

import com.zara.automation.core.base.BaseTest;
import com.zara.automation.core.driver.DriverManager;
import com.zara.automation.pages.CartPage;
import com.zara.automation.pages.HomePage;
import com.zara.automation.pages.LoginPage;
import com.zara.automation.pages.ProductDetailPage;
import com.zara.automation.pages.SearchResultsPage;
import com.zara.automation.utils.ExcelUtils;
import com.zara.automation.utils.FileWriterUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full end-to-end shopping flow split into ordered, independently named steps.
 *
 * Lifecycle:
 *   @BeforeAll  → opens browser once for all steps
 *   @BeforeEach → logs which step is starting  (browser NOT re-opened)
 *   @AfterEach  → logs which step finished
 *   @AfterAll   → closes browser once after all steps
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("e2e")
@Feature("Zara E2E Shopping")
@DisplayName("Zara Full Shopping Flow")
class ZaraShoppingFlowTest extends BaseTest {

    private static final int DATA_ROW   = 0;
    private static final int COL_SORT   = 0;
    private static final int COL_GOMLEK = 1;

    // ── Shared state passed between steps ─────────────────────────────────────
    private HomePage          homePage;
    private SearchResultsPage resultsPage;
    private ProductDetailPage pdp;
    private String            productPrice;
    private CartPage          cartPage;

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    /** Opens Chrome and navigates to the Zara base URL once for the entire test class. */
    @BeforeAll
    void openBrowser() {
        FileWriterUtils.clearProductInfo();
        initBrowser();
    }

    /** Quits the browser after all ordered steps have completed. */
    @AfterAll
    void closeBrowser() {
        DriverManager.getInstance().quitDriver();
    }

    /**
     * No-op override — browser lifecycle is handled by {@link #openBrowser()}.
     * Only logs the step name so the console output remains readable.
     *
     * @param testInfo JUnit-injected metadata about the running test
     */
    @Override
    @BeforeEach
    public void setUp(TestInfo testInfo) {
        log.info("Starting  [{}]", testInfo.getDisplayName());
        // intentionally no-op: browser is managed by @BeforeAll / @AfterAll
    }

    /**
     * No-op override — driver is kept alive until {@link #closeBrowser()}.
     * Only logs completion of the step.
     *
     * @param testInfo JUnit-injected metadata about the running test
     */
    @Override
    @AfterEach
    public void tearDown(TestInfo testInfo) {
        log.info("Finished  [{}]", testInfo.getDisplayName());
        // intentionally no-op
    }

    // ── Steps ──────────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Story("Authentication & Navigation")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Opens www.zara.com/tr, logs in with test credentials, navigates to Men → View All")
    @DisplayName("Step 1-3: Open site, log in, navigate to Men → View All")
    void loginAndNavigateToMen() {
        homePage = new HomePage();

        String email    = config.get("TEST_USER_EMAIL",    "test@example.com");
        String password = config.get("TEST_USER_PASSWORD", "TestPassword123!");

        LoginPage loginPage = homePage.navigateToLogin();
        // isLoaded() is asserted inside LoginPage constructor — no duplicate check needed

        homePage = loginPage.loginAs(email, password);
        log.info("Login successful");

        homePage.navigateToMenSection();
        assertTrue(
                homePage.getCurrentUrl().contains("erkek") || homePage.getCurrentUrl().contains("man"),
                "URL should contain men's category. Actual URL: " + homePage.getCurrentUrl()
        );
    }

    @Test
    @Order(2)
    @Story("Search")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Types 'şort' from Excel A1, clears it, types 'gömlek' from Excel B1, presses Enter")
    @DisplayName("Step 4-7: Type 'şort', clear, type 'gömlek', submit search")
    void searchFlow() {
        String firstTerm = ExcelUtils.read(DATA_ROW, COL_SORT);
        log.info("First search term from Excel: '{}'", firstTerm);
        homePage.openSearchBox();
        homePage.typeInSearchBox(firstTerm);

        homePage.clearSearchBox();
        log.info("Search box cleared");

        String secondTerm = ExcelUtils.read(DATA_ROW, COL_GOMLEK);
        log.info("Second search term from Excel: '{}'", secondTerm);
        homePage.typeInSearchBox(secondTerm);

        resultsPage = homePage.submitSearch();
        // isLoaded() is asserted inside SearchResultsPage constructor — no duplicate check needed
        assertTrue(resultsPage.hasResults(), "There should be results for '" + secondTerm + "'");
        log.info("Total {} results found", resultsPage.getResultCount());
    }

    @Test
    @Order(3)
    @Story("Product Selection")
    @Severity(SeverityLevel.NORMAL)
    @Description("Picks a random product from results and saves its name and price to a txt file")
    @DisplayName("Step 8-9: Select random product and save info to file")
    void selectProductAndSaveInfo() {
        pdp = resultsPage.openRandomProduct();
        // isLoaded() is asserted inside ProductDetailPage constructor — no duplicate check needed

        String name = pdp.getProductName();
        productPrice = pdp.getProductPrice();
        log.info("Selected product: '{}' — Price: {}", name, productPrice);

        String file = FileWriterUtils.writeProductInfo(name, productPrice);
        log.info("Product info saved: {}", file);
    }

    @Test
    @Order(4)
    @Story("Cart — Add & Price Verification")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adds the product to cart and asserts cart price matches the product page price")
    @DisplayName("Step 10-11: Add to cart and verify price matches product page")
    void addToCartAndVerifyPrice() {
        cartPage = pdp.addToCartAndGoToCart();
        // isLoaded() is asserted inside CartPage constructor — no duplicate check needed
        assertTrue(cartPage.getItemCount() > 0, "Cart should have at least 1 item");

        String cartPrice = cartPage.getFirstItemPrice();
        log.info("Product page price : {}", productPrice);
        log.info("Cart price         : {}", cartPrice);

        assertEquals(
                normalizePrice(productPrice), normalizePrice(cartPrice),
                "Product page price and cart price must match!"
                + "\n  Product page : " + productPrice
                + "\n  Cart         : " + cartPrice
        );
        log.info("Prices match");
    }

    @Test
    @Order(5)
    @Story("Cart — Quantity")
    @Severity(SeverityLevel.NORMAL)
    @Description("Increases the item quantity and verifies it equals 2")
    @DisplayName("Step 12: Increase quantity and verify it equals 2")
    void increaseQuantity() {
        cartPage.increaseFirstItemQuantity();

        int qty = cartPage.getFirstItemQuantity();
        log.info("Current quantity: {}", qty);

        assertEquals(2, qty, "Quantity should be 2 after increase, but got " + qty);
        log.info("Quantity confirmed as 2");
    }

    @Test
    @Order(6)
    @Story("Cart — Remove")
    @Severity(SeverityLevel.NORMAL)
    @Description("Removes the item from cart and verifies the cart is empty")
    @DisplayName("Step 13: Remove item and verify cart is empty")
    void removeItemAndVerifyCartEmpty() {
        cartPage.removeFirstItem();
        assertTrue(cartPage.isCartEmpty(), "Cart should be empty after removing the item");
        log.info("Cart confirmed as empty");
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private String normalizePrice(String raw) {
        return raw.replaceAll("[^0-9,]", "").trim();
    }
}
