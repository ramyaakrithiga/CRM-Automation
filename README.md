# CRM Automation Pipeline

A comprehensive Selenium automation testing framework for CRM application testing built with Java, Maven, TestNG, and Extent Reports.

## Project Overview

This framework provides a robust, maintainable, and scalable solution for automating UI tests on the CRM application. It follows industry best practices including the Page Object Model (POM) design pattern and includes comprehensive logging and reporting capabilities.

**Application URL:** https://crm.osllc.us/admin/auth/login

## Technology Stack

- **Language:** Java 11
- **Build Tool:** Maven 3.6+
- **Automation Tool:** Selenium WebDriver 4.16.1
- **Testing Framework:** TestNG 7.9.0
- **WebDriver Management:** WebDriverManager 5.7.3
- **Reporting:** Extent Reports 5.1.1
- **Logging:** Apache Log4j 2.22.0
- **Design Pattern:** Page Object Model (POM)

## Project Structure

```
crm-automation-pipeline/
├── src/
│   ├── main/
│   │   ├── java/com/crm/automation/
│   │   │   ├── base/
│   │   │   │   └── BaseTest.java
│   │   │   ├── config/
│   │   │   │   └── ConfigManager.java
│   │   │   ├── pages/
│   │   │   │   ├── BasePage.java
│   │   │   │   ├── LoginPage.java
│   │   │   │   └── DashboardPage.java
│   │   │   └── utilities/
│   │   │       ├── DriverManager.java
│   │   │       └── CommonUtils.java
│   │   └── resources/
│   │       ├── config.properties
│   │       └── log4j2.xml
│   └── test/
│       └── java/com/crm/automation/
│           ├── listeners/
│           │   └── ExtentReportListener.java
│           └── tests/
│               ├── LoginTests.java
│               └── DashboardTests.java
├── docs/
│   ├── prompt_library.md
│   └── traceability_matrix.md
├── tests/
│   ├── api/
│   │   └── README.md
│   ├── data/
│   │   └── README.md
│   └── ui/
│       └── README.md
├── pom.xml
├── testng.xml
├── Jenkinsfile
├── .gitignore
└── README.md
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Chrome, Firefox, or Edge browser
- Git

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd crm-automation-pipeline
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Configure Application Settings

Edit `src/main/resources/config.properties`:

```properties
app.url=https://crm.osllc.us/admin/auth/login
browser=chrome
username=admin@crm.com
password=Admin@123
```

### 4. Update Browser Options

- **Chrome:** Modify `chrome.args` in config.properties
- **Firefox:** Modify `firefox.args` in config.properties
- **Headless Mode:** Set `headless=true` for headless execution

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn clean test -Dtest=LoginTests
```

### Run Specific Test Method
```bash
mvn clean test -Dtest=LoginTests#testSuccessfulLogin
```

### Run Tests in Parallel
```bash
mvn clean test -DthreadCount=4
```

### Generate Reports Only
```bash
mvn clean test -Dskip.exec=true
```

## Configuration

### config.properties

Key configuration properties:

| Property | Description | Default |
|----------|-------------|---------|
| app.url | Application URL | https://crm.osllc.us/admin/auth/login |
| browser | Browser type (chrome, firefox, edge) | chrome |
| headless | Run browser in headless mode | false |
| implicit.wait | Implicit wait timeout (seconds) | 10 |
| explicit.wait | Explicit wait timeout (seconds) | 20 |
| page.load.timeout | Page load timeout (seconds) | 30 |
| username | Application username | admin@crm.com |
| password | Application password | Admin@123 |
| screenshot.on.failure | Take screenshot on test failure | true |
| screenshot.path | Screenshot storage path | ./screenshots/ |
| reports.path | Reports output path | ./reports/ |

### log4j2.xml

Configures logging levels, appenders, and output format. Logs are written to:
- Console (INFO level)
- File: `./logs/crm-automation.log`
- Rolling files with daily rotation

## Page Object Model (POM)

### Base Page Class
- Contains common methods for all pages
- Handles wait operations
- Manages navigation

### Login Page
Methods:
- `login(String username, String password)` - Perform login
- `enterUsername(String username)` - Enter username
- `enterPassword(String password)` - Enter password
- `clickLoginButton()` - Click login button
- `isLoginPageLoaded()` - Check if login page is loaded
- `getErrorMessage()` - Get error message text
- `verifyLoginPageElements()` - Verify all elements

### Dashboard Page
Methods:
- `isDashboardPageLoaded()` - Check dashboard load status
- `getDashboardTitle()` - Get dashboard title
- `logout()` - Perform logout
- `verifyDashboardPageElements()` - Verify dashboard elements

## Test Cases

### Login Tests (10 Test Cases)
1. Verify login page elements
2. Successful login with valid credentials
3. Invalid username handling
4. Invalid password handling
5. Empty credentials handling
6. Login page URL verification
7. Username field input
8. Password field input
9. Clear input fields
10. Login page title verification

### Dashboard Tests (10 Test Cases)
1. Dashboard page loads after login
2. Dashboard elements verification
3. Dashboard title verification
4. Dashboard URL verification
5. Page title verification
6. User greeting display
7. Navigation bar display
8. Dashboard load time verification
9. Browser navigation functionality
10. Page refresh functionality

## Test Execution Reports

### Extent Reports

Test reports are automatically generated in `./reports/` directory:

- **Report Name:** `TestReport_YYYY_MM_DD_HH_MM_SS.html`
- **Information Captured:**
  - Test summary (passed, failed, skipped)
  - Detailed test execution logs
  - Screenshots on failure
  - System information
  - Execution time

### View Reports

1. Navigate to `./reports/` directory
2. Open the HTML report file in a browser
3. View detailed test results with logs and screenshots

## Logging

### Log Levels

- **INFO:** General information and test flow
- **WARN:** Warning messages
- **ERROR:** Error messages and exceptions

### Log Files

- **Location:** `./logs/`
- **Main Log:** `crm-automation.log`
- **Rolling Logs:** Daily rotation with max 10 files

## Common Methods (CommonUtils)

### Wait Operations
- `waitForElementToBeVisible(By locator)`
- `waitForElementToBeClickable(By locator)`
- `waitForElementPresence(By locator)`

### Element Interactions
- `click(By locator)`
- `type(By locator, String text)`
- `getText(By locator)`
- `selectByValue(By locator, String value)`
- `hoverOverElement(By locator)`

### Element Verification
- `isElementDisplayed(By locator)`
- `isElementEnabled(By locator)`
- `getAttribute(By locator, String attributeName)`

### Browser Operations
- `navigateTo(String url)`
- `getPageTitle()`
- `getCurrentUrl()`
- `takeScreenshot(String fileName)`

## Jenkins Integration

The project includes a `Jenkinsfile` for CI/CD integration:

```groovy
// Build and test execution
stages {
    stage('Build') {
        steps {
            sh 'mvn clean install'
        }
    }
    stage('Test') {
        steps {
            sh 'mvn clean test'
        }
    }
    stage('Report') {
        steps {
            // Publish Extent Reports
        }
    }
}
```

## Troubleshooting

### WebDriver Issues

**Problem:** WebDriver not initializing
- **Solution:** Ensure browser is installed and WebDriverManager has internet access

**Problem:** Element not found
- **Solution:** Inspect element in browser, update locators in page class

### Test Failures

**Problem:** Tests timeout
- **Solution:** Increase wait times in config.properties

**Problem:** Screenshots not captured
- **Solution:** Ensure screenshot path exists and has write permissions

### Logging Issues

**Problem:** Logs not appearing
- **Solution:** Check log4j2.xml configuration and log directory permissions

## Best Practices

1. **Use Page Object Model:** Keep locators in page classes
2. **Wait for Elements:** Use explicit waits instead of Thread.sleep()
3. **Handle Exceptions:** Catch and log exceptions appropriately
4. **Take Screenshots:** On failures for debugging
5. **Use Descriptive Names:** For test methods and variables
6. **Keep Tests Independent:** Each test should be self-contained
7. **Use Data Providers:** For parameterized tests
8. **Update Locators:** When UI changes
9. **Review Logs:** After test execution for debugging
10. **Maintain Readability:** Use clear and concise code

## Contributing

1. Create a new branch for features
2. Follow Java coding standards
3. Add appropriate logging
4. Update documentation
5. Test thoroughly before submitting

## License

This project is proprietary and confidential.

## Support

For issues or questions:
1. Check the logs in `./logs/` directory
2. Review test reports in `./reports/` directory
3. Consult the traceability matrix in `./docs/`

## References

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [TestNG Documentation](https://testng.org/)
- [Extent Reports](https://www.extentreports.com/)
- [Apache Log4j](https://logging.apache.org/log4j/2.x/)
- [WebDriverManager](https://github.com/bonigarcia/webdrivermanager)

---

**Last Updated:** 2024
**Version:** 1.0.0
**Status:** Active Development
