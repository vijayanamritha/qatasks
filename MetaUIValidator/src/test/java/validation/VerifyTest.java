    package validation;

	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.chrome.ChromeDriver;
	import org.testng.Assert;
	import org.testng.annotations.*;

	import pages.ProjectExplorerPage;
	import pages.ProjectDetailsPage;
	import utils.JsonParserUtil.*;
	import utils.JsonParserUtil;

	import java.time.Duration;
	import java.util.ArrayList;
	import java.util.List;

	/**
	 * VerifyTest.java
	 *
	 *  - Parse JSON (JsonParserUtil)
	 *  - For each resource type: navigate, read details from UI, compare with JSON
	 *  - Collect missing / mismatches for that run if found
	 *  - At the end of each test method, fail if any issues found (show summary)
	 */
	public class VerifyTest {

	    WebDriver driver;
	    ProjectExplorerPage explorer;
	    ProjectDetailsPage details;

	    // Path to JSON file (adjust if necessary)
	    String jsonPath = System.getProperty("user.dir")
	            + "\\src\\test\\resources\\Jsonfiles\\QAChallengeJSON.json";

	    // Global lists for the final summary (optional)
	    List<String> globalMissing = new ArrayList<>();
	    List<String> globalMismatches = new ArrayList<>();
	    List<String> globalVerified = new ArrayList<>();

	    @BeforeClass
	    public void setUp() {
	        System.out.println("Launching browser...");
	        driver = new ChromeDriver();
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	        driver.manage().window().maximize();

	        // TODO: put your app url and login steps here
	        driver.get("https://app url.com"); // update
	        // Example login — replace with your locators/credentials:
	        // driver.findElement(By.id("user")).sendKeys("Admin");
	        // driver.findElement(By.id("password")).sendKeys("Admin123");
	        // driver.findElement(By.xpath("//button[@type=submit]")).click();

	        explorer = new ProjectExplorerPage(driver);
	        details = new ProjectDetailsPage(driver);
	    }

	   
	    // 1) VERIFY ENTITIES (SAMPLESTRUCUTURE)
	   
	    @Test(priority = 1)
	    public void verifyEntities() {
	        System.out.println("\n VERIFYING ENTITIES");

	        List<Entity> entities = JsonParserUtil.parseEntities(jsonPath);

	        List<String> missingResources = new ArrayList<>();  
	        List<String> mismatchedResources = new ArrayList<>();
	        List<String> verifiedResources = new ArrayList<>();

	        int total = 0, verified = 0, failed = 0;

	        for (Entity entity : entities) {
	            total++;
	            String resourceLabel = "Entity:SampleStrucuture " ;
	            System.out.println("\nChecking SampleStrucuture ");

	            //Navigate and open the SampleStructure in UI 
	            try {
	                explorer.openStructEnum("Structures","SampleStrucuture");
	                System.out.println("Navigated to SampleStrucuture");
	            } catch (Exception e) {
	                // Navigation failed -> log and continue to next resource
	                failed++;
	                String msg = "Missing (navigation): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Read UI details (Name)
	            String uiEntityName;
	            try {
	                uiEntityName = details.getEntityName();
	                if (uiEntityName == null) uiEntityName = "";
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (ResourceName): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Compare name from JSON with UI
	            boolean entityOk = true;
	            if (!uiEntityName.equals(entity.entityName)) {
	                entityOk = false;
	                String msg = "Mismatch (name): expected [" + entity.entityName + "] found [" + uiEntityName + "]";
	                mismatchedResources.add(resourceLabel + " | " + msg);
	                globalMismatches.add(resourceLabel + " | " + msg);
	                System.out.println(" - " + msg);
	            }

	            // Read fields from UI (Name, Data Type, PK)
	            List<String> uiFieldNames = new ArrayList<>();
	            List<String> uiFieldTypes = new ArrayList<>();
	            List<Boolean> uiPKs = new ArrayList<>();
	            try {
	                uiFieldNames = details.getUIFieldNames();
	                uiFieldTypes = details.getUIFieldTypes();
	                uiPKs = details.getUIPKStatuses();
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (reading fields): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // if any of the collections are empty -> mark missing and continue
	            if (uiFieldNames.isEmpty()) {
	                failed++;
	                String msg = "Missing (fields): " + resourceLabel;
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }
	            if (uiFieldTypes.isEmpty()) {
	                failed++;
	                String msg = "Missing (field types): " + resourceLabel;
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }
	            if (uiPKs.isEmpty()) {
	                failed++;
	                String msg = "Missing (PK statuses): " + resourceLabel;
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Compare each field by position — collect mismatches/missing fields
	            boolean anyFieldMismatch = false;
	            for (int i = 0; i < entity.fields.size(); i++) {
	                String expectedName = entity.fields.get(i).name;
	                String expectedType = entity.fields.get(i).scalarType;
	                boolean expectedPK = entity.fields.get(i).pk;

	                // name
	                if (i < uiFieldNames.size()) {
	                    if (!uiFieldNames.get(i).equals(expectedName)) {
	                        anyFieldMismatch = true;
	                        String msg = "Mismatch (field name): " + resourceLabel + " | pos " + i
	                                + " expected [" + expectedName + "] found [" + uiFieldNames.get(i) + "]";
	                        mismatchedResources.add(msg);
	                        globalMismatches.add(msg);
	                        System.out.println(" - " + msg);
	                    }
	                } else {
	                    anyFieldMismatch = true;
	                    String msg = "Missing (field name): " + resourceLabel + " | expected field: " + expectedName;
	                    missingResources.add(msg);
	                    globalMissing.add(msg);
	                    System.out.println(" - " + msg);
	                }

	                // type
	                if (i < uiFieldTypes.size()) {
	                    if (!uiFieldTypes.get(i).equals(expectedType)) {
	                        anyFieldMismatch = true;
	                        String msg = "Mismatch (field type): " + resourceLabel + " | field " + expectedName
	                                + " expected [" + expectedType + "] found [" + uiFieldTypes.get(i) + "]";
	                        mismatchedResources.add(msg);
	                        globalMismatches.add(msg);
	                        System.out.println(" - " + msg);
	                    }
	                } else {
	                    anyFieldMismatch = true;
	                    String msg = "Missing (field type): " + resourceLabel + " | field: " + expectedName;
	                    missingResources.add(msg);
	                    globalMissing.add(msg);
	                    System.out.println(" - " + msg);
	                }

	                // pk (primary key)
	                if (i < uiPKs.size()) {
	                    if (uiPKs.get(i) != expectedPK) {
	                        anyFieldMismatch = true;
	                        String msg = "Mismatch (PK): " + resourceLabel + " | field " + expectedName
	                                + " expected [" + expectedPK + "] found [" + uiPKs.get(i) + "]";
	                        mismatchedResources.add(msg);
	                        globalMismatches.add(msg);
	                        System.out.println(" - " + msg);
	                    }
	                } else {
	                    anyFieldMismatch = true;
	                    String msg = "Missing (PK): " + resourceLabel + " | field: " + expectedName;
	                    missingResources.add(msg);
	                    globalMissing.add(msg);
	                    System.out.println(" - " + msg);
	                }
	            }

	            // Decide resource result
	            if (entityOk && !anyFieldMismatch) {
	                verified++;
	                verifiedResources.add(resourceLabel);
	                globalVerified.add(resourceLabel);
	                System.out.println(resourceLabel + " — Found & verified");
	            } else {
	                failed++;
	            }
	        } // end for each entity

	        // Build summary text for this test and fail at end if any problems found
	        System.out.println("\nEntities summary: total=" + total + " | verified=" + verified + " | failed=" + failed);
	        if (!missingResources.isEmpty() || !mismatchedResources.isEmpty()) {
	            StringBuilder summary = new StringBuilder();
	            summary.append("Entity verification found issues:\n");
	            if (!missingResources.isEmpty()) {
	                summary.append("Missing:\n");
	                for (String m : missingResources) summary.append(" - ").append(m).append("\n");
	            }
	            if (!mismatchedResources.isEmpty()) {
	                summary.append("Mismatches:\n");
	                for (String m : mismatchedResources) summary.append(" - ").append(m).append("\n");
	            }
	            // Fail the test with the collected issues
	            Assert.fail(summary.toString());
	        }
	    }

	   
	    //2) VERIFY ENUMERATIONS
	    
	    @Test(priority = 2)
	    public void verifyEnumerations() {
	        System.out.println("\n VERIFYING ENUMERATIONS");

	        List<Enumeration> enums = JsonParserUtil.parseEnumerations(jsonPath);

	        List<String> missingResources = new ArrayList<>();
	        List<String> mismatchedResources = new ArrayList<>();
	        List<String> verifiedResources = new ArrayList<>();

	        int total = 0, verified = 0, failed = 0;

	        for (Enumeration enm : enums) {
	            total++;
	            String resourceLabel = "Enumerations: EnumSample";
	            System.out.println("\nChecking " + resourceLabel);

	            // Navigate and open the EnumSample in UI
	            try {	
	                explorer.openStructEnum("Enums", "EnumSample");
	                System.out.println(" - Navigated to EnumSample");
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (navigation): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Read UI details (Name)
	            String uiEnumName;
	            try {
	                uiEnumName = details.getEnumName();
	                if (uiEnumName == null) uiEnumName = "";
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (detail read): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Compare name from JSON with UI
	            boolean ok = true;
	            if (!uiEnumName.equals(enm.name)) {
	                ok = false;
	                String msg = "Mismatch (name): expected [" + enm.name + "] found [" + uiEnumName + "]";
	                mismatchedResources.add(resourceLabel + " | " + msg);
	                globalMismatches.add(resourceLabel + " | " + msg);
	                System.out.println(" - " + msg);
	            }

	            // Read literals from UI
	            List<String> uiLiterals;
	            try {
	                uiLiterals = details.getUIEnumLiterals();
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (reading literals): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // if any of the collections are empty -> mark missing and continue
	            if (uiLiterals.isEmpty()) {
	                failed++;
	                String msg = "Missing (literals): " + resourceLabel;
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Compare literals by position
	            boolean anyLiteralMismatch = false;
	            for (int i = 0; i < enm.literals.size(); i++) {
	                String expectedLiteral = enm.literals.get(i).constant;
	                if (i < uiLiterals.size()) {
	                    if (!uiLiterals.get(i).equals(expectedLiteral)) {
	                        anyLiteralMismatch = true;
	                        String msg = "Mismatch (literal): " + resourceLabel + " | pos " + i
	                                + " expected [" + expectedLiteral + "] found [" + uiLiterals.get(i) + "]";
	                        mismatchedResources.add(msg);
	                        globalMismatches.add(msg);
	                        System.out.println(" - " + msg);
	                    }
	                } else {
	                    anyLiteralMismatch = true;
	                    String msg = "Missing (literal): " + resourceLabel + " | expected literal: " + expectedLiteral;
	                    missingResources.add(msg);
	                    globalMissing.add(msg);
	                    System.out.println(" - " + msg);
	                }
	            }

	            // Decide resource result
	            if (ok && !anyLiteralMismatch) {
	                verified++;
	                verifiedResources.add(resourceLabel);
	                globalVerified.add(resourceLabel);
	                System.out.println(resourceLabel + " — Found & verified");
	            } else {
	                failed++;
	            }
	        } // end for each enum

	        // Build summary text for this test and fail at end if any problems found
	        System.out.println("\nEnumerations summary: total=" + total + " | verified=" + verified + " | failed=" + failed);
	        if (!missingResources.isEmpty() || !mismatchedResources.isEmpty()) {
	            StringBuilder summary = new StringBuilder();
	            summary.append("Enumeration verification found issues:\n");
	            if (!missingResources.isEmpty()) {
	                summary.append("Missing:\n");
	                for (String m : missingResources) summary.append(" - ").append(m).append("\n");
	            }
	            if (!mismatchedResources.isEmpty()) {
	                summary.append("Mismatches:\n");
	                for (String m : mismatchedResources) summary.append(" - ").append(m).append("\n");
	            }
	            Assert.fail(summary.toString());
	        }
	    }

	    
	    // VERIFY FLOWS
	    
	    @Test(priority = 3)
	    public void verifyFlows() {
	        System.out.println("\n VERIFYING FLOWS");

	        List<Flow> flows = JsonParserUtil.parseFlows(jsonPath);

	        List<String> missingResources = new ArrayList<>();
	        List<String> mismatchedResources = new ArrayList<>();
	        List<String> verifiedResources = new ArrayList<>();

	        int total = 0, verified = 0, failed = 0;

	        for (Flow flow : flows) {
	            total++;
	            String resourceLabel = "Flow: ServerFlow";
	            System.out.println("\nChecking " + resourceLabel);

	            // Navigate and open the SeverFlow in UI
	            try {
	                explorer.openFlow();
	                System.out.println(" - Navigated to SeverFlow");
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (navigation): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Read UI name
	            String uiFlowName;
	            try {
	                uiFlowName = details.getFlowName();
	                if (uiFlowName == null) uiFlowName = "";
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (detail read): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Compare Name from JSON with UI
	            boolean ok = true;
	            if (!uiFlowName.equals(flow.name)) {
	                ok = false;
	                String msg = "Mismatch (name): expected [" + flow.name + "] found [" + uiFlowName + "]";
	                mismatchedResources.add(resourceLabel + " | " + msg);
	                globalMismatches.add(resourceLabel + " | " + msg);
	                System.out.println(" - " + msg);
	            }

	            // Read fields from UI (inputs and types)
	            List<String> uiInputNames;
	            List<String> uiInputTypes;
	            try {
	                uiInputNames = details.getUIFlowInputNames();
	                uiInputTypes = details.getUIFlowInputTypes();
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (reading inputs): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }
	            
	            // if any of the collections are empty -> mark missing and continue
	            if (uiInputNames.isEmpty()) {
	                failed++;
	                String msg = "Missing (flow inputs): " + resourceLabel;
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }
	            if (uiInputTypes.isEmpty()) {
	                failed++;
	                String msg = "Missing (flow input types): " + resourceLabel;
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Compare input name and type by position
	            boolean anyInputMismatch = false;
	            for (int i = 0; i < flow.inputs.size(); i++) {
	                String expectedInput = flow.inputs.get(i).name;
	                String expectedType = flow.inputs.get(i).scalarType;

	                //name
	                if (i < uiInputNames.size()) {
	                    if (!uiInputNames.get(i).equals(expectedInput)) {
	                        anyInputMismatch = true;
	                        String msg = "Mismatch (flow input): " + resourceLabel + " | pos " + i
	                                + " expected [" + expectedInput + "] found [" + uiInputNames.get(i) + "]";
	                        mismatchedResources.add(msg);
	                        globalMismatches.add(msg);
	                        System.out.println(" - " + msg);
	                    }
	                } else {
	                    anyInputMismatch = true;
	                    String msg = "Missing (flow input): " + resourceLabel + " | expected input: " + expectedInput;
	                    missingResources.add(msg);
	                    globalMissing.add(msg);
	                    System.out.println(" - " + msg);
	                }

	                //type
	                if (i < uiInputTypes.size()) {
	                    if (!uiInputTypes.get(i).equals(expectedType)) {
	                        anyInputMismatch = true;
	                        String msg = "Mismatch (flow input type): " + resourceLabel + " | input " + expectedInput
	                                + " expected [" + expectedType + "] found [" + uiInputTypes.get(i) + "]";
	                        mismatchedResources.add(msg);
	                        globalMismatches.add(msg);
	                        System.out.println(" - " + msg);
	                    }
	                } else {
	                    anyInputMismatch = true;
	                    String msg = "Missing (flow input type): " + resourceLabel + " | input: " + expectedInput;
	                    missingResources.add(msg);
	                    globalMissing.add(msg);
	                    System.out.println(" - " + msg);
	                }
	            }

	            // Decide resource result
	            if (ok && !anyInputMismatch) {
	                verified++;
	                verifiedResources.add(resourceLabel);
	                globalVerified.add(resourceLabel);
	                System.out.println(resourceLabel + " — Found & verified");
	            } else {
	                failed++;
	            }
	        } // end flows

	        // Build summary text for this test and fail at end if any problems found
	        System.out.println("\nFlows summary: total=" + total + " | verified=" + verified + " | failed=" + failed);
	        if (!missingResources.isEmpty() || !mismatchedResources.isEmpty()) {
	            StringBuilder summary = new StringBuilder();
	            summary.append("Flow verification found issues:\n");
	            if (!missingResources.isEmpty()) {
	                summary.append("Missing:\n");
	                for (String m : missingResources) summary.append(" - ").append(m).append("\n");
	            }
	            if (!mismatchedResources.isEmpty()) {
	                summary.append("Mismatches:\n");
	                for (String m : mismatchedResources) summary.append(" - ").append(m).append("\n");
	            }
	            Assert.fail(summary.toString());
	        }
	    }

	   
	    // VERIFY VIRTUAL FOLDERS (FileFolder)
	    
	    @Test(priority = 4)
	    public void verifyFileFolders() {
	        System.out.println("\n VERIFYING VIRTUAL FOLDERS");

	        List<FileFolder> folders = JsonParserUtil.parseFileFolders(jsonPath);

	        List<String> missingResources = new ArrayList<>();
	        List<String> mismatchedResources = new ArrayList<>();
	        List<String> verifiedResources = new ArrayList<>();

	        int total = 0, verified = 0, failed = 0;

	        for (FileFolder folder : folders) {
	            total++;
	            String resourceLabel = "FileFolder: " + folder.name;
	            System.out.println("\nChecking Virtual Folder ");

	            // Navigate to Virtual Folders(VF1)
	            try {
	                explorer.openVirtualFolder();
	                System.out.println(" - Navigated to " + folder.name);
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (navigation): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Read UI details
	            String uiFolderName, uiLocation;
	            boolean uiCitizenTool;
	            try {
	                uiFolderName = details.getFolderName();
	                uiLocation = details.getUIFolderLocation();
	                uiCitizenTool = details.isUICitizenToolEnabled();
	            } catch (Exception e) {
	                failed++;
	                String msg = "Missing (folder read): " + resourceLabel + " | " + e.getMessage();
	                missingResources.add(msg);
	                globalMissing.add(msg);
	                System.out.println(" - " + msg);
	                continue;
	            }

	            // Compare name
	            boolean ok = true;
	            if (!uiFolderName.equals(folder.name)) {
	                ok = false;
	                String msg = "Mismatch (folder name): expected [" + folder.name + "] found [" + uiFolderName + "]";
	                mismatchedResources.add(resourceLabel + " | " + msg);
	                globalMismatches.add(resourceLabel + " | " + msg);
	                System.out.println(" - " + msg);
	            }

	            // Compare location
	            if (!uiLocation.equals(folder.location)) {
	                ok = false;
	                String msg = "Mismatch (folder location): expected [" + folder.location + "] found [" + uiLocation + "]";
	                mismatchedResources.add(resourceLabel + " | " + msg);
	                globalMismatches.add(resourceLabel + " | " + msg);
	                System.out.println(" - " + msg);
	            }

	            // Compare citizen tool flag
	            boolean expectedCitizen = Boolean.TRUE.equals(folder.citizenToolEnabled);
	            if (uiCitizenTool != expectedCitizen) {
	                ok = false;
	                String msg = "Mismatch (folder citizenTool): expected [" + expectedCitizen + "] found [" + uiCitizenTool + "]";
	                mismatchedResources.add(resourceLabel + " | " + msg);
	                globalMismatches.add(resourceLabel + " | " + msg);
	                System.out.println(" - " + msg);
	            }
              
	            // Decide resource result
	            if (ok) {
	                verified++;
	                verifiedResources.add(resourceLabel);
	                globalVerified.add(resourceLabel);
	                System.out.println(resourceLabel + " — Found & verified");
	            } else {
	                failed++;
	            }
	        } // end folders

	        // Build summary text for this test and fail at end if any problems found
	        System.out.println("\nVirtual Folders summary: total=" + total + " | verified=" + verified + " | failed=" + failed);
	        if (!missingResources.isEmpty() || !mismatchedResources.isEmpty()) {
	            StringBuilder summary = new StringBuilder();
	            summary.append("FileFolder verification found issues:\n");
	            if (!missingResources.isEmpty()) {
	                summary.append("Missing:\n");
	                for (String m : missingResources) summary.append(" - ").append(m).append("\n");
	            }
	            if (!mismatchedResources.isEmpty()) {
	                summary.append("Mismatches:\n");
	                for (String m : mismatchedResources) summary.append(" - ").append(m).append("\n");
	            }
	            Assert.fail(summary.toString());
	        }
	    }

	    // Final summary
	    @AfterClass
	    public void tearDown() {
	        System.out.println("\n----FINAL SUMMARY ----");
	        System.out.println("Total missing items: " + globalMissing.size());
	        System.out.println("Total mismatches: " + globalMismatches.size());
	        System.out.println("Total verified resources: " + globalVerified.size());

	        if (!globalMissing.isEmpty()) {
	            System.out.println("\nMissing resources:");
	            for (String s : globalMissing) 
	            	{ 
	            	System.out.println(" - " + s);
	            	}
	        }
	        if (!globalMismatches.isEmpty()) {
	            System.out.println("\nMismatched resources:");
	            for (String s : globalMismatches) 
	            	{
	            	System.out.println(" - " + s);
	            	}
	        }
	        if (!globalVerified.isEmpty()) {
	            System.out.println("\nVerified resources:");
	            for (String s : globalVerified) 
	            	{
	            	System.out.println(" - " + s);
	            	}
	        }

	        System.out.println("\nClosing browser...");
	        if (driver != null) driver.quit();
	    }
	}


