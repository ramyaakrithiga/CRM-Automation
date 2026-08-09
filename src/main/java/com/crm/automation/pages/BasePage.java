package com.crm.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.crm.automation.config.ConfigManager;
import com.crm.automation.utilities.CommonUtils;
import java.time.Duration;

/**
 * Base Page class for Page Object Model
 * Contains common methods used by all pages
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getExplicitWait()));
        PageFactory.initElements(driver, this);
        CommonUtils.setDriver(driver);
    }

    /**
     * Navigate to URL
     *
     * @param url URL to navigate
     */
    public void navigateTo(String url) {
        driver.navigate().to(url);
    }

    /**
     * Get page title
     *
     * @return Page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get current URL
     *
     * @return Current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Refresh page
     */
    public void refreshPage() {
        driver.navigate().refresh();
    }

    /**
     * Go back in browser
     */
    public void goBack() {
        driver.navigate().back();
    }

    /**
     * Go forward in browser
     */
    public void goForward() {
        driver.navigate().forward();
    }
}
