package com.crm.automation.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Configuration Manager to load properties from config.properties file
 */
public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static Properties properties;

    static {
        loadProperties();
    }

    /**
     * Load properties from config.properties file
     */
    private static void loadProperties() {
        try {
            properties = new Properties();
            String configPath = "src/main/resources/config.properties";
            FileInputStream fileInputStream = new FileInputStream(configPath);
            properties.load(fileInputStream);
            fileInputStream.close();
            logger.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading properties file: " + e.getMessage());
            throw new RuntimeException("Failed to load config.properties file");
        }
    }

    /**
     * Get property value by key
     *
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get property value by key with default value
     *
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Property value or default
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get integer property value
     *
     * @param key Property key
     * @return Integer property value
     */
    public static int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    /**
     * Get boolean property value
     *
     * @param key Property key
     * @return Boolean property value
     */
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    // Configuration getters for common properties
    public static String getApplicationUrl() {
        return getProperty("app.url");
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome").toLowerCase();
    }

    public static boolean isHeadless() {
        return getBooleanProperty("headless");
    }

    public static int getImplicitWait() {
        return getIntProperty("implicit.wait");
    }

    public static int getExplicitWait() {
        return getIntProperty("explicit.wait");
    }

    public static int getPageLoadTimeout() {
        return getIntProperty("page.load.timeout");
    }

    public static String getUsername() {
        return getProperty("username");
    }

    public static String getPassword() {
        return getProperty("password");
    }

    public static String getScreenshotPath() {
        return getProperty("screenshot.path");
    }

    public static boolean isScreenshotOnFailure() {
        return getBooleanProperty("screenshot.on.failure");
    }

    public static String getReportsPath() {
        return getProperty("reports.path");
    }

    public static String getTestDataPath() {
        return getProperty("test.data.path");
    }

    public static int getRetryCount() {
        return getIntProperty("retry.count");
    }
}
