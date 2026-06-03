# Roadmap

The direction for the PetStore API test framework. See the [README](README.md) for what the suite covers today.

---

## Delivered

- Maven project scaffolding with a layered package design (`config`, `client`, `context`, `models`, `testdata`, `tests`).
- `ConfigManager` — thread-safe singleton with environment-variable override for portable configuration.
- `PetStoreClient` — Builder-pattern `RequestSpecification` factory.
- `Pet` model with Lombok (including nested `Category` and `Tag`).
- `TestContext` — JUnit 5 extension providing a fresh client per test.
- Full CRUD coverage: GET, POST, PUT, DELETE.
- Negative testing — 404 assertions for non-existent and deleted pets.
- JSON Schema contract validation via `matchesJsonSchemaInClasspath`.
- Field-level assertions through typed deserialization (`extract().as(Pet.class)`).
- End-to-end lifecycle test: POST → GET → PUT → GET → DELETE → GET 404, with schema and field checks at each step.
- Tag-based test filtering (`mvn test -Dgroups=smoke`).
- Allure reporting wired end-to-end with request/response attachments.

---

## Planned

- **Richer Allure metadata** — add `@Step`, `@Description`, and `@Severity` annotations for a more informative report.
- **Parameterized status coverage** — exercise `available` / `pending` / `sold` via `@ParameterizedTest` (`@ValueSource` / `@CsvSource`).
- **`/pet/findByStatus` query tests** — assert on list responses using `hasItem` / `everyItem` matchers.
- **Nested JsonPath assertions** — e.g. `body("category.name", equalTo("dogs"))`.

---

## Backlog

- **CI pipeline** — GitHub Actions workflow running `mvn test` on push, publishing the Allure report.
- **Cucumber branch** — port a few tests to Gherkin + step definitions for a BDD comparison.

---

## Known Limitations

The public PetStore sandbox is a shared demo service and does not strictly enforce its own contract — it accepts some malformed payloads and returns success. As a result:

- Pet IDs are randomized per test to reduce collisions with other users of the sandbox; a dedicated environment would remove this concern entirely.
- Schema- and field-level assertions in this suite reflect the rigor of the test framework rather than guarantees enforced by the server.
