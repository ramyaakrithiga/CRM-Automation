package com.crm.automation.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import com.crm.automation.config.ConfigManager;
import java.time.Duration;

/**
 * WebDriver Manager to handle driver initialization and management
 */
public class DriverManager {
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Initialize WebDriver based on browser type from config
     *
     * @return WebDriver instance
     */
    public static WebDriver initializeDriver() {
        String browser = ConfigManager.getBrowser();
        logger.info("Initializing driver for browser: " + browser);

        WebDriver webDriver = null;

        switch (browser) {
            case "chrome":
                webDriver = initializeChromeDriver();
                break;
            case "firefox":
                webDriver = initializeFirefoxDriver();
                break;
            case "edge":
                webDriver = initializeEdgeDriver();
                break;
            default:
                logger.error("Browser not supported: " + browser);
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.set(webDriver);
        setImplicitAndExplicitWaits(webDriver);
        logger.info("WebDriver initialized successfully");
        return webDriver;
    }

    /**
     * Initialize Chrome Driver with options
     *
     * @return Chrome WebDriver instance
     */
    private static WebDriver initializeChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        // Add Chrome arguments
        String chromeArgs = ConfigManager.getProperty("chrome.args");
        if (chromeArgs != null && !chromeArgs.isEmpty()) {
            String[] args = chromeArgs.split(",");
            for (String arg : args) {
                options.addArguments(arg.trim());
            }
        }

        // Add headless option if configured
        if (ConfigManager.isHeadless()) {
            options.addArguments("--headless");
        }

        // Disable automation features
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // Use a fresh Chrome profile to avoid password-save prompts
        options.addArguments("--no-first-run");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-features=Translate,OptimizationHints");
        options.addArguments("--password-store=basic");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        return new ChromeDriver(options);
    }

    /**
     * Initialize Firefox Driver with options
     *
     * @return Firefox WebDriver instance
     */
    private static WebDriver initializeFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        // Add Firefox arguments
        String firefoxArgs = ConfigManager.getProperty("firefox.args");
        if (firefoxArgs != null && !firefoxArgs.isEmpty()) {
            String[] args = firefoxArgs.split(",");
            for (String arg : args) {
                options.addArguments(arg.trim());
            }
        }

        // Add headless option if configured
        if (ConfigManager.isHeadless()) {
            options.addArguments("--headless");
        }

        return new FirefoxDriver(options);
    }

    /**
     * Initialize Edge Driver with options
     *
     * @return Edge WebDriver instance
     */
    private static WebDriver initializeEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();

        if (ConfigManager.isHeadless()) {
            options.addArguments("--headless");
        }

        options.addArguments("--start-maximized");
        return new EdgeDriver(options);
    }

    /**
     * Set implicit and explicit waits for driver
     *
     * @param webDriver WebDriver instance
     */
    private static void setImplicitAndExplicitWaits(WebDriver webDriver) {
        int implicitWait = ConfigManager.getImplicitWait();
        int pageLoadTimeout = ConfigManager.getPageLoadTimeout();

        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        logger.info("Implicit wait set to: " + implicitWait + " seconds");
        logger.info("Page load timeout set to: " + pageLoadTimeout + " seconds");
    }

    /**
     * Get current driver instance
     *
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver == null) {
            logger.warn("Driver is null, initializing new driver");
            return initializeDriver();
        }
        return webDriver;
    }

    /**
     * Close the current driver instance
     */
    public static void closeDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            webDriver.quit();
            logger.info("WebDriver closed successfully");
            driver.remove();
        }
    }

    /**
     * Close all driver instances
     */
    public static void quitDriver() {
        closeDriver();
    }

    /**
     * Maximize browser window
     */
    public static void maximizeBrowser() {
        try {
            getDriver().manage().window().maximize();
            logger.info("Browser window maximized");
        } catch (Exception e) {
            logger.error("Error maximizing browser window: " + e.getMessage());
        }
    }

    /**
     * Delete all cookies
     */
    public static void deleteAllCookies() {
        try {
            getDriver().manage().deleteAllCookies();
            logger.info("All cookies deleted");
        } catch (Exception e) {
            logger.error("Error deleting cookies: " + e.getMessage());
        }
    }
}
