package com.boydston.petstore.client;

import com.boydston.petstore.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class PetStoreClient {

    private final ConfigManager config;
    private boolean loggingEnabled = false;

    public PetStoreClient() {
        this.config = ConfigManager.getInstance();
    }

    public PetStoreClient withLogging() {
        this.loggingEnabled = true;
        return this;
    }

    public RequestSpecification build() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("api_key", config.getApiKey())
                .addFilter(new AllureRestAssured());

        if (loggingEnabled) {
            builder.log(LogDetail.ALL);
        }

        return builder.build();
    }
}