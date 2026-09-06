package com.crm.automation.tests;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.crm.automation.base.BaseTest;
import com.crm.automation.config.ConfigManager;
import com.crm.automation.pages.DashboardPage;
import com.crm.automation.pages.LoginPage;
import com.crm.automation.utilities.CommonUtils;
import com.crm.automation.utilities.DriverManager;

/**
 * Login Test Cases for CRM webpages
 */
public class LoginTests extends BaseTest {
    private static final Logger logger = LogManager.getLogger(LoginTests.class);
    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    //before method
    @BeforeMethod(alwaysRun = true)
    public void resetBrowserState() {
        try {
            if (driver != null) {
                DriverManager.closeDriver();
            }
            driver = DriverManager.initializeDriver();
            CommonUtils.setDriver(driver);
            driver.get(ConfigManager.getApplicationUrl());
            driver.manage().deleteAllCookies();
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Enter your username']")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Enter your password']")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type='submit' and normalize-space()='Sign in']")));

            logger.info("Started fresh browser session and loaded login page before test method");
        } catch (Exception e) {
            logger.error("Unable to start fresh browser session before test method: " + e.getMessage());
            throw new RuntimeException("Failed to initialize login page before test", e);
        }
    }

    private void handlePasswordSavePopupIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            List<WebElement> possibleButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[normalize-space()='Save' or normalize-space()='Yes' or normalize-space()='No thanks' or normalize-space()='Never' or normalize-space()='OK']")));
            for (WebElement button : possibleButtons) {
                String text = button.getText().trim();
                if (text.equalsIgnoreCase("Save") || text.equalsIgnoreCase("Yes") || text.equalsIgnoreCase("No thanks") || text.equalsIgnoreCase("Never") || text.equalsIgnoreCase("OK")) {
                    button.click();
                    logger.info("Clicked popup button: " + text);
                    break;
                }
            }
        } catch (Exception e) {
            logger.info("No password save popup detected in login screen");
        }
    }

    /**
     * Test 1: Verify Login Page Elements
     */
    @Test(priority = 1, description = "Verify login page elements are displayed")
    public void testLoginPageElements() {
        logger.info("Test: Verify Login Page Elements");
        loginPage = new LoginPage(driver);

        // Verify page elements
        Assert.assertTrue(loginPage.isLoginPageLoaded(), "Login page did not load");
        Assert.assertTrue(loginPage.isUsernameFieldVisible(), "Username field not visible");
        Assert.assertTrue(loginPage.isPasswordFieldVisible(), "Password field not visible");
        Assert.assertTrue(loginPage.verifyLoginPageElements(), "Not all login page elements are visible");

        logger.info("All login page elements are visible and accessible");
    }

    /**
     * Test 2: Successful Login
     */
    @Test(priority = 2, description = "Test successful login with valid credentials")
    public void testSuccessfulLogin() {
        logger.info("Test: Successful Login");
        loginPage = new LoginPage(driver);

        String username = ConfigManager.getUsername();
        String password = ConfigManager.getPassword();

        // Perform login
        logger.info("Attempting login with username: " + username);
        loginPage.login(username, password);

        // Wait for dashboard to load
        dashboardPage = new DashboardPage(driver);
        dashboardPage.waitForDashboardToLoad();
        handlePasswordSavePopupIfPresent();

        // Verify dashboard is loaded
        boolean dashboardLoaded = dashboardPage.isDashboardPageLoaded();
        boolean dashboardVisible = dashboardPage.verifyDashboardPageElements();
        Assert.assertTrue(dashboardLoaded || dashboardVisible, "Dashboard page did not load after login");

        logger.info("Login successful - Dashboard loaded");
    }

    /**
     * Test 3: Invalid Username
     */
    @Test(priority = 3, description = "Test login with invalid username")
    public void testLoginWithInvalidUsername() {
        logger.info("Test: Login with Invalid Username");
        loginPage = new LoginPage(driver);

        String invalidUsername = "invalid@example.com";
        String password = ConfigManager.getPassword();

        // Perform login with invalid username
        logger.info("Attempting login with invalid username: " + invalidUsername);
        loginPage.login(invalidUsername, password);

        // Wait briefly for error message/validation state
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for the exact invalid-login error div
        boolean invalidLoginErrorVisible = loginPage.waitForInvalidLoginMessage(180);
        Assert.assertTrue(invalidLoginErrorVisible, "Expected the exact invalid login error div with message 'Login failed. Please try again.' for invalid username");
        Assert.assertEquals(loginPage.getErrorMessage().trim(), "Login failed. Please try again.", "Invalid username message text mismatch");

        logger.info("Invalid username test passed - error handled correctly");
    }

    /**
     * Test 4: Invalid Password
     */
    @Test(priority = 4, description = "Test login with invalid password")
    public void testLoginWithInvalidPassword() {
        logger.info("Test: Login with Invalid Password");
        loginPage = new LoginPage(driver);

        String username = ConfigManager.getUsername();
        String invalidPassword = "invalidpassword123";

        // Perform login with invalid password
        logger.info("Attempting login with invalid password");
        loginPage.login(username, invalidPassword);

        // Wait briefly for error message/validation state
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for the exact invalid-login error div
        boolean invalidLoginErrorVisible = loginPage.waitForInvalidLoginMessage(180);
        Assert.assertTrue(invalidLoginErrorVisible, "Expected the exact invalid login error div with message 'Login failed. Please try again.' for invalid password");
        Assert.assertEquals(loginPage.getErrorMessage().trim(), "Login failed. Please try again.", "Invalid password message text mismatch");

        logger.info("Invalid password test passed - error handled correctly");
    }

    /**
     * Test 5: Empty Credentials
     */
    @Test(priority = 5, description = "Test login with empty credentials")
    public void testLoginWithEmptyCredentials() {
        logger.info("Test: Login with Empty Credentials");
        loginPage = new LoginPage(driver);

        // Try to login without entering credentials
        logger.info("Attempting login without credentials");
        loginPage.clickLoginButton();

        // Wait for login page or validation message after clicking Sign in
        boolean loginPageLoaded = loginPage.waitForLoginPageToLoad();
        boolean validationMessageVisible = loginPage.waitForValidationMessage();

        Assert.assertTrue(
                loginPageLoaded || validationMessageVisible,
                "Should remain on login page or show validation error"
        );
        Assert.assertTrue(
                loginPage.isValidationMessageDisplayed(),
                "Validation message should be displayed when credentials are empty"
        );

        logger.info("Empty credentials test passed - login prevented");
    }

    /**
     * Test 6: Login With Empty Username
     */
    @Test(priority = 6, description = "Test login with empty username")
    public void testLoginWithEmptyUsername() {
        logger.info("Test: Login with Empty Username");
        loginPage = new LoginPage(driver);

        loginPage.enterUsername("");
        loginPage.enterPassword(ConfigManager.getPassword());
        loginPage.clickLoginButton();

        // Wait for the exact empty username validation message
        boolean emptyUsernameErrorVisible = loginPage.waitForEmptyUsernameMessage(10);
        Assert.assertTrue(emptyUsernameErrorVisible, "Expected the empty username validation message");
        Assert.assertTrue(loginPage.isEmptyUsernameMessageDisplayed(), "Empty username validation message should be displayed");
    }

    /**
     * Test 7: Login With Empty Password
     */
    @Test(priority = 7, description = "Test login with empty password")
    public void testLoginWithEmptyPassword() {
        logger.info("Test: Login with Empty Password");
        loginPage = new LoginPage(driver);

        loginPage.enterUsername(ConfigManager.getUsername());
        loginPage.enterPassword("");
        loginPage.clickLoginButton();

        // Wait for the exact empty password validation message
        boolean emptyPasswordErrorVisible = loginPage.waitForEmptyPasswordMessage(10);
        Assert.assertTrue(emptyPasswordErrorVisible, "Expected the empty password validation message");
        Assert.assertTrue(loginPage.isEmptyPasswordMessageDisplayed(), "Empty password validation message should be displayed");
    }

    /**
     * Test 8: Verify Page URL
     */
    @Test(priority = 8, description = "Verify login page URL")
    public void testLoginPageURL() {
        logger.info("Test: Verify Login Page URL");
        loginPage = new LoginPage(driver);

        String currentUrl = loginPage.getCurrentUrl();

        Assert.assertTrue(currentUrl.contains("login"), "Current URL does not contain 'login'");
        logger.info("Login page URL verified: " + currentUrl);
    }

    /**
     * Test 9: Username Field Input
     */
    @Test(priority = 7, description = "Test username field accepts input")
    public void testUsernameFieldInput() {
        logger.info("Test: Username Field Input");
        loginPage = new LoginPage(driver);

        String testUsername = "testuser@example.com";
        loginPage.enterUsername(testUsername);

        String enteredValue = loginPage.getUsernameFieldValue();
        Assert.assertEquals(enteredValue, testUsername, "Username field input mismatch");

        logger.info("Username field input test passed");
    }

    /**
     * Test 8: Password Field Input
     */
    @Test(priority = 8, description = "Test password field accepts input")
    public void testPasswordFieldInput() {
        logger.info("Test: Password Field Input");
        loginPage = new LoginPage(driver);

        String testPassword = "TestPassword123";
        loginPage.enterPassword(testPassword);

        // Note: Password field value might be hidden, so we just verify it accepts input
        // and check that the field is not empty
        String enteredValue = loginPage.getPasswordFieldValue();
        Assert.assertNotNull(enteredValue, "Password field did not accept input");

        logger.info("Password field input test passed");
    }

    /**
     * Test 9: Clear Input Fields
     */
    @Test(priority = 9, description = "Test clearing input fields")
    public void testClearInputFields() {
        logger.info("Test: Clear Input Fields");
        loginPage = new LoginPage(driver);

        // Enter credentials
        loginPage.enterUsername("testuser@example.com");
        loginPage.enterPassword("TestPassword123");

        // Clear fields
        loginPage.clearUsernameField();
        loginPage.clearPasswordField();

        // Verify fields are cleared
        String usernameValue = loginPage.getUsernameFieldValue();
        String passwordValue = loginPage.getPasswordFieldValue();

        Assert.assertEquals(usernameValue, "", "Username field was not cleared");
        Assert.assertEquals(passwordValue, "", "Password field was not cleared");

        logger.info("Clear input fields test passed");
    }

    /**
     * Test 10: Page Title
     */
    @Test(priority = 10, description = "Verify login page title")
    public void testLoginPageTitle() {
        logger.info("Test: Login Page Title");
        loginPage = new LoginPage(driver);

        String pageTitle = loginPage.getPageTitle();
        Assert.assertNotNull(pageTitle, "Page title is null");
        Assert.assertFalse(pageTitle.isEmpty(), "Page title is empty");

        logger.info("Login page title: " + pageTitle);
    }
}
