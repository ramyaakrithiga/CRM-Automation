# Quick Start Guide

## CRM Automation Testing Framework

Get started with the CRM automation framework in 5 minutes!

---

## 1. Prerequisites

- Java 11+
- Maven 3.6+
- Chrome browser (or Firefox/Edge)
- Git

Check versions:
```bash
java -version
mvn -version
```

---

## 2. Project Setup

### Clone/Download Project

```bash
cd e:\ramya\Automation\ AI\ training\crm-automation-pipeline
```

### Install Dependencies

```bash
mvn clean install
```

This will:
- Download all dependencies from Maven central repository
- Compile the project
- Run initial setup

---

## 3. Configure Application

### Edit config.properties

File: `src/main/resources/config.properties`

```properties
# Application URL
app.url=https://crm.osllc.us/admin/auth/login

# Browser
browser=chrome              # Options: chrome, firefox, edge
headless=false             # Set to true for headless mode

# Credentials
username=admin@crm.com
password=Admin@123

# Waits (in seconds)
implicit.wait=10
explicit.wait=20
page.load.timeout=30
```

---

## 4. Run Tests

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

### Run with Different Browser
```bash
# Update config.properties or use system property
mvn clean test -Dbrowser=firefox
```

---

## 5. View Reports

### Report Location
```
./reports/TestReport_YYYY_MM_DD_HH_MM_SS.html
```

### View Logs
```
./logs/crm-automation.log
```

### View Screenshots
```
./screenshots/[screenshot_name].png
```

---

## Project Structure

```
crm-automation-pipeline/
├── src/
│   ├── main/java/com/crm/automation/
│   │   ├── config/          → Configuration management
│   │   ├── pages/           → Page Object classes
│   │   ├── utilities/       → Helper utilities
│   │   └── base/            → Base classes
│   ├── test/java/com/crm/automation/
│   │   ├── tests/           → Test cases
│   │   └── listeners/       → Report listeners
│   └── resources/
│       ├── config.properties
│       └── log4j2.xml
├── docs/                    → Documentation
├── tests/                   → Test organization
├── pom.xml                  → Maven configuration
├── testng.xml              → TestNG suite configuration
└── README.md               → Detailed documentation
```

---

## Common Commands

### Build Only (No Tests)
```bash
mvn clean install -DskipTests
```

### Run Tests with Verbose Output
```bash
mvn clean test -X
```

### Run Tests in Parallel
```bash
mvn clean test -DthreadCount=4
```

### Generate HTML Report
```bash
mvn clean test
# Reports generated automatically in ./reports/
```

---

## Test Examples

### Example 1: Login Test
```java
@Test
public void testSuccessfulLogin() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.login("admin@crm.com", "Admin@123");
    
    DashboardPage dashboardPage = new DashboardPage(driver);
    Assert.assertTrue(dashboardPage.isDashboardPageLoaded());
}
```

### Example 2: Dashboard Test
```java
@BeforeMethod
public void loginFirst() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.login("admin@crm.com", "Admin@123");
}

@Test
public void testDashboardElements() {
    DashboardPage dashboardPage = new DashboardPage(driver);
    Assert.assertTrue(dashboardPage.verifyDashboardPageElements());
}
```

---

## Troubleshooting

### Issue: Tests Not Running
**Solution:** Ensure Java and Maven are installed and in PATH
```bash
java -version
mvn -version
```

### Issue: WebDriver Download Failed
**Solution:** Check internet connection. WebDriverManager downloads drivers automatically.

### Issue: Elements Not Found
**Solution:** 
1. Update wait times in config.properties
2. Inspect element in browser and update locators
3. Check browser compatibility

### Issue: Tests Timeout
**Solution:** Increase wait times in config.properties
```properties
explicit.wait=30    # Increase from default 20
page.load.timeout=60 # Increase from default 30
```

### Issue: Reports Not Generated
**Solution:** Check if ./reports/ directory exists and has write permissions

---

## Best Practices

1. **Keep Tests Independent**: Each test should not depend on another
2. **Use Wait Operations**: Avoid Thread.sleep(), use explicit waits
3. **Page Objects**: Always use page objects for element interactions
4. **Logging**: Check logs for debugging
5. **Screenshots**: Review screenshots on failures
6. **Configuration**: Use config.properties for URLs, credentials, timeouts

---

## Next Steps

1. **Review:** Read [README.md](README.md) for detailed documentation
2. **Understand:** Study the Page Object Model pattern
3. **Create:** Add new page objects for additional pages
4. **Write:** Create test cases for more features
5. **Integrate:** Setup Jenkins pipeline for CI/CD

---

## Key Files

| File | Purpose |
|------|---------|
| `src/main/resources/config.properties` | Application configuration |
| `src/main/resources/log4j2.xml` | Logging configuration |
| `src/main/java/com/crm/automation/pages/LoginPage.java` | Login page object |
| `src/test/java/com/crm/automation/tests/LoginTests.java` | Login test cases |
| `pom.xml` | Maven dependencies and build configuration |
| `testng.xml` | TestNG test suite configuration |

---

## Support

For detailed information:
- See [README.md](README.md)
- Check [Traceability Matrix](docs/traceability_matrix.md)
- Review [Prompt Library](docs/prompt_library.md)

---

## Quick Reference Commands

```bash
# Build and test
mvn clean test

# Run specific tests
mvn clean test -Dtest=LoginTests

# Run with Chrome (default)
mvn clean test -Dbrowser=chrome

# Run with Firefox
mvn clean test -Dbrowser=firefox

# Run in headless mode
mvn clean test -Dheadless=true

# Run in parallel (4 threads)
mvn clean test -DthreadCount=4

# Skip tests during build
mvn clean install -DskipTests

# View help
mvn help:describe -Dplugin=surefire
```

---

**Happy Testing! 🚀**

For questions or issues, refer to the detailed [README.md](README.md) file.
