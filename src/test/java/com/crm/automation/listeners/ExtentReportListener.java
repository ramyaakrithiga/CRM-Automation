package com.crm.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.crm.automation.utilities.CommonUtils;
import com.crm.automation.utilities.DriverManager;
import com.crm.automation.config.ConfigManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Extent Reports Test Listener
 * Generates HTML test reports
 */
public class ExtentReportListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(ExtentReportListener.class);
    private static ExtentReports extentReports;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static String reportPath;

    static {
        initializeReports();
    }

    /**
     * Initialize Extent Reports
     */
    private static void initializeReports() {
        try {
            // Create reports directory if not exists
            String reportsPath = ConfigManager.getReportsPath();
            File reportsDir = new File(reportsPath);
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
                logger.info("Reports directory created: " + reportsPath);
            }

            // Create report file with timestamp
            String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            reportPath = reportsPath + "TestReport_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle("CRM Automation Test Report");
            sparkReporter.config().setReportName("CRM Test Execution Report");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);

            // System information
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Browser", ConfigManager.getBrowser());
            extentReports.setSystemInfo("Application URL", ConfigManager.getApplicationUrl());

            logger.info("Extent Reports initialized: " + reportPath);
        } catch (Exception e) {
            logger.error("Error initializing Extent Reports: " + e.getMessage());
        }
    }

    /**
     * Called when test starts
     *
     * @param result ITestResult
     */
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Test started: " + result.getMethod().getMethodName());
        ExtentTest test = extentReports.createTest(result.getMethod().getMethodName());
        test.info("Test method: " + result.getMethod().getMethodName());
        extentTest.set(test);
    }

    /**
     * Called when test passes
     *
     * @param result ITestResult
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: " + result.getMethod().getMethodName());
        extentTest.get().pass("Test passed successfully");
        extentTest.get().info("Test execution time: " + (result.getEndMillis() - result.getStartMillis()) + "ms");
    }

    /**
     * Called when test fails
     *
     * @param result ITestResult
     */
    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test failed: " + result.getMethod().getMethodName());
        Throwable throwable = result.getThrowable();
        extentTest.get().fail("Test failed with exception: " + throwable.getMessage());
        extentTest.get().fail(throwable);

        // Take screenshot on failure
        try {
            if (ConfigManager.isScreenshotOnFailure()) {
                String screenshotFileName = result.getMethod().getMethodName() + "_" + 
                        new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
                CommonUtils.takeScreenshot(screenshotFileName);
                logger.info("Screenshot captured: " + screenshotFileName);
            }
        } catch (Exception e) {
            logger.error("Error taking screenshot: " + e.getMessage());
        }
    }

    /**
     * Called when test is skipped
     *
     * @param result ITestResult
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test skipped: " + result.getMethod().getMethodName());
        String skipMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown reason";
        extentTest.get().skip("Test skipped: " + skipMessage);
    }

    /**
     * Called after all tests
     */
    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test execution finished");
        if (extentReports != null) {
            extentReports.flush();
            logger.info("Extent Reports flushed. Report path: " + reportPath);
        }
    }

    /**
     * Get report path
     *
     * @return Report file path
     */
    public static String getReportPath() {
        return reportPath;
    }
}
