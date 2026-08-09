# Test Data

This directory contains all test data files used by the automation tests.

## Structure

```
data/
├── credentials.json
├── testdata.excel
├── sample_data.csv
└── [Other test data files]
```

## Test Data Files

### credentials.json

Contains user credentials for different roles:

```json
{
  "admin": {
    "username": "admin@crm.com",
    "password": "Admin@123"
  },
  "user": {
    "username": "user@crm.com",
    "password": "User@123"
  },
  "invalid": {
    "username": "invalid@crm.com",
    "password": "Invalid@123"
  }
}
```

### testdata.excel

Excel file containing:
- Login credentials
- User information
- Test scenarios
- Expected results

### sample_data.csv

CSV format test data:

```
username,password,expected_result,notes
admin@crm.com,Admin@123,Success,Valid admin credentials
invalid@crm.com,Invalid@123,Failure,Invalid credentials
admin@crm.com,wrong,Failure,Wrong password
,,Failure,Empty credentials
```

## Test Data Management

### Best Practices

1. **Externalize Test Data**
   - Keep test data separate from test code
   - Use JSON, CSV, or Excel formats
   - Version control test data files

2. **Data Security**
   - Never commit sensitive data
   - Use environment variables for credentials
   - Mask passwords in logs and reports

3. **Data Cleanup**
   - Clean up test data after execution
   - Reset database to known state
   - Use test data factories

4. **Test Data Versioning**
   - Maintain multiple versions
   - Document changes
   - Track data updates

## Using Test Data in Tests

### From config.properties

```java
String username = ConfigManager.getUsername();
String password = ConfigManager.getPassword();
```

### From JSON Files

```java
JSONObject testData = new JSONObject(readJsonFile("credentials.json"));
String username = testData.getJSONObject("admin").getString("username");
```

### From Excel Files

```java
XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream("testdata.excel"));
XSSFSheet sheet = workbook.getSheetAt(0);
String username = sheet.getRow(1).getCell(0).getStringCellValue();
```

### From CSV Files

```java
List<String[]> data = CSVParser.parse("sample_data.csv");
for (String[] row : data) {
    String username = row[0];
    String password = row[1];
}
```

## Data-Driven Testing

### Using @DataProvider

```java
@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return new Object[][] {
        { "admin@crm.com", "Admin@123", true },
        { "invalid@crm.com", "Invalid@123", false },
        { "", "", false }
    };
}

@Test(dataProvider = "loginData")
public void testLogin(String username, String password, boolean expectedResult) {
    // Test implementation
}
```

## Test Data Templates

Create templates for:
- Login credentials
- User profiles
- Business objects
- Expected responses

## Data Generation

For large-scale testing:
- Use data generation libraries
- Create random test data
- Generate unique identifiers
- Use factory patterns

## References

- [Apache POI for Excel](https://poi.apache.org/)
- [JSON Simple](https://github.com/fangyidong/json-simple)
- [OpenCSV](http://opencsv.sourceforge.net/)

## Guidelines

1. Keep test data realistic
2. Use valid email formats
3. Follow business rules
4. Document data dependencies
5. Maintain data consistency
6. Update data with code changes
7. Archive old test data
