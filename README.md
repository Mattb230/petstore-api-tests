# PetStore API Test Framework

A REST API test framework targeting the public [Swagger PetStore API](https://petstore.swagger.io).

**Stack:** Java 17 · REST Assured · JUnit 5 · Maven · Allure · JSON Schema Validation

---

## Project Structure

```
src/test/java/com/boydston/petstore/
├── client/       # PetStoreClient — builds the shared RestAssured RequestSpecification
├── config/       # ConfigManager — loads config.properties with env var override support
├── context/      # TestContext — JUnit 5 extension; wires up a fresh client per test
├── models/       # Pet — Lombok-based request/response model
└── tests/        # PetCrudTests — CRUD test suite
```

---

## Setup

### 1. Clone and install dependencies

```bash
git clone https://github.com/mattb230/petstore-api-tests.git
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

# Run a specific tag group
mvn test -Dgroups=smoke
```

---

## Reporting

Tests are instrumented with Allure. After a test run, generate and open the report:

```bash
mvn allure:report
mvn allure:serve
```

---

## Design Notes

- **`ConfigManager`** is a thread-safe singleton that resolves config values in priority order: environment variable → `config.properties`. This makes the suite portable across local dev and CI environments without code changes.
- **`TestContext`** is a JUnit 5 extension (`BeforeEachCallback`) that builds a fresh `RequestSpecification` before each test, keeping test state isolated.
- **`@Tag("skipSetup")`** on a test bypasses the `@BeforeEach` pet-creation step for tests that need to manage their own preconditions (e.g., the POST test).
