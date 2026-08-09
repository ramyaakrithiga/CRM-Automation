package com.crm.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.crm.automation.utilities.CommonUtils;

/**
 * Login Page Object Model
 * Contains locators and methods for login page interactions
 */
public class LoginPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    // Locators using @FindBy annotation
    @FindBy(xpath = "//input[@placeholder='Enter your username']")
    private WebElement usernameField;

    @FindBy(xpath = "//input[@placeholder='Enter your password']")
    private WebElement passwordField;

    @FindBy(xpath = "//button[@type='submit' and normalize-space()='Sign in']")
    private WebElement loginButton;

    @FindBy(css = ".error-message, .alert-danger")
    private WebElement errorMessage;

    @FindBy(xpath = "//label[contains(normalize-space(.),'Username')] | //label[contains(normalize-space(.),'Password')]")
    private WebElement pageLabel;

    // Locators using By
    private final By validationMessageLocator = By.cssSelector("p.mt-1.text-sm.text-red-500");
    private final By emptyUsernameMessageLocator = By.xpath("//p[contains(normalize-space(.),'Please input your username') or contains(normalize-space(.),'Enter username') or contains(normalize-space(.),'Username is required')]");
    private final By emptyPasswordMessageLocator = By.xpath("//p[contains(normalize-space(.),'Please input password') or contains(normalize-space(.),'Enter password') or contains(normalize-space(.),'Password is required')]");
    private final By invalidLoginMessageLocator = By.xpath("//div[normalize-space()='Login failed. Please try again.'] | //span[normalize-space()='Login failed. Please try again.'] | //p[normalize-space()='Login failed. Please try again.']");
    private final By validationTextLocator = By.xpath("//*[contains(normalize-space(.),'Login failed. Please try again.') or contains(normalize-space(.),'Username or password is incorrect') or contains(normalize-space(.),'Please input your username') or contains(normalize-space(.),'Please input password') or contains(normalize-space(.),'Invalid username') or contains(normalize-space(.),'Invalid password')]");
    private final By loginFormLocator = By.cssSelector("form.space-y-5");
    private final By usernameLocator = By.xpath("//input[@placeholder='Enter your username']");
    private final By passwordLocator = By.xpath("//input[@placeholder='Enter your password']");
    private final By loginButtonLocator = By.xpath("//button[@type='submit' and normalize-space()='Sign in']");
    private final By pageTitleLocator = By.xpath("//h1 | //h2 | //h3");
    private final By rememberMeCheckbox = By.cssSelector("input[type='checkbox'][id*='remember'], input[type='checkbox'][name*='remember'], input[type='checkbox'][class*='remember']");
    private final By forgotPasswordLink = By.xpath("//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'forgot')] | //button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'forgot')]");

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        logger.info("Login Page initialized");
    }

    /**
     * Enter username
     *
     * @param username Username to enter
     */
    public void enterUsername(String username) {
        logger.info("Entering username: " + username);
        try {
            CommonUtils.type(usernameLocator, username);
        } catch (Exception e) {
            logger.warn("Primary username entry failed, attempting direct JS fallback: " + e.getMessage());
            WebElement element = driver.findElement(usernameLocator);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click(); arguments[0].focus(); arguments[0].value = ''; arguments[0].value = arguments[1];", element, username);
        }
    }

    /**
     * Enter password
     *
     * @param password Password to enter
     */
    public void enterPassword(String password) {
        logger.info("Entering password");
        try {
            CommonUtils.type(passwordLocator, password);
        } catch (Exception e) {
            logger.warn("Primary password entry failed, attempting direct JS fallback: " + e.getMessage());
            WebElement element = driver.findElement(passwordLocator);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click(); arguments[0].focus(); arguments[0].value = ''; arguments[0].value = arguments[1];", element, password);
        }
    }

    /**
     * Click login button
     */
    public void clickLoginButton() {
        logger.info("Clicking login button");
        try {
            CommonUtils.click(loginButtonLocator);
        } catch (Exception e) {
            logger.warn("Primary login button click failed, attempting JS fallback: " + e.getMessage());
            WebElement element = driver.findElement(loginButtonLocator);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Perform login with credentials
     *
     * @param username Username
     * @param password Password
     */
    public void login(String username, String password) {
        logger.info("Performing login with username: " + username);
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Get error message
     *
     * @return Error message text
     */
    public String getErrorMessage() {
        logger.info("Getting error message");
        try {
            String text = "";
            try {
                text = CommonUtils.getText(invalidLoginMessageLocator);
            } catch (Exception ignored) {
                // ignore and try fallback locators
            }

            if (text == null || text.trim().isEmpty()) {
                try {
                    text = CommonUtils.getText(validationTextLocator);
                } catch (Exception ignored) {
                    // ignore fallback
                }
            }

            if (text == null) {
                text = "";
            }

            String normalized = text.replaceAll("\\s+", " ").trim();
            if (normalized.contains("Login failed. Please try again.")) {
                return "Login failed. Please try again.";
            }
            if (normalized.contains("Username or password is incorrect")) {
                return "Username or password is incorrect";
            }
            if (normalized.contains("Invalid username")) {
                return "Invalid username";
            }
            if (normalized.contains("Invalid password")) {
                return "Invalid password";
            }
            if (normalized.contains("Login failed")) {
                return "Login failed. Please try again.";
            }
            if (!normalized.isEmpty()) {
                return normalized;
            }
        } catch (Exception e) {
            logger.warn("Error reading login message: " + e.getMessage());
        }

        try {
            String fallback = CommonUtils.getText(By.cssSelector(".error-message, .alert-danger"));
            return fallback == null ? "" : fallback.replaceAll("\\s+", " ").trim();
        } catch (Exception e) {
            logger.warn("Fallback error message not found: " + e.getMessage());
            return "";
        }
    }

    /**
     * Check if the exact invalid login error message is displayed
     *
     * @return True if the exact invalid login error div is visible
     */
    public boolean isInvalidLoginErrorDisplayed() {
        logger.info("Checking if invalid login error message is displayed");
        return CommonUtils.isElementDisplayed(invalidLoginMessageLocator);
    }

    /**     * Wait for empty username validation message
     *
     * @param timeoutSeconds maximum seconds to wait
     * @return True if the empty username message appears
     */
    public boolean waitForEmptyUsernameMessage(int timeoutSeconds) {
        logger.info("Waiting for empty username error message for up to " + timeoutSeconds + " seconds");
        try {
            WebDriverWait customWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(timeoutSeconds));
            customWait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(emptyUsernameMessageLocator),
                    ExpectedConditions.visibilityOfElementLocated(validationTextLocator)
            ));
            return true;
        } catch (Exception e) {
            logger.warn("Empty username message did not appear in time: " + e.getMessage());
            return false;
        }
    }

    /**
     * Wait for empty password validation message
     *
     * @param timeoutSeconds maximum seconds to wait
     * @return True if the empty password message appears
     */
    public boolean waitForEmptyPasswordMessage(int timeoutSeconds) {
        logger.info("Waiting for empty password error message for up to " + timeoutSeconds + " seconds");
        try {
            WebDriverWait customWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(timeoutSeconds));
            customWait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(emptyPasswordMessageLocator),
                    ExpectedConditions.visibilityOfElementLocated(validationTextLocator)
            ));
            return true;
        } catch (Exception e) {
            logger.warn("Empty password message did not appear in time: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if the empty username validation message is displayed
     *
     * @return True if the empty username validation message is visible
     */
    public boolean isEmptyUsernameMessageDisplayed() {
        logger.info("Checking if empty username validation message is displayed");
        return CommonUtils.isElementDisplayed(emptyUsernameMessageLocator);
    }

    /**
     * Check if the empty password validation message is displayed
     *
     * @return True if the empty password validation message is visible
     */
    public boolean isEmptyPasswordMessageDisplayed() {
        logger.info("Checking if empty password validation message is displayed");
        return CommonUtils.isElementDisplayed(emptyPasswordMessageLocator);
    }

    /**     * Check if any generic validation message is displayed
     *
     * @return True if a generic validation message is visible
     */
    public boolean isValidationMessageDisplayed() {
        logger.info("Checking if generic validation message is displayed");
        return CommonUtils.isElementDisplayed(validationMessageLocator)
                || CommonUtils.isElementDisplayed(validationTextLocator);
    }

    /**
     * Wait for the invalid login error message to appear
     *
     * @param timeoutSeconds maximum seconds to wait
     * @return True if the invalid login error message appears
     */
    public boolean waitForInvalidLoginMessage(int timeoutSeconds) {
        logger.info("Waiting for invalid login message for up to " + timeoutSeconds + " seconds");
        try {
            WebDriverWait customWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(timeoutSeconds));
            customWait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(invalidLoginMessageLocator),
                    ExpectedConditions.visibilityOfElementLocated(validationTextLocator)
            ));
            return true;
        } catch (Exception e) {
            logger.warn("Invalid login message did not appear in time: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if login page is loaded
     *
     * @return True if login form and fields are visible
     */
    public boolean isLoginPageLoaded() {
        logger.info("Checking if login page is loaded");
        return isLoginFormVisible()
                && isUsernameFieldVisible()
                && isPasswordFieldVisible()
                && CommonUtils.isElementDisplayed(loginButtonLocator);
    }

    /**
     * Check if login form is visible
     *
     * @return True if login form is visible
     */
    public boolean isLoginFormVisible() {
        logger.info("Checking if login form is visible");
        return CommonUtils.isElementDisplayed(loginFormLocator);
    }

    /**
     * Wait for the login page to fully load
     *
     * @return True if the login page appears within timeout
     */
    public boolean waitForLoginPageToLoad() {
        logger.info("Waiting for login page to load");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormLocator));
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameLocator));
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordLocator));
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginButtonLocator));
            return true;
        } catch (Exception e) {
            logger.warn("Login page did not load in time: " + e.getMessage());
            return false;
        }
    }

    /**
     * Wait for validation message to appear
     *
     * @return True if a validation message appears within timeout
     */
    public boolean waitForValidationMessage() {
        logger.info("Waiting for validation message to appear");
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(validationMessageLocator),
                    ExpectedConditions.visibilityOfElementLocated(validationTextLocator)
            ));
            return true;
        } catch (Exception e) {
            logger.warn("Validation message did not appear in time: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if username field is visible
     *
     * @return True if username field is visible
     */
    public boolean isUsernameFieldVisible() {
        logger.info("Checking if username field is visible");
        return CommonUtils.isElementDisplayed(usernameLocator);
    }

    /**
     * Check if password field is visible
     *
     * @return True if password field is visible
     */
    public boolean isPasswordFieldVisible() {
        logger.info("Checking if password field is visible");
        return CommonUtils.isElementDisplayed(passwordLocator);
    }

    /**
     * Check remember me checkbox
     */
    public void checkRememberMe() {
        logger.info("Checking remember me checkbox");
        try {
            WebElement checkbox = driver.findElement(rememberMeCheckbox);
            if (!checkbox.isSelected()) {
                CommonUtils.click(rememberMeCheckbox);
            }
        } catch (Exception e) {
            logger.warn("Remember me checkbox not found");
        }
    }

    /**
     * Check if remember me checkbox is selected
     *
     * @return True if selected
     */
    public boolean isRememberMeCheckboxSelected() {
        logger.info("Checking whether remember me checkbox is selected");
        try {
            return driver.findElement(rememberMeCheckbox).isSelected();
        } catch (Exception e) {
            logger.warn("Remember me checkbox not found");
            return false;
        }
    }

    /**
     * Click on forgot password link
     */
    public void clickForgotPasswordLink() {
        logger.info("Clicking forgot password link");
        try {
            CommonUtils.click(forgotPasswordLink);
        } catch (Exception e) {
            logger.warn("Forgot password link not found");
        }
    }

    /**
     * Check if forgot password link is visible
     *
     * @return True if link is visible
     */
    public boolean isForgotPasswordLinkVisible() {
        logger.info("Checking if forgot password link is visible");
        return CommonUtils.isElementDisplayed(forgotPasswordLink);
    }

    /**
     * Get page title/heading
     *
     * @return Page heading text
     */
    public String getPageHeading() {
        logger.info("Getting page heading");
        try {
            return CommonUtils.getText(pageTitleLocator);
        } catch (Exception e) {
            logger.warn("Page heading not found");
            return "";
        }
    }

    /**
     * Verify login page elements
     *
     * @return True if all elements are visible
     */
    public boolean verifyLoginPageElements() {
        logger.info("Verifying login page elements");
        return isUsernameFieldVisible() && isPasswordFieldVisible() && isLoginPageLoaded();
    }

    /**
     * Clear username field
     */
    public void clearUsernameField() {
        logger.info("Clearing username field");
        try {
            usernameField.clear();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("var el = document.querySelector('input[placeholder=\"Enter your username\"]'); if (el) { el.value = ''; el.dispatchEvent(new Event('input', { bubbles: true })); el.dispatchEvent(new Event('change', { bubbles: true })); }");
        }
    }

    /**
     * Clear password field
     */
    public void clearPasswordField() {
        logger.info("Clearing password field");
        try {
            passwordField.clear();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("var el = document.querySelector('input[placeholder=\"Enter your password\"]'); if (el) { el.value = ''; el.dispatchEvent(new Event('input', { bubbles: true })); el.dispatchEvent(new Event('change', { bubbles: true })); }");
        }
    }

    /**
     * Get username field value
     *
     * @return Username field value
     */
    public String getUsernameFieldValue() {
        logger.info("Getting username field value");
        return CommonUtils.getAttribute(usernameLocator, "value");
    }

    /**
     * Get password field value
     *
     * @return Password field value
     */
    public String getPasswordFieldValue() {
        logger.info("Getting password field value");
        return CommonUtils.getAttribute(passwordLocator, "value");
    }
}
