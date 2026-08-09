package com.crm.automation.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.crm.automation.config.ConfigManager;
import java.time.Duration;
import java.util.List;

/**
 * Common utilities for WebDriver operations
 */
public class CommonUtils {
    private static final Logger logger = LogManager.getLogger(CommonUtils.class);
    private static WebDriver driver;
    private static WebDriverWait wait;

    private CommonUtils() {
    }

    /**
     * Initialize utilities with driver
     *
     * @param webDriver WebDriver instance
     */
    public static void setDriver(WebDriver webDriver) {
        driver = webDriver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getExplicitWait()));
    }

    /**
     * Click on element using JavaScript
     *
     * @param element WebElement to click
     */
    public static void jsClick(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            logger.info("Clicked element using JavaScript");
        } catch (Exception e) {
            logger.error("Error clicking element with JS: " + e.getMessage());
            throw new RuntimeException("Failed to click element using JS", e);
        }
    }

    /**
     * Send keys to element using JavaScript
     *
     * @param element WebElement to send keys
     * @param text Text to send
     */
    public static void jsSendKeys(WebElement element, String text) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + text + "';", element);
            logger.info("Sent keys to element using JavaScript: " + text);
        } catch (Exception e) {
            logger.error("Error sending keys using JS: " + e.getMessage());
            throw new RuntimeException("Failed to send keys using JS", e);
        }
    }

    /**
     * Wait for element to be visible
     *
     * @param locator By locator
     * @return WebElement
     */
    private static WebElement findElementWithFallback(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            if (!elements.isEmpty()) {
                WebElement element = elements.get(0);
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
                return element;
            }
        } catch (Exception ignored) {
            // continue to DOM fallback below
        }

        try {
            return wait.until(driver -> {
                try {
                    WebElement element = driver.findElement(locator);
                    return element.isDisplayed() && element.isEnabled() ? element : null;
                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    return null;
                }
            });
        } catch (TimeoutException e) {
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                logger.warn("Element became present but not yet fully visible/interactive: " + locator);
                return element;
            } catch (TimeoutException ex) {
                try {
                    String locatorText = locator.toString();
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    if (locatorText.startsWith("By.cssSelector:")) {
                        String selector = locatorText.substring("By.cssSelector:".length()).trim();
                        Object result = js.executeScript("return document.querySelector(arguments[0]);", selector);
                        if (result instanceof WebElement) {
                            logger.warn("Using JavaScript DOM element for locator: " + locator);
                            return (WebElement) result;
                        }
                    } else if (locatorText.startsWith("By.xpath:")) {
                        String xpath = locatorText.substring("By.xpath:".length()).trim();
                        Object result = js.executeScript("return document.evaluate(arguments[0], document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;", xpath);
                        if (result instanceof WebElement) {
                            logger.warn("Using JavaScript DOM element for locator: " + locator);
                            return (WebElement) result;
                        }
                    }
                } catch (Exception ignored) {
                    logger.warn("JavaScript DOM fallback failed for locator: " + locator);
                }
                throw ex;
            }
        }
    }

    public static WebElement waitForElementToBeVisible(By locator) {
        try {
            WebElement element = findElementWithFallback(locator);
            logger.info("Element is visible/present: " + locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Element not visible within timeout: " + locator);
            throw e;
        }
    }

    /**
     * Wait for element to be clickable
     *
     * @param locator By locator
     * @return WebElement
     */
    public static WebElement waitForElementToBeClickable(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        if (!elements.isEmpty()) {
            WebElement element = elements.get(0);
            if (element.isDisplayed() && element.isEnabled()) {
                logger.info("Element is clickable: " + locator);
                return element;
            }
        }

        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.info("Element is clickable: " + locator);
            return element;
        } catch (TimeoutException e) {
            try {
                WebElement element = findElementWithFallback(locator);
                logger.warn("Element became present but not yet clickable: " + locator);
                return element;
            } catch (TimeoutException ex) {
                logger.error("Element not clickable within timeout: " + locator);
                throw ex;
            }
        }
    }

    /**
     * Wait for element to be present
     *
     * @param locator By locator
     * @return WebElement
     */
    public static WebElement waitForElementPresence(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            logger.info("Element is present: " + locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Element not present within timeout: " + locator);
            throw e;
        }
    }

    /**
     * Click on element
     *
     * @param locator By locator
     */
    public static void click(By locator) {
        try {
            WebElement element = waitForElementToBeClickable(locator);
            if (element != null) {
                if (element.isDisplayed() && element.isEnabled()) {
                    element.click();
                } else {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                }
                logger.info("Clicked on element: " + locator);
                return;
            }
        } catch (Exception e) {
            logger.warn("Selenium click failed for locator: " + locator + ", trying DOM fallback");
        }

        try {
            String locatorText = locator.toString();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            if (locatorText.startsWith("By.cssSelector:")) {
                String selector = locatorText.substring("By.cssSelector:".length()).trim();
                js.executeScript("var el = document.querySelector(arguments[0]); if (el) { el.click(); el.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true })); }", selector);
            } else if (locatorText.startsWith("By.xpath:")) {
                String xpath = locatorText.substring("By.xpath:".length()).trim();
                js.executeScript("var el = document.evaluate(arguments[0], document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; if (el) { el.click(); el.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true })); }", xpath);
            }
            logger.info("Clicked element using JavaScript fallback: " + locator);
        } catch (Exception ignored) {
            logger.warn("JavaScript fallback click failed for locator: " + locator);
            throw new RuntimeException("Failed to click element", ignored);
        }
    }

    /**
     * Click on WebElement
     *
     * @param element WebElement to click
     */
    public static void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
            logger.info("Clicked on element");
        } catch (Exception e) {
            logger.error("Error clicking element: " + e.getMessage());
            throw new RuntimeException("Failed to click element", e);
        }
    }

    /**
     * Type text into element
     *
     * @param locator By locator
     * @param text Text to type
     */
    public static void type(By locator, String text) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("Typed text in element: " + text);
            return;
        } catch (Exception e) {
            logger.warn("Selenium typing failed for locator: " + locator + ", trying DOM fallback");
        }

        try {
            String locatorText = locator.toString();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            if (locatorText.startsWith("By.cssSelector:")) {
                String selector = locatorText.substring("By.cssSelector:".length()).trim();
                js.executeScript("var el = document.querySelector(arguments[0]); if (el) { el.focus(); el.value = ''; el.value = arguments[1]; el.dispatchEvent(new Event('input', { bubbles: true })); el.dispatchEvent(new Event('change', { bubbles: true })); }", selector, text);
            } else if (locatorText.startsWith("By.xpath:")) {
                String xpath = locatorText.substring("By.xpath:".length()).trim();
                js.executeScript("var el = document.evaluate(arguments[0], document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; if (el) { el.focus(); el.value = ''; el.value = arguments[1]; el.dispatchEvent(new Event('input', { bubbles: true })); el.dispatchEvent(new Event('change', { bubbles: true })); }", xpath, text);
            }
            logger.info("Typed text using JavaScript fallback: " + text);
        } catch (Exception ignored) {
            logger.warn("JavaScript fallback typing failed for locator: " + locator);
            throw new RuntimeException("Failed to type text", ignored);
        }
    }

    /**
     * Type text into WebElement
     *
     * @param element WebElement
     * @param text Text to type
     */
    public static void type(WebElement element, String text) {
        try {
            element.clear();
            element.sendKeys(text);
            logger.info("Typed text in element: " + text);
        } catch (Exception e) {
            logger.error("Error typing text: " + e.getMessage());
            throw new RuntimeException("Failed to type text", e);
        }
    }

    /**
     * Get text from element
     *
     * @param locator By locator
     * @return Text content
     */
    public static String getText(By locator) {
        try {
            String text = waitForElementToBeVisible(locator).getText();
            logger.info("Retrieved text: " + text);
            return text;
        } catch (Exception e) {
            logger.error("Error getting text: " + e.getMessage());
            throw new RuntimeException("Failed to get text", e);
        }
    }

    /**
     * Get text from WebElement
     *
     * @param element WebElement
     * @return Text content
     */
    public static String getText(WebElement element) {
        try {
            String text = element.getText();
            logger.info("Retrieved text: " + text);
            return text;
        } catch (Exception e) {
            logger.error("Error getting text: " + e.getMessage());
            throw new RuntimeException("Failed to get text", e);
        }
    }

    /**
     * Select option from dropdown by value
     *
     * @param locator By locator
     * @param value Option value
     */
    public static void selectByValue(By locator, String value) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            Select select = new Select(element);
            select.selectByValue(value);
            logger.info("Selected option by value: " + value);
        } catch (Exception e) {
            logger.error("Error selecting option: " + e.getMessage());
            throw new RuntimeException("Failed to select option", e);
        }
    }

    /**
     * Select option from dropdown by visible text
     *
     * @param locator By locator
     * @param text Option text
     */
    public static void selectByVisibleText(By locator, String text) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            Select select = new Select(element);
            select.selectByVisibleText(text);
            logger.info("Selected option by text: " + text);
        } catch (Exception e) {
            logger.error("Error selecting option: " + e.getMessage());
            throw new RuntimeException("Failed to select option", e);
        }
    }

    /**
     * Check if element is displayed
     *
     * @param locator By locator
     * @return True if displayed
     */
    public static boolean isElementDisplayed(By locator) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            return element.isDisplayed() || element.getAttribute("value") != null || element.getTagName() != null;
        } catch (TimeoutException e) {
            logger.warn("Element not displayed: " + locator);
            return false;
        }
    }

    /**
     * Check if element is enabled
     *
     * @param locator By locator
     * @return True if enabled
     */
    public static boolean isElementEnabled(By locator) {
        try {
            return waitForElementPresence(locator).isEnabled();
        } catch (TimeoutException e) {
            logger.warn("Element not enabled: " + locator);
            return false;
        }
    }

    /**
     * Get all elements matching locator
     *
     * @param locator By locator
     * @return List of WebElements
     */
    public static List<WebElement> findElements(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            logger.info("Found " + elements.size() + " elements");
            return elements;
        } catch (Exception e) {
            logger.error("Error finding elements: " + e.getMessage());
            throw new RuntimeException("Failed to find elements", e);
        }
    }

    /**
     * Hover over element
     *
     * @param locator By locator
     */
    public static void hoverOverElement(By locator) {
        try {
            WebElement element = waitForElementToBeVisible(locator);
            Actions actions = new Actions(driver);
            actions.moveToElement(element).perform();
            logger.info("Hovered over element: " + locator);
        } catch (Exception e) {
            logger.error("Error hovering over element: " + e.getMessage());
            throw new RuntimeException("Failed to hover over element", e);
        }
    }

    /**
     * Switch to frame by index
     *
     * @param frameIndex Frame index
     */
    public static void switchToFrame(int frameIndex) {
        try {
            driver.switchTo().frame(frameIndex);
            logger.info("Switched to frame index: " + frameIndex);
        } catch (Exception e) {
            logger.error("Error switching to frame: " + e.getMessage());
            throw new RuntimeException("Failed to switch to frame", e);
        }
    }

    /**
     * Switch to frame by WebElement
     *
     * @param element Frame element
     */
    public static void switchToFrame(WebElement element) {
        try {
            driver.switchTo().frame(element);
            logger.info("Switched to frame");
        } catch (Exception e) {
            logger.error("Error switching to frame: " + e.getMessage());
            throw new RuntimeException("Failed to switch to frame", e);
        }
    }

    /**
     * Switch out of frame
     */
    public static void switchOutOfFrame() {
        try {
            driver.switchTo().defaultContent();
            logger.info("Switched out of frame");
        } catch (Exception e) {
            logger.error("Error switching out of frame: " + e.getMessage());
            throw new RuntimeException("Failed to switch out of frame", e);
        }
    }

    /**
     * Get attribute value
     *
     * @param locator By locator
     * @param attributeName Attribute name
     * @return Attribute value
     */
    public static String getAttribute(By locator, String attributeName) {
        try {
            String value = waitForElementPresence(locator).getAttribute(attributeName);
            logger.info("Retrieved attribute '" + attributeName + "': " + value);
            return value;
        } catch (Exception e) {
            logger.error("Error getting attribute: " + e.getMessage());
            throw new RuntimeException("Failed to get attribute", e);
        }
    }

    /**
     * Take screenshot
     *
     * @param fileName Screenshot file name
     */
    public static void takeScreenshot(String fileName) {
        try {
            String screenshotPath = ConfigManager.getScreenshotPath();
            java.io.File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            java.io.File destFile = new java.io.File(screenshotPath + fileName + ".png");
            org.apache.commons.io.FileUtils.copyFile(sourceFile, destFile);
            logger.info("Screenshot saved: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Error taking screenshot: " + e.getMessage());
        }
    }

    /**
     * Navigate to URL
     *
     * @param url URL to navigate
     */
    public static void navigateTo(String url) {
        try {
            driver.navigate().to(url);
            logger.info("Navigated to URL: " + url);
        } catch (Exception e) {
            logger.error("Error navigating to URL: " + e.getMessage());
            throw new RuntimeException("Failed to navigate to URL", e);
        }
    }

    /**
     * Get current page title
     *
     * @return Page title
     */
    public static String getPageTitle() {
        try {
            String title = driver.getTitle();
            logger.info("Page title: " + title);
            return title;
        } catch (Exception e) {
            logger.error("Error getting page title: " + e.getMessage());
            throw new RuntimeException("Failed to get page title", e);
        }
    }

    /**
     * Get current page URL
     *
     * @return Page URL
     */
    public static String getCurrentUrl() {
        try {
            String url = driver.getCurrentUrl();
            logger.info("Current URL: " + url);
            return url;
        } catch (Exception e) {
            logger.error("Error getting current URL: " + e.getMessage());
            throw new RuntimeException("Failed to get current URL", e);
        }
    }
}
