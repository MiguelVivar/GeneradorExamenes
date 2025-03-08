package com.examgenerator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static Properties properties;
    
    static {
        properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration: " + e.getMessage());
        }
    }
    
    public static String getUrl() {
        return properties.getProperty("db.url");
    }
    
    public static String getUser() {
        return properties.getProperty("db.user");
    }
    
    public static String getPassword() {
        return properties.getProperty("db.password");
    }
}