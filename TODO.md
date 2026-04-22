# Project 2 — Java / RestAssured / JUnit 5 TODO Tracker
_Last updated: Session 8 (complete)_

---

## 🔴 In Progress (Session 8)

_(none — Session 8 scope complete)_

---

## 🟡 Up Next

- [ ] Allure annotations — add `@Step`, `@Description`, `@Severity` to existing tests to make the report portfolio-worthy
- [ ] `@ParameterizedTest` — status enum coverage (`available`, `pending`, `sold`) using `@ValueSource` or `@CsvSource`
- [ ] `/pet/findByStatus` query param tests — exercises list responses (`hasItem`, `everyItem` matchers)
- [ ] RestAssured JsonPath nested assertions — e.g. `.body("category.name", equalTo("dogs"))` for interview coverage

---

## 🟢 Completed

- [x] **Session 5:** Maven project structure scaffolded, `ConfigManager`, `PetStoreClient` (Builder pattern), `Pet` model (Lombok)
- [x] **Session 6:** `TestContext.java` — JUnit 5 extension model, `BeforeEachCallback`, `@RegisterExtension`
- [x] **Session 6:** `PetCrudTests.java` — `@BeforeEach`/`@AfterEach` lifecycle, `@Tag("skipSetup")` pattern, `TestInfo` injection
- [x] **Session 6:** Moved `config.properties` to `src/test/resources` (Maven classpath fix)
- [x] **Session 6:** `.gitignore` updated to exclude `allure-results/` and `allure-report/`
- [x] **Session 6:** GET existing pet test passing against PetStore sandbox
- [x] **Session 6:** POST new pet test passing
- [x] **Session 7:** Schema validation test — `matchesJsonSchemaInClasspath` wired to `pet-schema.json`
- [x] **Session 7:** Field-level assertion test — `.extract().as(Pet.class)` with Hamcrest on `id`, `name`, `status`
- [x] **Session 7:** Negative GET test — 404 on nonexistent pet ID; `@Tag(SKIP_SETUP)` skips lifecycle
- [x] **Session 7:** PUT update test — asserts both `name` and `status` fields on response
- [x] **Session 7:** Allure wired end-to-end — version downgraded to 2.25.0, `allure.properties` committed, `allure-bom` added, `.allure/` gitignored
- [x] **Session 7:** `SKIP_SETUP` extracted to `static final String` constant
- [x] **Session 7:** `shouldReturn200WhenPostingNewPet` — cleanup DELETE added in try/catch
- [x] **Session 8:** DELETE test — `shouldReturn404AfterDeletingPet` — POST → DELETE → GET asserts 404
- [x] **Session 8:** `TestData` constants class — extracted `PET_NAME`, `PET_STATUS_*`, `PET_PHOTO_URL*` from inline string literals
- [x] **Session 8:** End-to-end lifecycle test — `shouldCompleteFullPetLifecycle` — POST → GET → PUT → GET → DELETE → GET 404, with field/schema assertions at each step and `try/finally` cleanup

---

## 🔵 Post-Project 2 Backlog

- [ ] **Cucumber branch** — port 2–3 tests to Gherkin + step definitions
  - Goals: Cucumber interview coverage, git branching practice, before/after portfolio comparison
- [ ] **CI pipeline** — add GitHub Actions workflow to run `mvn test` on push
- [ ] **Allure reporting** — confirm report generates cleanly, add to README
- [ ] **README** — document project structure, how to run, stack decisions

---

## 📝 Known Issues / Tech Debt

| Item | Notes |
|------|-------|
| `@AfterEach` deletes even if `@BeforeEach` was skipped | Harmless on PetStore sandbox (DELETE of nonexistent ID returns 404), but not logically clean. Low priority. |
| PetStore sandbox doesn't enforce its own contract | Accepts malformed payloads, returns 200. Schema validation tests reflect your framework's rigor, not the server's. Worth noting in README. |
| Random ID collision risk | `Math.random() * 1_000_000_000` reduces but doesn't eliminate collision with other sandbox users. Acceptable for portfolio; production would use a dedicated environment. |