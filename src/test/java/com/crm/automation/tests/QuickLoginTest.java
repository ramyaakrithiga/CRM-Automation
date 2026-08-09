package com.crm.automation.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.crm.automation.base.BaseTest;
import com.crm.automation.pages.LoginPage;
import com.crm.automation.pages.DashboardPage;

public class QuickLoginTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(QuickLoginTest.class);

    @Test(description = "Quick login test using provided demo credentials")
    public void testQuickLogin() {
        logger.info("Running QuickLoginTest");
        LoginPage loginPage = new LoginPage(driver);

        // Use supplied credentials directly
        loginPage.login("demo", "5555");

        DashboardPage dashboard = new DashboardPage(driver);
        dashboard.waitForDashboardToLoad();

        Assert.assertTrue(dashboard.isDashboardPageLoaded(), "Dashboard did not load after login");
    }
}
