package com.crm.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.crm.automation.utilities.CommonUtils;
import org.openqa.selenium.WebElement;

/**
 * Dashboard Page Object Model
 * Contains locators and methods for dashboard/home page interactions
 */
public class DashboardPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    // Locators
    private final By dashboardHeaderLocator = By.xpath("//h1[contains(., 'Dashboard') or contains(., 'dashboard')] | //h2[contains(., 'Dashboard') or contains(., 'dashboard')] | //h3[contains(., 'Dashboard') or contains(., 'dashboard')]");
    private final By userMenuLocator = By.xpath("//div[@class='user-menu'] | //nav[@role='navigation'] | //button[contains(., 'Menu')] | //button[contains(., 'menu')]");
    private final By logoutButtonLocator = By.xpath("//a[contains(text(), 'Logout')] | //button[contains(text(), 'Logout')] | //button[contains(., 'Logout')]");
    private final By userGreetingLocator = By.xpath("//*[contains(text(), 'Welcome') or contains(text(), 'welcome')]");
    private final By navbarLocator = By.xpath("//nav | //div[contains(@class, 'navbar')] | //div[contains(@class, 'sidebar')] | //header");

    public DashboardPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        logger.info("Dashboard Page initialized");
    }

    /**
     * Check if dashboard page is loaded
     *
     * @return True if dashboard is loaded
     */
    public boolean isDashboardPageLoaded() {
        logger.info("Checking if dashboard page is loaded");
        try {
            return CommonUtils.isElementDisplayed(dashboardHeaderLocator)
                    || CommonUtils.isElementDisplayed(navbarLocator)
                    || getCurrentUrl().contains("dashboard")
                    || getCurrentUrl().contains("home");
        } catch (Exception e) {
            logger.warn("Dashboard header not found, checking URL");
            return getCurrentUrl().contains("dashboard") || getCurrentUrl().contains("home");
        }
    }

    /**
     * Get dashboard page title
     *
     * @return Dashboard title
     */
    public String getDashboardTitle() {
        logger.info("Getting dashboard title");
        try {
            return CommonUtils.getText(dashboardHeaderLocator);
        } catch (Exception e) {
            logger.info("Dashboard title not found, using page title");
            return getPageTitle();
        }
    }

    /**
     * Click on user menu
     */
    public void clickUserMenu() {
        logger.info("Clicking user menu");
        try {
            CommonUtils.click(userMenuLocator);
        } catch (Exception e) {
            logger.warn("User menu not found or clickable");
        }
    }

    /**
     * Click logout button
     */
    public void clickLogout() {
        logger.info("Clicking logout button");
        try {
            CommonUtils.click(logoutButtonLocator);
        } catch (Exception e) {
            logger.error("Logout button not found: " + e.getMessage());
            throw new RuntimeException("Failed to click logout button", e);
        }
    }

    /**
     * Logout from dashboard
     */
    public void logout() {
        logger.info("Performing logout");
        clickUserMenu();
        clickLogout();
    }

    /**
     * Check if user greeting is displayed
     *
     * @return True if greeting is visible
     */
    public boolean isUserGreetingDisplayed() {
        logger.info("Checking if user greeting is displayed");
        try {
            return CommonUtils.isElementDisplayed(userGreetingLocator);
        } catch (Exception e) {
            logger.warn("User greeting not found");
            return false;
        }
    }

    /**
     * Get user greeting text
     *
     * @return User greeting text
     */
    public String getUserGreetingText() {
        logger.info("Getting user greeting text");
        try {
            return CommonUtils.getText(userGreetingLocator);
        } catch (Exception e) {
            logger.warn("User greeting not found");
            return "";
        }
    }

    /**
     * Check if navigation bar is displayed
     *
     * @return True if navbar is visible
     */
    public boolean isNavbarDisplayed() {
        logger.info("Checking if navigation bar is displayed");
        return CommonUtils.isElementDisplayed(navbarLocator);
    }

    /**
     * Wait for dashboard to load
     */
    public void waitForDashboardToLoad() {
        logger.info("Waiting for dashboard to load");
        int maxAttempts = 6;
        int attempts = 0;
        while (!isDashboardPageLoaded() && attempts < maxAttempts) {
            try {
                Thread.sleep(500);
                attempts++;
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        if (isDashboardPageLoaded()) {
            logger.info("Dashboard loaded successfully");
        } else {
            logger.error("Dashboard did not load after " + maxAttempts + " attempts");
        }
    }

    /**
     * Verify dashboard page elements
     *
     * @return True if all elements are visible
     */
    public boolean verifyDashboardPageElements() {
        logger.info("Verifying dashboard page elements");
        return isDashboardPageLoaded() && isNavbarDisplayed();
    }
}
