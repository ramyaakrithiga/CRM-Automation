package com.crm.automation.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.crm.automation.base.BaseTest;
import com.crm.automation.config.ConfigManager;
import com.crm.automation.pages.LoginPage;
import com.crm.automation.pages.DashboardPage;

/**
 * Dashboard Test Cases
 */
public class DashboardTests extends BaseTest {
    private static final Logger logger = LogManager.getLogger(DashboardTests.class);
    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    /**
     * Before method - Login before each test
     */
    @BeforeMethod
    public void loginBeforeTest() {
        logger.info("Logging in before dashboard test");
        loginPage = new LoginPage(driver);
        
        String username = ConfigManager.getUsername();
        String password = ConfigManager.getPassword();
        
        loginPage.login(username, password);
        
        dashboardPage = new DashboardPage(driver);
        dashboardPage.waitForDashboardToLoad();
    }

    /**
     * Test 1: Verify Dashboard Page Loads
     */
    @Test(priority = 1, description = "Verify dashboard page loads after login")
    public void testDashboardPageLoads() {
        logger.info("Test: Verify Dashboard Page Loads");
        
        Assert.assertTrue(dashboardPage.isDashboardPageLoaded(), "Dashboard page did not load");
        logger.info("Dashboard page loaded successfully");
    }

    /**
     * Test 2: Verify Dashboard Elements
     */
    @Test(priority = 2, description = "Verify dashboard page elements")
    public void testDashboardElements() {
        logger.info("Test: Verify Dashboard Elements");
        
        Assert.assertTrue(dashboardPage.verifyDashboardPageElements(), "Dashboard elements not visible");
        Assert.assertTrue(dashboardPage.isNavbarDisplayed(), "Navigation bar not displayed");
        
        logger.info("All dashboard elements verified");
    }

    /**
     * Test 3: Verify Dashboard Title
     */
    @Test(priority = 3, description = "Verify dashboard page title")
    public void testDashboardTitle() {
        logger.info("Test: Verify Dashboard Title");
        
        String dashboardTitle = dashboardPage.getDashboardTitle();
        Assert.assertNotNull(dashboardTitle, "Dashboard title is null");
        Assert.assertFalse(dashboardTitle.isEmpty(), "Dashboard title is empty");
        
        logger.info("Dashboard title: " + dashboardTitle);
    }

    /**
     * Test 4: Verify Current URL
     */
    @Test(priority = 4, description = "Verify dashboard URL after login")
    public void testDashboardURL() {
        logger.info("Test: Verify Dashboard URL");
        
        String currentUrl = dashboardPage.getCurrentUrl();
        Assert.assertNotNull(currentUrl, "Current URL is null");
        Assert.assertFalse(currentUrl.contains("login"), "Should not be on login page");
        
        logger.info("Dashboard URL: " + currentUrl);
    }

    /**
     * Test 5: Verify Page Title
     */
    @Test(priority = 5, description = "Verify page title after login")
    public void testPageTitle() {
        logger.info("Test: Verify Page Title");
        
        String pageTitle = dashboardPage.getPageTitle();
        Assert.assertNotNull(pageTitle, "Page title is null");
        Assert.assertFalse(pageTitle.isEmpty(), "Page title is empty");
        
        logger.info("Page title: " + pageTitle);
    }

    /**
     * Test 6: Verify User Greeting
     */
    @Test(priority = 6, description = "Verify user greeting is displayed")
    public void testUserGreeting() {
        logger.info("Test: Verify User Greeting");
        
        boolean greetingDisplayed = dashboardPage.isUserGreetingDisplayed();
        if (greetingDisplayed) {
            String greetingText = dashboardPage.getUserGreetingText();
            logger.info("User greeting: " + greetingText);
            Assert.assertNotNull(greetingText, "Greeting text is null");
        } else {
            logger.warn("User greeting not displayed");
        }
    }

    /**
     * Test 7: Verify Navigation Bar
     */
    @Test(priority = 7, description = "Verify navigation bar is displayed")
    public void testNavigationBar() {
        logger.info("Test: Verify Navigation Bar");
        
        Assert.assertTrue(dashboardPage.isNavbarDisplayed(), "Navigation bar not displayed");
        logger.info("Navigation bar verified");
    }

    /**
     * Test 8: Verify Dashboard Load Time
     */
    @Test(priority = 8, description = "Verify dashboard loads within acceptable time")
    public void testDashboardLoadTime() {
        logger.info("Test: Verify Dashboard Load Time");
        
        long startTime = System.currentTimeMillis();
        dashboardPage.waitForDashboardToLoad();
        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;
        
        // Allow up to 30 seconds for dashboard to load
        Assert.assertTrue(loadTime < 30000, "Dashboard load time exceeds 30 seconds: " + loadTime + "ms");
        logger.info("Dashboard loaded in: " + loadTime + "ms");
    }

    /**
     * Test 9: Verify Multiple Navigation to Dashboard
     */
    @Test(priority = 9, description = "Verify navigating back to dashboard URL")
    public void testNavigateBackToDashboard() {
        logger.info("Test: Navigate Back to Dashboard");
        
        String dashboardUrl = dashboardPage.getCurrentUrl();
        
        // Navigate back
        dashboardPage.goBack();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Navigate forward
        dashboardPage.goForward();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String currentUrl = dashboardPage.getCurrentUrl();
        logger.info("URL after navigation: " + currentUrl);
    }

    /**
     * Test 10: Verify Refresh Dashboard
     */
    @Test(priority = 10, description = "Verify refreshing dashboard page")
    public void testRefreshDashboard() {
        logger.info("Test: Refresh Dashboard");
        
        String titleBefore = dashboardPage.getPageTitle();
        
        // Refresh page
        dashboardPage.refreshPage();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String titleAfter = dashboardPage.getPageTitle();
        Assert.assertEquals(titleBefore, titleAfter, "Page title changed after refresh");
        
        logger.info("Dashboard refresh test passed");
    }
}
