# API Tests

This directory is designated for API-level testing of the CRM application.

## Overview

API testing involves testing backend services and REST endpoints without UI interaction.

## Structure

```
api/
├── [API test classes]
├── fixtures/
│   ├── requests/
│   └── responses/
└── utils/
    └── [API utility classes]
```

## Technologies

- **Framework:** RestAssured / Apache HttpClient
- **Assertion:** Hamcrest / AssertJ
- **Test Framework:** TestNG

## Implementation Guide

### Step 1: Add Dependencies

Update `pom.xml` with API testing dependencies:

```xml
<!-- REST Assured for API testing -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>

<!-- JSON Path for response parsing -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-path</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>

<!-- Hamcrest for assertions -->
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <version>2.2</version>
    <scope>test</scope>
</dependency>
```

### Step 2: Create API Test Base Class

```java
public class APIBaseTest {
    protected static String baseURL;
    protected static RequestSpecification requestSpec;
    
    @BeforeClass
    public void setUp() {
        baseURL = ConfigManager.getApplicationUrl();
        requestSpec = RestAssured.given()
            .baseUri(baseURL)
            .contentType("application/json");
    }
}
```

### Step 3: Create API Test Cases

```java
public class LoginAPITests extends APIBaseTest {
    @Test
    public void testLoginAPI() {
        given()
            .spec(requestSpec)
            .body("{ \"username\": \"admin\", \"password\": \"Admin@123\" }")
        .when()
            .post("/api/login")
        .then()
            .statusCode(200)
            .body("success", equalTo(true));
    }
}
```

## Common API Test Scenarios

1. **Authentication Tests**
   - Login API
   - Token validation
   - Refresh token
   - Logout API

2. **CRUD Operations**
   - Create resources (POST)
   - Read resources (GET)
   - Update resources (PUT/PATCH)
   - Delete resources (DELETE)

3. **Error Handling**
   - Invalid credentials
   - Missing parameters
   - Unauthorized access
   - Server errors

4. **Performance Tests**
   - Response time
   - Load testing
   - Stress testing

## Running API Tests

```bash
# Run all API tests
mvn clean test -Dtest=com.crm.automation.api.*

# Run specific API test class
mvn clean test -Dtest=LoginAPITests

# Run with specific profile
mvn clean test -Papi-tests
```

## Test Fixtures

### Request Fixtures
Store sample request bodies in `api/fixtures/requests/`

### Response Fixtures
Store sample response bodies in `api/fixtures/responses/`

## Reports

API test reports are generated in the same reports directory as UI tests.

## Best Practices

- Use externalized test data
- Validate response status codes
- Verify response body structure
- Test error scenarios
- Use meaningful assertion messages
- Document API endpoints
- Track API changes
- Version your tests with API versions

## Future Enhancements

- [ ] GraphQL API testing support
- [ ] API performance metrics
- [ ] Contract testing
- [ ] API mocking
- [ ] API security testing
