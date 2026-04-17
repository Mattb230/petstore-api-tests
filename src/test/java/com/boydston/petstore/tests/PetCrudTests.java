package com.boydston.petstore.tests;

import com.boydston.petstore.context.TestContext;
import com.boydston.petstore.models.Pet;
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

    @BeforeEach
    void createTestPet(TestInfo testInfo){
        if (testInfo.getTags().contains(SKIP_SETUP)) return;
        Pet pet = Pet.builder()
                .id(testPetId)
                .name("Otto")
                .photoUrls(List.of("https://example.com/otto.jpg"))
                .status("available")
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
                .name("Otto")
                .photoUrls(List.of("https://example.com/otto.jpg"))
                .status("available")
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
                .body(matchesJsonSchemaInClasspath("schemas/pet-schema.json"));
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
        assertThat(returned.getName(), equalTo("Otto"));
        assertThat(returned.getStatus(), equalTo("available"));
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
                .name("Otto Updated")
                .photoUrls(List.of("https://example.com/otto-updated.jpg"))
                .status("pending")
                .build();

        given(ctx.getSpec())
                .body(updated)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("Otto Updated"))
                .body("status", equalTo("pending"));
    }
}
