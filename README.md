# Zara E2E Selenium Framework

End-to-end test automation framework built with Java, Selenium 4, JUnit 5, and Allure.  
Covers the full shopping journey on Zara Turkey — login, search, product selection, and cart management.

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Build | Apache Maven | 3.x |
| Test Framework | JUnit 5 | 5.10.2 |
| Browser Automation | Selenium WebDriver | 4.20.0 |
| Driver Management | WebDriverManager | 5.8.0 |
| Reporting | Allure | 2.27.0 |
| Logging | Log4j2 | 2.23.1 |
| Config Management | dotenv-java | 3.0.0 |
| Excel Reading | Apache POI | 5.2.5 |

---

## Project Structure

```
zara-e2e-selenium-framework/
├── src/
│   ├── main/java/com/zara/automation/
│   │   ├── config/
│   │   │   └── ConfigReader.java              # .env loader, Singleton
│   │   ├── core/
│   │   │   ├── base/BasePage.java             # Base class for all page objects
│   │   │   ├── driver/
│   │   │   │   ├── DriverManager.java         # ThreadLocal WebDriver
│   │   │   │   └── factory/                   # Chrome factory + provider
│   │   │   ├── exception/                     # Custom exception hierarchy
│   │   │   └── wait/ExplicitWaitStrategy.java # WebDriverWait wrapper
│   │   ├── pages/
│   │   │   ├── HomePage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── SearchResultsPage.java
│   │   │   ├── ProductDetailPage.java
│   │   │   ├── CartPage.java
│   │   │   └── locators/                      # By definitions, separate from pages
│   │   └── utils/
│   │       ├── ExcelUtils.java                # .xlsx reader (Apache POI)
│   │       ├── FileWriterUtils.java           # product-info.txt writer
│   │       └── ScreenshotUtils.java           # Screenshot capture + Allure attachment
│   ├── test/java/com/zara/automation/
│   │   ├── core/
│   │   │   ├── base/BaseTest.java             # Browser lifecycle, cookie injection
│   │   │   └── extension/
│   │   │       └── ScreenshotOnFailureExtension.java  # Auto-screenshot on failure
│   │   └── tests/
│   │       └── ZaraShoppingFlowTest.java      # Main E2E test class
│   └── test/resources/
│       └── test-data/
│           ├── search-terms.xlsx              # Search terms (şort, gömlek)
│           └── product-info.txt              # Test output (auto-generated)
├── .env.example                               # Config template
├── pom.xml
└── README.md
```

---

## Setup

### Prerequisites

- Java 17+
- Maven 3.6+
- Google Chrome (any version — WebDriverManager handles the driver automatically)

### Steps

```bash
# 1. Clone the repository
git clone <repo-url>
cd zara-e2e-selenium-framework

# 2. Create the .env file
cp .env.example .env
```

Fill in your credentials in `.env`:

```properties
BASE_URL=https://www.zara.com/tr/
BROWSER=chrome
BROWSER_HEADLESS=false
TEST_USER_EMAIL=your@email.com
TEST_USER_PASSWORD=yourpassword
```

---

## Running Tests

```bash
# Run all tests
mvn clean test

# Run in headless mode (for CI/CD)
mvn clean test -DBROWSER_HEADLESS=true

# Generate Allure report
mvn allure:report

# Open report in browser
mvn allure:serve
```

---

## Test Flow

`ZaraShoppingFlowTest` runs 6 ordered steps in a single shared browser session:

| # | Step | Description |
|---|---|---|
| 1 | `loginAndNavigateToMen` | Log in to the site and navigate to the Men section |
| 2 | `searchFlow` | Read from Excel → type "şort", clear → type "gömlek", submit |
| 3 | `selectProductAndSaveInfo` | Pick a random product, capture name & price, write to `product-info.txt` |
| 4 | `addToCartAndVerifyPrice` | Add to cart, assert price matches the product detail page |
| 5 | `increaseQuantity` | Increase quantity to 2 and verify |
| 6 | `removeItemAndVerifyCartEmpty` | Remove the item and assert the cart is empty |

---

## Configuration Reference

| Variable | Default | Description |
|---|---|---|
| `BASE_URL` | `https://www.zara.com/tr/` | Target site URL |
| `BROWSER` | `chrome` | Browser type |
| `BROWSER_HEADLESS` | `false` | Run without a visible browser window |
| `BROWSER_LOCALE` | `tr-TR` | Browser language setting |
| `EXPLICIT_WAIT_SECONDS` | `15` | Element wait timeout |
| `PAGE_LOAD_TIMEOUT_SECONDS` | `30` | Page load timeout |
| `TEST_USER_EMAIL` | — | Login email |
| `TEST_USER_PASSWORD` | — | Login password |
| `SCREENSHOT_DIR` | `target/screenshots` | Failure screenshot directory |
| `TEST_OUTPUT_DIR` | `target/test-output` | `product-info.txt` output directory |

---

## Architecture

### Design Patterns

- **Page Object Model** — Each page has its own class; locators live in separate classes
- **Template Method** — `BasePage` provides common interactions; pages only implement business logic
- **Factory** — `DriverFactoryProvider` → `ChromeDriverFactory`; adding new browsers is straightforward
- **Strategy** — `IWaitStrategy` / `ExplicitWaitStrategy`; wait behavior is swappable
- **Singleton (per-thread)** — `DriverManager` uses ThreadLocal for parallel test support

### Exception Hierarchy

```
AutomationException (base)
├── DriverException
├── PageLoadException
├── ElementInteractionException
└── TestDataException
```

---

## Reporting

After tests run, generate the Allure report:

```bash
mvn allure:report
# Output: target/site/allure-maven-plugin/index.html
```

The report shows every `@Step`, severity levels, feature/story labels, and failure screenshots attached automatically.

---

## Logs

| File | Content |
|---|---|
| `target/logs/automation.log` | All DEBUG+ logs (rolling, max 10 MB / 7 days) |
| `target/logs/error.log` | ERROR-level logs only (max 5 MB) |

---

## CI/CD

No pipeline file is included, but the framework is CI/CD-ready out of the box:

```bash
# Example GitHub Actions / Jenkins command
export BROWSER_HEADLESS=true
export TEST_USER_EMAIL=${{ secrets.ZARA_EMAIL }}
export TEST_USER_PASSWORD=${{ secrets.ZARA_PASSWORD }}
mvn clean test allure:report
```

System environment variables override `.env` values — secrets stay out of source control.
