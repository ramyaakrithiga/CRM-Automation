# UI Tests

This directory contains all UI-level automation tests for the CRM application.

## Test Structure

```
ui/
├── LoginTests.java         # Login page test cases
├── DashboardTests.java     # Dashboard page test cases
└── [Other UI test files]
```

## Test Execution

Run UI tests using:

```bash
# Run all UI tests
mvn clean test -Dtest=com.crm.automation.tests.*

# Run login tests
mvn clean test -Dtest=LoginTests

# Run dashboard tests
mvn clean test -Dtest=DashboardTests
```

## Test Categories

### Login Tests (10 tests)
- Page element verification
- Successful login
- Invalid credential handling
- Empty field validation
- Field input verification
- Page URL and title verification

### Dashboard Tests (10 tests)
- Dashboard page load
- Element visibility
- User greeting
- Navigation functionality
- Page refresh
- Browser navigation

## Test Data

Test data is configured in:
- `config.properties` - Application configuration
- Test class @DataProvider methods - Dynamic test data

## Reporting

Test reports are generated in:
- `./reports/TestReport_YYYY_MM_DD_HH_MM_SS.html`
- Console output with Log4j

## Adding New Tests

1. Create new test class extending `BaseTest`
2. Use `@Test` annotation for test methods
3. Use Page Object classes for element interactions
4. Add comprehensive logging
5. Include assertions for verification
6. Update testng.xml with new test class

## Best Practices

- Keep tests independent
- Use descriptive test names
- Add priority to tests
- Include logging statements
- Handle waits properly
- Take screenshots on failure
- Use valid test data
- Keep test data external
- Document complex test logic
