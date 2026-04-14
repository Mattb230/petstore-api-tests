package com.boydston.petstore.tests;

import com.boydston.petstore.context.TestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.restassured.RestAssured.given;

public class PetCrudTests {

    @RegisterExtension
    static TestContext ctx = new TestContext();

    @Test
    void shouldReturn200WhenGettingExistingPet() {
        given(ctx.getSpec())
                .when()
                    .get("/pet/1")
                .then()
                    .statusCode(200);
    }
}
