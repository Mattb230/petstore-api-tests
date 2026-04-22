package com.boydston.petstore.tests;

import com.boydston.petstore.context.TestContext;
import com.boydston.petstore.models.Pet;
import com.boydston.petstore.testdata.TestData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PetCrudTests {
    @RegisterExtension
    static TestContext ctx = new TestContext();

    private Long testPetId = (long) (Math.random() * 1_000_000_000);
    static final String SKIP_SETUP = "skipSetup";
    static final String PET_SCHEMA = "schemas/pet-schema.json";

    @BeforeEach
    void createTestPet(TestInfo testInfo){
        if (testInfo.getTags().contains(SKIP_SETUP)) return;
        Pet pet = Pet.builder()
                .id(testPetId)
                .name(TestData.PET_NAME)
                .photoUrls(List.of(TestData.PET_PHOTO_URL))
                .status(TestData.PET_STATUS_AVAILABLE)
                .build();

        given(ctx.getSpec())
                .body(pet)
                .when()
                    .post("/pet")
                .then()
                    .statusCode(200);
    }

    @AfterEach
    void deleteTestPet(TestInfo testInfo){
        if (testInfo.getTags().contains(SKIP_SETUP)) return;
        given(ctx.getSpec())
                .when()
                    .delete("/pet/" + testPetId)
                .then()
                    .statusCode(200);
    }

    @Test
    @Tag("smoke")
    void shouldReturn200WhenGettingExistingPet() {
        given(ctx.getSpec())
                .when()
                    .get("/pet/" + testPetId)
                .then()
                    .statusCode(200);
    }

    @Test
    @Tag(SKIP_SETUP)
    void shouldReturn200WhenPostingNewPet() {
        Long newPetId = (long) (Math.random() * 1_000_000_000);

        Pet pet = Pet.builder()
                .id(newPetId)
                .name(TestData.PET_NAME)
                .photoUrls(List.of(TestData.PET_PHOTO_URL))
                .status(TestData.PET_STATUS_AVAILABLE)
                .build();

        given(ctx.getSpec())
                .body(pet)
                .when()
                    .post("/pet")
                .then()
                    .statusCode(200);

        // cleanup
        try {
            given(ctx.getSpec())
                    .when()
                        .delete("/pet/" + newPetId)
                    .then()
                        .statusCode(200);
        } catch (Exception e) {
            System.out.println("Cleanup DELETE failed for petId " + newPetId + ": " + e.getMessage());
        }
    }

    @Test
    void shouldReturnValidPetSchemaWhenGettingExistingPet() {
        given(ctx.getSpec())
                .when()
                    .get("/pet/" + testPetId)
                .then()
                    .statusCode(200)
                    .body(matchesJsonSchemaInClasspath(PET_SCHEMA));
    }

    @Test
    void shouldReturnCorrectPetDataWhenGettingExistingPet(){
        Pet returned = given(ctx.getSpec())
                .when()
                .get("/pet/" + testPetId)
                .then()
                    .statusCode(200)
                    .extract()
                    .as(Pet.class);

        assertThat(returned.getId(), equalTo(testPetId));
        assertThat(returned.getName(), equalTo(TestData.PET_NAME));
        assertThat(returned.getStatus(), equalTo(TestData.PET_STATUS_AVAILABLE));
    }

    @Test
    @Tag(SKIP_SETUP)
    @Tag("smoke")
    void shouldReturn404WhenGettingNonExistentPet() {
        long nonExistentId = 999999999999L;

        given(ctx.getSpec())
                .when()
                    .get("/pet/" + nonExistentId)
                .then()
                    .statusCode(404);
    }

    @Test
    void shouldReturn200WhenUpdatingExistingPet() {
        Pet updated = Pet.builder()
                .id(testPetId)
                .name(TestData.PET_NAME_UPDATED)
                .photoUrls(List.of(TestData.PET_PHOTO_URL_UPDATED))
                .status(TestData.PET_STATUS_PENDING)
                .build();

        Pet returned = given(ctx.getSpec())
                .body(updated)
                .when()
                    .put("/pet")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(Pet.class);

        assertThat(returned.getName(), equalTo(TestData.PET_NAME_UPDATED));
        assertThat(returned.getStatus(), equalTo(TestData.PET_STATUS_PENDING));
    }

    @Test
    @Tag(SKIP_SETUP)
    void shouldReturn404AfterDeletingPet(){
        Long newPetId = (long) (Math.random() * 1_000_000_000);

        Pet pet = Pet.builder()
                .id(newPetId)
                .name(TestData.PET_NAME)
                .photoUrls(List.of(TestData.PET_PHOTO_URL))
                .status(TestData.PET_STATUS_AVAILABLE)
                .build();

        given(ctx.getSpec())
                .body(pet)
                .when()
                    .post("/pet")
                .then()
                    .statusCode(200);

        given(ctx.getSpec())
                .when()
                    .delete("/pet/" + newPetId)
                .then()
                    .statusCode(200);

        given(ctx.getSpec())
                .when()
                    .get("/pet/" + newPetId)
                .then()
                    .statusCode(404);
    }

    @Test
    @Tag(SKIP_SETUP)
    void shouldCompleteFullPetLifecycle(){
        Long petId = (long) (Math.random() * 1_000_000_000);

        try {
            Pet initialPetPayload = Pet.builder()
                    .id(petId)
                    .name(TestData.PET_NAME)
                    .photoUrls(List.of(TestData.PET_PHOTO_URL))
                    .status(TestData.PET_STATUS_AVAILABLE)
                    .build();

            // Step 1: POST Pet and assert on response
            given(ctx.getSpec())
                    .body(initialPetPayload)
                    .when()
                        .post("/pet")
                    .then()
                        .statusCode(200)
                        .body("name", equalTo(TestData.PET_NAME))
                        .body("status", equalTo(TestData.PET_STATUS_AVAILABLE));

            // Step 2: GET Pet and verify schema contract and data
            Pet createdPetResponse = given(ctx.getSpec())
                    .when()
                        .get("/pet/" + petId)
                    .then()
                        .statusCode(200)
                        .body(matchesJsonSchemaInClasspath(PET_SCHEMA))
                        .extract()
                        .as(Pet.class);

            assertThat(createdPetResponse.getId(), equalTo(petId));
            assertThat(createdPetResponse.getName(), equalTo(TestData.PET_NAME));
            assertThat(createdPetResponse.getStatus(), equalTo(TestData.PET_STATUS_AVAILABLE));

            Pet updatedPetPayload = Pet.builder()
                    .id(petId)
                    .name(TestData.PET_NAME_UPDATED)
                    .status(TestData.PET_STATUS_PENDING)
                    .build();

            // Step 3: PUT Pet and verify schema contract and updates
            given(ctx.getSpec())
                    .body(updatedPetPayload)
                    .when()
                        .put("/pet")
                    .then()
                        .statusCode(200)
                        .body(matchesJsonSchemaInClasspath(PET_SCHEMA))
                        .body("name", equalTo(TestData.PET_NAME_UPDATED))
                        .body("status", equalTo(TestData.PET_STATUS_PENDING));

            // Step 4: GET Pet and verify updates persisted
            Pet updatedPetResponse = given(ctx.getSpec())
                    .when()
                        .get("/pet/" + petId)
                    .then()
                        .statusCode(200)
                        .extract()
                        .as(Pet.class);

            assertThat(updatedPetResponse.getName(), equalTo(TestData.PET_NAME_UPDATED));
            assertThat(updatedPetResponse.getStatus(), equalTo(TestData.PET_STATUS_PENDING));

            // Step 5: DELETE — remove the pet
            given(ctx.getSpec())
                    .when()
                        .delete("/pet/" + petId)
                    .then()
                        .statusCode(200);

            // Step 6: GET — verify deletion
            given(ctx.getSpec())
                    .when()
                        .get("/pet/" + petId)
                    .then()
                        .statusCode(404);
        } finally {
            // Best-effort cleanup if the test failed before Step 5; no-op if Step 5 already succeeded
            try {
                given(ctx.getSpec()).when().delete("/pet/" + petId);
            } catch (Exception e) {
                System.out.println("Cleanup DELETE failed for petId " + petId + ": " + e.getMessage());
            }
        }
    }
}
