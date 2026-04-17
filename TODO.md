# Project 2 — Java / RestAssured / JUnit 5 TODO Tracker
_Last updated: Session 7_

---

## 🔴 In Progress (Session 7)

- [ ] Wire `pet-schema.json` into `shouldReturn200WhenGettingExistingPet` using `matchesJsonSchemaInClasspath`
- [ ] Walk through what schema validation is actually asserting (field presence, types, enum values)

---

## 🟡 Up Next

- [ ] Add remaining CRUD tests:
    - [ ] PUT to update a pet's status
    - [ ] DELETE pet
    - [ ] Assert 404 after deletion
- [ ] Build the chained end-to-end flow test: POST → GET → PUT → GET again → DELETE → assert 404
- [ ] Add cleanup to `shouldReturn200WhenCreatingNewPet` — currently leaks the pet it creates (known gap from Session 6)
- [ ] Extract `"skipSetup"` magic string to a constant: `static final String SKIP_SETUP = "skipSetup"`

---

## 🟢 Completed

- [x] **Session 5:** Maven project structure scaffolded, `ConfigManager`, `PetStoreClient` (Builder pattern), `Pet` model (Lombok)
- [x] **Session 6:** `TestContext.java` — JUnit 5 extension model, `BeforeEachCallback`, `@RegisterExtension`
- [x] **Session 6:** `PetCrudTests.java` — `@BeforeEach`/`@AfterEach` lifecycle, `@Tag("skipSetup")` pattern, `TestInfo` injection
- [x] **Session 6:** Moved `config.properties` to `src/test/resources` (Maven classpath fix)
- [x] **Session 6:** `.gitignore` updated to exclude `allure-results/` and `allure-report/`
- [x] **Session 6:** GET existing pet test passing against PetStore sandbox
- [x] **Session 6:** POST new pet test passing

---

## 🔵 Post-Project 2 Backloggit 

- [ ] **Cucumber branch** — port 2–3 tests to Gherkin + step definitions
    - Goals: Cucumber interview coverage, git branching practice, before/after portfolio comparison
- [ ] **CI pipeline** — add GitHub Actions workflow to run `mvn test` on push
- [ ] **Allure reporting** — confirm report generates cleanly, add to README
- [ ] **README** — document project structure, how to run, stack decisions

---

## 📝 Known Issues / Tech Debt

| Item | Notes |
|------|-------|
| `shouldReturn200WhenCreatingNewPet` — no cleanup | Creates a pet with `newPetId`, never deletes it. Needs a scoped `@AfterEach` or manual delete at end of test. |
| `"skipSetup"` magic string | Typo is a silent failure. Extract to `static final String SKIP_SETUP`. |
| `@AfterEach` deletes even if `@BeforeEach` was skipped | Harmless on PetStore sandbox (DELETE of nonexistent ID returns 404 or 200), but not logically clean. |