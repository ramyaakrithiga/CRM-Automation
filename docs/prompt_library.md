# Prompt Library for CRM Automation Framework

## Overview

This document provides a collection of prompts and examples for working with the CRM Automation Testing Framework. These prompts can be used with AI assistants or language models for code generation, testing, and documentation.

---

## Table of Contents

1. [Setup & Installation Prompts](#setup--installation-prompts)
2. [Code Generation Prompts](#code-generation-prompts)
3. [Test Case Prompts](#test-case-prompts)
4. [Debugging Prompts](#debugging-prompts)
5. [Documentation Prompts](#documentation-prompts)

---

## Setup & Installation Prompts

### Prompt 1: Initial Project Setup

```
I need to set up a Selenium automation testing framework in Java for testing 
a CRM application at https://crm.osllc.us/admin/auth/login. 

Requirements:
- Use Maven as build tool
- Use Selenium WebDriver 4.x
- Use TestNG as testing framework
- Use Page Object Model design pattern
- Use WebDriverManager for driver management
- Include Extent Reports for reporting
- Include Log4j for logging
- Create config.properties for configuration

Please provide:
1. Complete Maven POM file with all dependencies
2. Project directory structure
3. Initial configuration files
4. Base classes for page objects and tests
```

### Prompt 2: WebDriver Manager Setup

```
I need to implement a WebDriver manager for my Selenium Java project that:
1. Supports Chrome, Firefox, and Edge browsers
2. Uses WebDriverManager for automatic driver setup
3. Implements thread-safe driver management using ThreadLocal
4. Provides methods to initialize, get, and close drivers
5. Sets implicit and explicit waits
6. Includes logging

Please provide the complete DriverManager class with all necessary methods.
```

### Prompt 3: Configuration Management

```
Create a ConfigManager class that:
1. Reads properties from config.properties file
2. Provides methods to get string, integer, and boolean properties
3. Includes commonly used configuration getters (URL, browser, credentials, etc.)
4. Includes error handling for missing properties
5. Uses static methods for easy access
6. Includes logging

Please provide the complete ConfigManager class implementation.
```

---

## Code Generation Prompts

### Prompt 4: Page Object Class Generation

```
Generate a Page Object class for a Login page that:
1. Extends BasePage
2. Contains the following elements:
   - Username field (name="login")
   - Password field (name="password")
   - Login button (css="button[type='submit']")
   - Error message (css=".error-message")
   - Remember me checkbox (id="remember")
3. Includes methods for:
   - Entering username and password
   - Clicking login button
   - Performing complete login
   - Getting error messages
   - Verifying page elements
   - Clearing fields
4. Uses @FindBy annotation from PageFactory
5. Includes comprehensive logging
6. Includes javadoc comments

URL: https://crm.osllc.us/admin/auth/login
```

### Prompt 5: Utility Class Generation

```
Generate a CommonUtils utility class that provides:
1. Wait operations (explicit waits for visibility, clickability, presence)
2. Element interaction methods (click, type, getText)
3. Dropdown selection methods (by value, by visible text)
4. Element verification methods (isDisplayed, isEnabled)
5. Navigation methods (navigateTo, getCurrentUrl, getPageTitle)
6. Screenshot functionality
7. Frame switching methods
8. JavaScript execution methods
9. Proper exception handling and logging
10. Thread-safe access to WebDriver

Please provide the complete utility class.
```

### Prompt 6: Base Test Class Generation

```
Generate a BaseTest class that:
1. Includes @BeforeClass and @AfterClass methods
2. Initializes WebDriver using DriverManager
3. Navigates to application URL from config
4. Closes driver and cleans up resources
5. Provides helper methods like takeScreenshot
6. Sets up CommonUtils with driver instance
7. Includes comprehensive logging
8. Handles exceptions gracefully

Please provide the complete BaseTest class.
```

---

## Test Case Prompts

### Prompt 7: Login Test Cases

```
Generate 10 comprehensive TestNG test cases for Login functionality:

1. Verify login page elements are visible
2. Test successful login with valid credentials
3. Test login with invalid username
4. Test login with invalid password
5. Test login with empty credentials
6. Verify login page URL
7. Test username field accepts input
8. Test password field accepts input
9. Test clearing input fields
10. Verify login page title

Requirements:
- Use Page Object Model
- Include proper assertions
- Add descriptive logging
- Use @Test annotation with priority and description
- Include javadoc comments
- Handle exceptions appropriately

Credentials:
- Username: admin@crm.com
- Password: Admin@123
```

### Prompt 8: Dashboard Test Cases

```
Generate 10 comprehensive TestNG test cases for Dashboard functionality 
after successful login:

1. Verify dashboard page loads
2. Verify dashboard elements are visible
3. Verify dashboard title
4. Verify dashboard URL
5. Verify page title
6. Verify user greeting display
7. Verify navigation bar
8. Verify dashboard load time
9. Test browser navigation (back/forward)
10. Test page refresh functionality

Requirements:
- Use @BeforeMethod to login before each test
- Include proper assertions
- Add descriptive logging
- Use @Test annotation with priority
- Include javadoc comments
- Handle waits appropriately
```

### Prompt 9: Parameterized Test Cases

```
Generate a parameterized TestNG test case for login with multiple 
credentials using @DataProvider:

Test Scenario: Login with different credentials

Test Data:
| Username | Password | Expected Result |
| admin@crm.com | Admin@123 | Login Success |
| invalid@crm.com | Invalid@123 | Login Failed |
| admin@crm.com | wrong | Login Failed |
| empty | empty | Validation Error |

Requirements:
- Use @DataProvider annotation
- Include multiple test data sets
- Add logging for each iteration
- Include proper assertions
- Handle both success and failure cases
```

---

## Debugging Prompts

### Prompt 10: Troubleshooting WebDriver Issues

```
I'm experiencing WebDriver initialization issues in my Selenium Java project:
- WebDriver is null sometimes
- ChromeDriver not found errors
- Connection timeout to Chrome

What are the common causes and solutions?

Context:
- Using WebDriverManager
- Running tests with Maven
- Using TestNG
- Need thread-safe driver management

Please provide:
1. Common causes of these issues
2. Solutions for each cause
3. Best practices for driver management
4. Code examples for proper implementation
```

### Prompt 11: Element Locator Issues

```
I'm having trouble finding elements in my Selenium tests:
- Element not visible timeout
- StaleElementReferenceException
- Elements found but not clickable

My locators:
- Login button: css="button[type='submit']"
- Username field: name="login"
- Dashboard header: xpath="//h1 | //h2"

How should I:
1. Debug these locator issues?
2. Create more reliable locators?
3. Implement proper waits?
4. Handle dynamic elements?

Please provide solutions and code examples.
```

### Prompt 12: Test Failure Analysis

```
My tests are failing intermittently. I have:
- Login tests sometimes fail with timeout
- Dashboard tests fail when run in parallel
- Screenshots not being captured on failure

Issues to investigate:
1. Wait time configuration
2. Element synchronization
3. Thread safety
4. Resource cleanup

Please provide:
1. Root cause analysis approach
2. Solutions for each issue
3. Code improvements needed
4. Best practices for stable tests
```

---

## Documentation Prompts

### Prompt 13: README Generation

```
Generate a comprehensive README.md file for a Selenium Java automation 
framework that includes:

1. Project overview and objectives
2. Technology stack with versions
3. Project structure explanation
4. Prerequisites and installation steps
5. Configuration instructions
6. How to run tests (various scenarios)
7. Test organization and naming conventions
8. Page Object Model explanation
9. Logging and reporting setup
10. Troubleshooting guide
11. Contributing guidelines
12. Support and contact information

Project Details:
- Framework: Selenium WebDriver 4.x with Java
- URL: https://crm.osllc.us/admin/auth/login
- Test Framework: TestNG
- Reporting: Extent Reports
- Logging: Log4j
```

### Prompt 14: Test Case Documentation

```
Generate comprehensive documentation for test cases that includes:

1. Test case ID and name
2. Test objective
3. Prerequisites
4. Test steps with expected results
5. Test data requirements
6. Pass/Fail criteria
7. Automation status
8. Priority level
9. Related requirements
10. Known issues or limitations

Test Cases to Document:
1. Login Tests (10 tests)
2. Dashboard Tests (10 tests)

Format as a detailed table with all information.
```

### Prompt 15: API Documentation

```
Generate JavaDoc documentation for the following classes:
1. DriverManager
2. ConfigManager
3. CommonUtils
4. LoginPage
5. DashboardPage
6. BaseTest

Each documentation should include:
- Class description
- Purpose and usage
- Methods with parameters and return types
- Usage examples
- Exception information
- Since version and author information

Please provide complete JavaDoc comments for these classes.
```

---

## Advanced Prompts

### Prompt 16: Data-Driven Testing

```
Implement data-driven testing for the CRM login page using:
1. Excel file for test data
2. JSON file for test data
3. CSV file for test data

Requirements:
- Create DataProvider methods to read test data
- Handle multiple data sources
- Include logging for data loading
- Support dynamic test case generation
- Include error handling

Test Scenarios:
- Login with different user roles
- Login with various browsers
- Login at different times

Please provide complete implementation.
```

### Prompt 17: Cross-Browser Testing

```
Implement cross-browser testing framework that:
1. Tests on Chrome, Firefox, and Edge
2. Uses TestNG parallel execution
3. Generates separate reports per browser
4. Handles browser-specific issues
5. Uses WebDriverManager for driver management

Requirements:
- Update POM for parallel testing
- Create browser configuration
- Update test base class
- Implement browser-specific waits
- Generate consolidated reports

Please provide the complete implementation.
```

### Prompt 18: CI/CD Integration

```
Create a Jenkinsfile for CI/CD pipeline that:
1. Checks out code from Git
2. Builds project using Maven
3. Runs automated tests
4. Generates and publishes reports
5. Sends notifications on failure
6. Archives test artifacts

Pipeline Stages:
- Build
- Test
- Report Generation
- Cleanup

Tools: Jenkins, Maven, Git, Slack/Email

Please provide complete Jenkinsfile configuration.
```

---

## Utility Prompts

### Prompt 19: Logging Configuration

```
Create a comprehensive Log4j 2 configuration that:
1. Logs to console with specific format
2. Logs to file with rolling appenders
3. Sets different log levels for different components
4. Includes pattern layout with timestamp, level, class, and message
5. Handles exceptions with full stack trace
6. Supports daily and size-based rolling

Requirements:
- Console appender for INFO level
- File appender for all levels
- Rolling file appender with max 10 files
- Separate logger for Selenium
- Proper file permissions

Please provide complete log4j2.xml configuration.
```

### Prompt 20: Report Generation

```
Implement Extent Reports integration that:
1. Generates HTML reports with test results
2. Includes screenshot on failure
3. Shows system information
4. Provides test duration
5. Generates pass/fail summary
6. Supports multiple reports

Requirements:
- Create ExtentReportListener
- Implement ITestListener interface
- Attach screenshots to failed tests
- Add system information
- Generate timestamped reports
- Support concurrent test execution

Please provide complete implementation.
```

---

## Command Reference

### Maven Commands

```bash
# Build project
mvn clean install

# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=LoginTests

# Run specific test method
mvn clean test -Dtest=LoginTests#testSuccessfulLogin

# Run with specific browser
mvn clean test -Dbrowser=firefox

# Run tests in parallel
mvn clean test -DthreadCount=4

# Generate reports only
mvn clean test -Dskip.exec=true

# Skip tests during build
mvn clean install -DskipTests

# Run tests with debug logging
mvn clean test -X

# Run with specific profile
mvn clean test -Pproduction
```

### TestNG Commands

```bash
# Run specific XML suite
mvn test -Dsuitexml=testng.xml

# Parallel execution by method
mvn test -Dparallel=methods -DthreadCount=4

# Parallel execution by class
mvn test -Dparallel=classes -DthreadCount=4

# Skip specific groups
mvn test -Dgroups=smoke,sanity

# Run only specific groups
mvn test -Dgroups=regression
```

---

## Best Practices

### Code Organization
- Keep page objects in separate files
- Group related test cases in one class
- Use meaningful variable and method names
- Add comprehensive logging
- Document complex logic

### Test Design
- One assertion per test is ideal
- Use @BeforeMethod for setup
- Use @AfterMethod for cleanup
- Keep tests independent
- Use data providers for multiple test data

### Maintenance
- Update locators when UI changes
- Keep documentation updated
- Review and refactor code regularly
- Archive old test reports
- Track test execution trends

---

**Document Version:** 1.0
**Last Updated:** 2024
**Maintained By:** QA Team
