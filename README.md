# PetStore API Test Framework

A REST API test automation framework targeting the public [Swagger PetStore API](https://petstore.swagger.io), built to demonstrate clean, maintainable API test design.

**Stack:** Java 17 · REST Assured · JUnit 5 · Maven · Allure · JSON Schema Validation · Lombok

---

## Highlights

- **Full CRUD coverage** — POST, GET, PUT, and DELETE exercised individually plus a chained end-to-end lifecycle test.
- **Contract testing** — responses validated against a JSON Schema (`pet-schema.json`), not just status codes.
- **Layered design** — config, HTTP client, test context, models, and test data are each isolated in their own package.
- **Environment-portable config** — values resolve from an environment variable first, then a properties file, so the same suite runs locally and in CI with no code changes.
- **Rich reporting** — Allure captures every request/response and renders an interactive HTML report.

---

## Project Structure

```
src/test/
├── java/com/boydston/petstore/
│   ├── client/      # PetStoreClient — builds the shared RestAssured RequestSpecification (Builder pattern)
│   ├── config/      # ConfigManager — thread-safe singleton; loads config with env-var override
│   ├── context/     # TestContext — JUnit 5 extension; wires up a fresh client per test
│   ├── models/      # Pet — Lombok-based request/response model (with Category & Tag)
│   ├── testdata/    # TestData — centralized test constants
│   └── tests/       # PetCrudTests — CRUD, schema, negative, and lifecycle tests
└── resources/
    ├── schemas/                    # pet-schema.json — JSON Schema contract
    ├── config.properties.template  # config template (real config is gitignored)
    └── allure.properties           # Allure results directory config
```

---

## Test Coverage

| Test | What it verifies |
|------|------------------|
| `shouldReturn200WhenGettingExistingPet` | GET returns 200 for an existing pet *(smoke)* |
| `shouldReturn200WhenPostingNewPet` | POST creates a pet and returns 200 |
| `shouldReturn200WhenUpdatingExistingPet` | PUT updates `name` and `status` |
| `shouldReturn404AfterDeletingPet` | DELETE removes a pet; subsequent GET returns 404 |
| `shouldReturn404WhenGettingNonExistentPet` | GET returns 404 for an unknown ID *(smoke, negative)* |
| `shouldReturnValidPetSchemaWhenGettingExistingPet` | Response body conforms to the Pet JSON Schema |
| `shouldReturnCorrectPetDataWhenGettingExistingPet` | Response deserializes to `Pet` with correct field values |
| `shouldCompleteFullPetLifecycle` | End-to-end POST → GET → PUT → GET → DELETE → GET 404, with field and schema assertions at each step |

---

## Setup

### 1. Clone and install dependencies

```bash
git clone https://github.com/Mattb230/petstore-api-tests.git
cd petstore-api-tests
mvn install -DskipTests
```

### 2. Configure credentials

`config.properties` is gitignored and must be created locally before running tests.
A template is provided:

```bash
cp src/test/resources/config.properties.template src/test/resources/config.properties
```

Then open `config.properties` and replace `YOUR_API_KEY_HERE` with your API key.
For the public Swagger PetStore, the demo key is `special-key`.

**Alternatively**, set the `PETSTORE_API_KEY` environment variable — it takes precedence over the properties file:

```bash
export PETSTORE_API_KEY=special-key
```

---

## Running Tests

```bash
# Run all tests
mvn test

# Run only the smoke subset
mvn test -Dgroups=smoke
```

---

## Reporting

Tests are instrumented with Allure. After a test run, generate and open the report:

```bash
mvn allure:report   # generate static report under target/site/allure-maven-plugin
mvn allure:serve    # generate and open the interactive report in a browser
```

---

## Design Notes

- **`ConfigManager`** is a thread-safe (double-checked-locking) singleton that resolves config values in priority order: environment variable → `config.properties`. This keeps the suite portable across local dev and CI without code changes.
- **`PetStoreClient`** uses the Builder pattern to assemble a `RequestSpecification` (base URI, JSON content type, API key header, Allure filter, optional logging), keeping HTTP setup out of the tests.
- **`TestContext`** is a JUnit 5 extension (`BeforeEachCallback`) that builds a fresh `RequestSpecification` before each test, keeping test state isolated.
- **`@Tag("skipSetup")`** bypasses the `@BeforeEach` pet-creation step for tests that manage their own preconditions (e.g. POST, negative, and lifecycle tests).
- **Self-cleaning tests** — lifecycle tests use `try/finally` for best-effort cleanup so a mid-test failure doesn't leak data into the sandbox.

> **Note:** The public PetStore sandbox does not strictly enforce its own contract — it accepts some malformed payloads and is shared across users, so IDs are randomized to reduce collisions. The schema- and field-level assertions here reflect the rigor of the test framework rather than guarantees from the server.
