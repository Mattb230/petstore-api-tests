package com.boydston.petstore.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static volatile ConfigManager instance;
    private final Properties properties;

    private static final String CONFIG_FILE = "config.properties";

    private ConfigManager() {
        properties = new Properties();
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new IllegalStateException("Config file not found on classpath: " + CONFIG_FILE);
            }
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config file: " + CONFIG_FILE, e);
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public String getBaseUrl() {
        return getRequired("base.url");
    }

    public String getApiKey() {
        String envKey = System.getenv("PETSTORE_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey.trim();
        }
        return getRequired("api.key");
    }

    public int getRequestTimeoutSeconds() {
        return Integer.parseInt(getRequired("request.timeout.seconds"));
    }

    private String getRequired(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required config key missing: " + key);
        }
        return value.trim();
    }
}