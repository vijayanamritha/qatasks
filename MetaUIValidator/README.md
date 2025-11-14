# MetaUIValidator

## Overview

This project is a test automation framework built using Java, Selenium WebDriver, TestNG, and Maven.
It is designed for UI validation for comparing JSON-defined metadata (e.g., entities, enumerations, flows, and virtual folders) with data shown on the web application’s UI.
The framework follows the Page Object Model (POM) design pattern

## Tech Stack
This project Tech Stack contains:

Language          : Java 17+,
Build Tool        : Maven,
Testing Framework : TestNG,
Automation Tool   : Selenium WebDriver,
JSON Parser       : JSON-simple,
IDE               : Eclipse (Maven Project),
Browser           : Google Chrome,
Driver Management : WebDriverManager

## Project Structure
```
MetaUIValidator/
├── pom.xml                                      # Maven configuration (dependencies, plugins, build settings)
├── testng.xml                                   # TestNG suite configuration
├── README.md                                    # Project documentation
├── src/
│   └── test/
│       ├── java/
│       │   ├── pages/                           # Page Object Model classes (UI interaction logic)
│       │   │   ├── ProjectExplorerPage.java
│       │   │   └── ProjectDetailsPage.java
│       │   ├── utils/                           # Utility classes for JSON parsing
│       │   │   └── JsonParserUtil.java
│       │   └── validation/                      # Test classes containing main verification logic
│       │       └── VerifyTest.java
│       └── resources/
│           └── Jsonfiles/                       # Input data (e.g., JSON structure definitions)
│               └── QAChallengeJSON.json
├── test-output/                                 # Output in html format
```
 
## Design Overview
The design uses a layered structure for a clean separation of responsibilities:
Layers and Descriptions:

JsonParserUtil      : Parses JSON resources and converts them into Java POJOs, 
ProjectExplorerPage : Encapsulates navigation logic (e.g., clicking SampleStructure/EnumSample/Flows in UI),
ProjectDetailsPage  : Handles reading UI details (e.g., names, field types, and PK flags)
VerifyTest          : Core TestNG test class that performs validations and logs results

## Key Features

- Cross-verification of JSON resources with the actual UI  
- Collect-all-errors-then-fail approach — shows complete results for all items before failing  
- Page Object Model (POM) for reuse   
- Readable console and TestNG HTML reports  
- Clear found and verified, mismatch and missing details 
- Final summary report of all entities, enumerations, flows, and folders  

## Prerequisites

Ensure the following are installed:

- Java JDK 17 or later  
- Maven 3.8+  
- Eclipse IDE (latest, with Maven integration)  
- Google Chrome browser  
- ChromeDriver (automatically managed if using WebDriverManager)

## Running the Tests in Eclipse

### 1. Import the Maven Project
- File → Import → Existing Maven Projects  
- Select the folder containing `pom.xml`

### 2. Verify Maven Dependencies
- Ensure the pom.xml includes these dependencies for:Selenium Java,TestNG, JSON-simple, and WebDriverManager (for automatic driver setup).
- Right click the project → Maven → Update Project…

### 3. Verify and update the following

The following values are to be verified and updated in the code. Update them to match your environment:

- jsonPath in VerifyTest: ensure it points to your real JSON file.
- driver.get("https://app url.com") and login code in VerifyTest#setUp(): change to your app URL and real login workflow.
- Locators inside ProjectExplorerPage and ProjectDetailsPage: update them to match the real DOM. Comments in the page classes point out the locator places to change.
- ChromeDriver (or using WebDriverManager): ensure the driver binary version matches the installed Chrome browser.

### 4. Run Using TestNG
- Right-click `testng.xml` → Run As → TestNG Suite  
**or directly:**  
- Right-click `VerifyTest.java` → Run As → TestNG Test

### 5. View Reports
- Results appear in Eclipse’s **Console** and **TestNG Results** tab  
- A detailed HTML report is generated at: /test-output/emailable-report.html
- Open the emailable-report.html file with System Editor
```

## How the Tests Work (`VerifyTest.java`)

1. . Parse JSON  
        - Use JsonParserUtil methods: parseEntities(), parseEnumerations(), parseFlows(), parseFileFolders()

2. For each resource returned:  
       - Navigate and open the resource using ProjectExplorerPage methods  
         Example: openStructEnum("Structures", "SampleStructure")  
       - Read UI data using ProjectDetailsPage methods:  
         Example: getEntityName(), getUIFieldNames(), getUIFieldTypes(), getUIPKStatuses(),  

3. Compare values  
       - Compare JSON and UI values by position for lists (first JSON field → first UI field, etc.)  
       - Use checks like if (i < list.size()) to avoid errors

4. Record 
       - Resources found and verified
       - Any missing resources or mismatches are collected in per-test lists

5. Fail at the end of each test  
       - If any issues are found, Assert.fail(summary) is called  
       - This shows a combined message in the TestNG report for all problems