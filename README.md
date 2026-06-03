<!--
  DRAFT README — review the items marked "CONFIRM" before committing.
  These are details I couldn't verify against the live repo (GitHub API was
  rate-limited), so reconcile them against your actual files:
    1. The CI badge workflow filename (line below the title).
    2. The exact file names under tests/ and fixtures/ in the structure tree.
    3. The "Known Defect Found" section — keep it ONLY if you actually encoded
       a SauceDemo bug as a test; otherwise delete that whole section.
  Delete this comment block when you're done.
-->

# Playwright SauceDemo E2E Suite

<!-- CONFIRM workflow filename, then this badge goes live -->
[![CI](https://github.com/Mattb230/playwright-sauce-demo/actions/workflows/CONFIRM-WORKFLOW-FILE.yml/badge.svg)](https://github.com/Mattb230/playwright-sauce-demo/actions)

An end-to-end UI test suite for [SauceDemo](https://www.saucedemo.com), built with Playwright and TypeScript using the Page Object Model. The suite covers the full purchase flow — login, inventory, cart, and checkout — with assertions that verify data integrity as it moves across pages, not just that pages load.

**Stack:** TypeScript · Playwright · Page Object Model · GitHub Actions

---

## Project Structure

<!-- CONFIRM the exact file names under tests/ and fixtures/ -->
```
.
├── pages/                  # Page Objects — one class per screen
│   ├── LoginPage.ts        #   login form interactions and submission
│   ├── InventoryPage.ts    #   product list, add-to-cart, getItemPrice() lookup
│   ├── CartPage.ts         #   cart contents and line-item verification
│   └── CheckoutPage.ts     #   checkout steps, totals, and order completion
├── fixtures/               # Custom Playwright fixtures
│   └── auth.fixture.ts     #   authenticated fixture — tests start logged in
├── test-data/              # Test data, decoupled from test logic
│   ├── users.ts            #   SauceDemo user accounts
│   └── products.ts         #   product names and expected prices
├── tests/                  # Spec files
│   └── checkout.spec.ts    #   full E2E purchase flow with data-integrity checks
├── .github/workflows/      # CI — runs the suite on push
├── playwright.config.ts
└── package.json
```

---

## Prerequisites

- Node.js 18+ (20 LTS recommended)
- npm

---

## Setup

```bash
git clone https://github.com/Mattb230/playwright-sauce-demo.git
cd playwright-sauce-demo
npm install
npx playwright install   # downloads the browser binaries
```

---

## Running Tests

```bash
# Run the full suite (headless)
npx playwright test

# Run with the UI mode for debugging
npx playwright test --ui

# Run a single spec
npx playwright test tests/checkout.spec.ts

# Run in headed mode to watch the browser
npx playwright test --headed
```

---

## Reporting

Playwright generates an HTML report after each run. Open the most recent one with:

```bash
npx playwright show-report
```

<!-- Optional but recommended: add a screenshot of the HTML report here.
     Save it under docs/ and reference it like:
     ![Playwright HTML report](docs/report.png) -->

---

## Test Coverage

- **End-to-end checkout** — a complete purchase from login through order confirmation.
- **Data integrity across pages** — verifies that a product's price on the inventory page is the same price shown in the cart and reflected in the checkout total, catching regressions where data is dropped or transformed between steps.
- **Cart accuracy** — confirms the items added on the inventory page match exactly what appears in the cart.

<!-- CONFIRM / EDIT: add or remove rows above to match your actual specs. -->

---

## Known Defect Found

<!--
  KEEP THIS SECTION ONLY IF TRUE. SauceDemo ships intentional bugs (e.g. the
  problem_user account renders broken images, certain users hit sort/checkout
  issues). If you encoded one as a test, describe it here — it's one of the
  strongest signals in the whole repo. If you did NOT, delete this section.
-->

While building the suite I encountered a defect in the application under test: _[describe the behaviour — what you expected, what SauceDemo actually did, and which user/flow triggers it]_. It's captured in `[spec file]` as a documented test so the regression is tracked rather than worked around.

---

## Design Notes

- **Page Object Model.** Each screen is a class that owns its locators and exposes intent-level methods (e.g. `addItemToCart`, `checkout`). Specs read as user actions, and a UI change touches one Page Object instead of every test.
- **`data-test` selectors as the primary strategy.** SauceDemo exposes `data-test` attributes specifically for automation. Selecting on those instead of CSS classes or text keeps the suite stable against styling and copy changes — the locators break only when behaviour actually changes.
- **Authenticated fixture.** A custom fixture handles login once and hands tests an already-authenticated context, so specs that aren't testing login don't repeat it. This keeps tests focused and faster.
- **Scoped price lookup (`getItemPrice`).** `InventoryPage.getItemPrice(productName)` scopes a locator to a single product row, which is what makes the cross-page price-integrity assertions possible.
- **Test data separated from logic.** User accounts and product data live in `test-data/`, so the same specs can run against different data without editing test code.

---

## CI

The suite runs automatically on push via GitHub Actions (see `.github/workflows/`). <!-- CONFIRM: add a sentence on what the workflow does — e.g. installs deps, installs browsers, runs the suite headless on every push to master. -->
