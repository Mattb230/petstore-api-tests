package com.boydston.petstore.context;

import com.boydston.petstore.client.PetStoreClient;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestContext implements BeforeEachCallback {

    private RequestSpecification spec;

    @Override
    public void beforeEach(ExtensionContext context) {
        spec = new PetStoreClient()
                .withLogging()
                .build();
    }

    public RequestSpecification getSpec() {
        return spec;
    }
}
