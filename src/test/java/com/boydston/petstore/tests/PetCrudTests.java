package com.boydston.petstore.tests;

import com.boydston.petstore.context.TestContext;
import com.boydston.petstore.models.Pet;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static io.restassured.RestAssured.given;

public class PetCrudTests {

    private Long testPetId = (long) (Math.random() * 1_000_000_000);

    @RegisterExtension
    static TestContext ctx = new TestContext();

    @BeforeEach
    void createTestPet(TestInfo testInfo){
        if (testInfo.getTags().contains("skipSetup")) return;
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
    void deleteTestPet(){
        given(ctx.getSpec())
                .when()
                    .delete("/pet/" + testPetId)
                .then()
                    .statusCode(200);
    }

    @Test
    void shouldReturn200WhenGettingExistingPet() {
        given(ctx.getSpec())
                .when()
                    .get("/pet/" + testPetId)
                .then()
                    .statusCode(200);
    }

    @Test
    @Tag("skipSetup")
    void shouldReturn200WhenPostingNewPet() {
        Pet pet = Pet.builder()
                .id(testPetId)
                .name("Otto")
                .photoUrls(List.of("https://example.com/otto.jpg"))
                .status("Available")
                .build();

        given(ctx.getSpec())
                .body(pet)
                .when()
                    .post("/pet")
                .then()
                    .statusCode(200);
    }
}
