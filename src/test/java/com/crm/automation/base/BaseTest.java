package com.crm.automation.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import com.crm.automation.config.ConfigManager;
import com.crm.automation.utilities.DriverManager;
import com.crm.automation.utilities.CommonUtils;

/**
 * Base Test class for all test cases
 * Contains setup and teardown methods
 */
public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;

    /**
     * Setup method - runs before each test class
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() {
        logger.info("========== Test Setup Started ==========");
        try {
            // Initialize WebDriver
            driver = DriverManager.initializeDriver();
            CommonUtils.setDriver(driver);

            // Navigate to application URL
            String appUrl = ConfigManager.getApplicationUrl();
            logger.info("Navigating to: " + appUrl);
            driver.navigate().to(appUrl);

            logger.info("========== Test Setup Completed ==========");
        } catch (Exception e) {
            logger.error("Error during test setup: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to setup test", e);
        }
    }

    /**
     * Teardown method - runs after each test class
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        logger.info("========== Test Teardown Started ==========");
        try {
            if (driver != null) {
                // Delete all cookies
                DriverManager.deleteAllCookies();

                // Close the driver
                DriverManager.closeDriver();
                logger.info("WebDriver closed successfully");
            }
            logger.info("========== Test Teardown Completed ==========");
        } catch (Exception e) {
            logger.error("Error during test teardown: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get driver instance
     *
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Take screenshot for debugging
     *
     * @param fileName Screenshot file name
     */
    protected void captureScreenshot(String fileName) {
        logger.info("Capturing screenshot: " + fileName);
        CommonUtils.takeScreenshot(fileName);
    }
}
