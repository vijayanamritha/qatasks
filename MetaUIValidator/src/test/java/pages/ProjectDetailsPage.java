package pages;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * This class reads details from the right-side panel of the UI for:
 *  1. Structures (Entities)
 *  2. Enumerations
 *  3. Flows
 *  4. Virtual Folders
 *
 * These returned details will later be compared with values parsed from JSON.
 */
public class ProjectDetailsPage {

    WebDriver driver;

    public ProjectDetailsPage(WebDriver driver) {
        this.driver = driver;
    }

    //SAMPLESTRUCTURE

    // Get entity (SampleStrucuture) name from the Name text box.
    public String getEntityName() {
    	try {
    		//Provide actual xpath of the element from the DOM
        WebElement nameField = driver.findElement(By.xpath("//input[@aria-label='Name' or @id='name']"));
        return nameField.getAttribute("value").trim();    
    	}
    	catch (Exception e) {
            System.out.println("Unable to get Entity name: " + e.getMessage());
            throw new RuntimeException("Failed to locate Entity name field.", e); 
        }
    }

    // Get all field names listed in the UI table for the SampleStrucuture.
    public List<String> getUIFieldNames() {
        List<String> uiFields = new ArrayList<>();
        try {
        //Provide actual xpath of the elements from the DOM
        List<WebElement> fieldElements = driver.findElements(
                By.xpath("//td[@data-field='name'] | //div[@class='field-name']")
        );
        if (fieldElements.isEmpty())
            throw new RuntimeException("No UI field names found.");
        for (WebElement field : fieldElements) {
            uiFields.add(field.getText().trim());
        }
        }
        catch(Exception e)
        {
        	System.out.println("Unable to get Field names: " + e.getMessage());
        	throw new RuntimeException("Failed to get UI field names.", e); 
        }
        return uiFields;
    }

    // Get all default selected field types from Data Type dropdowns
    public List<String> getUIFieldTypes() {
        List<String> uiTypes = new ArrayList<>();

        try {
            // Locate all dropdown elements (assume all are <select>)
            List<WebElement> dropdownElements = driver.findElements(
                By.xpath("//select[@data-field='type']"));//Provide actual xpath of the elements from the DOM
            if (dropdownElements.isEmpty())
                throw new RuntimeException("No data type dropdowns found.");
              //Iterate through each dropdown
            for (WebElement dropdown : dropdownElements) {
                Select select = new Select(dropdown);

                // Get the currently selected (default) option
                WebElement selectedOption = select.getFirstSelectedOption();

                // Get visible text and trim
                String typeText = selectedOption.getText().trim();

                // Add non-empty values to list
                if (!typeText.isEmpty()) {
                    uiTypes.add(typeText);
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to get Field types: " + e.getMessage());
            throw new RuntimeException("Failed to get UI field types.", e);
        }

        return uiTypes;
    }

    // Get PK (Primary Key) checkbox status for each field.
    public List<Boolean> getUIPKStatuses() {
        List<Boolean> pkStatuses = new ArrayList<>();
        try
        {
        //Provide actual xpath of the elements from the DOM
        List<WebElement> pkCheckboxes = driver.findElements(
                By.xpath("//td[contains(@class, 'pk')]//input[@type='checkbox']"));
        if (pkCheckboxes.isEmpty())
            throw new RuntimeException("No PK checkboxes found.");
        
        for (WebElement checkbox : pkCheckboxes) {
        	//Check if the primary key checkbox is selected or not
            boolean checked = checkbox.isSelected() ||
                    "true".equalsIgnoreCase(checkbox.getAttribute("aria-checked"));
            pkStatuses.add(checked);
        }
        }
        catch(Exception e)
        {
        	System.out.println("Unable to get Primary Key Status: " + e.getMessage());
        	throw new RuntimeException("Failed to get UI PK statuses.", e); 
        }
        return pkStatuses;
    }

    
    //ENUMSAMPLE
   
    /**
     * Get enumeration name (e.g., EnumSample) from the Name input field.
     */
    public String getEnumName() {
        try {
        	//Provide actual xpath of the element from the DOM
            WebElement nameField = driver.findElement(By.xpath("//input[@aria-label='Name' or @id='name']"));
            return nameField.getAttribute("value").trim();
        } catch (Exception e) {
            System.out.println("Unable to get Enumeration name: " + e.getMessage());
            throw new RuntimeException("Failed to locate Enumeration name field.", e);
        }
    }

    /**
     * Get all enumeration literal values (e.g., Munich, Berlin).
     */
    public List<String> getUIEnumLiterals() {
        List<String> literals = new ArrayList<>();
        try {
        	//Provide actual xpath of the element from the DOM
            List<WebElement> literalElements = driver.findElements(
                    By.xpath("//td[@data-field='constant'] | //div[@class='enum-literal']"));
            if (literalElements.isEmpty())
                throw new RuntimeException("No Enumeration literals found.");
            for (WebElement element : literalElements) {
                literals.add(element.getText().trim());
            }
        } catch (Exception e) {
            System.out.println("Unable to get Enum literals: " + e.getMessage());
            throw new RuntimeException("Failed to get Enum literals.", e);
        }
        return literals;
    }

    
    //SERVERFLOW

    /**
     * Get flow name (e.g., ServerFlow) from the Name input field.
     */
    public String getFlowName() {
        try {
        	//Provide actual xpath of the element from the DOM
            WebElement nameField = driver.findElement(By.xpath("//input[@aria-label='Name' or @id='name']"));
            return nameField.getAttribute("value").trim();
        } catch (Exception e) {
            System.out.println("Unable to get Flow name: " + e.getMessage());
            throw new RuntimeException("Failed to locate Flow name field.", e);
        }
    }

    /**
     * Get all input names for the flow (e.g., Input1).
     */
    public List<String> getUIFlowInputNames() {
        List<String> flowInputs = new ArrayList<>();
        try {
        	//Provide actual xpath of the elements from the DOM
            List<WebElement> inputElements = driver.findElements(
                    By.xpath("//td[@data-field='inputName'] | //div[@class='flow-input-name']"));
           
            if (inputElements.isEmpty())
            throw new RuntimeException("No Flow input names found.");
            
            for (WebElement input : inputElements) {
                flowInputs.add(input.getText().trim());
            }
        } catch (Exception e) {
            System.out.println("Unable to get Flow Input names: " + e.getMessage());
            throw new RuntimeException("Failed to get Flow Input names.", e); 
        }
        return flowInputs;
    }

    /**
     * Get all flow input data types (e.g., STRING, INT).
     */
 // Get all default selected Flow Input types from dropdowns
    public List<String> getUIFlowInputTypes() {
        List<String> flowTypes = new ArrayList<>();

        try {
            // Locate all dropdown <select> elements that define input types
            List<WebElement> dropdownElements = driver.findElements(By.xpath("//select[@data-field='inputType']"));
            
            if (dropdownElements.isEmpty())
                throw new RuntimeException("No Flow input type dropdowns found.");

            // Iterate through each dropdown
            for (WebElement dropdown : dropdownElements) {
                Select select = new Select(dropdown);

                // Get the currently selected (default) option
                WebElement selectedOption = select.getFirstSelectedOption();

                // Get visible text and trim
                String selectedText = selectedOption.getText().trim();

                // Add non-empty values to list
                if (!selectedText.isEmpty()) {
                    flowTypes.add(selectedText);
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to get Flow Input types: " + e.getMessage());
            throw new RuntimeException("Failed to get Flow Input types.", e);
        }

        return flowTypes;
    }

    //VIRTUAL FOLDERS (VF1)
    
    /**
     * Get virtual folder name (e.g., VF1) from the details panel.
     */
    public String getFolderName() {
        try {
        	//Provide actual xpath of the element from the DOM
            WebElement nameField = driver.findElement(By.xpath("//input[@aria-label='Name' or @id='folderName' or @id='name']"));
            return nameField.getAttribute("value").trim();
        } catch (Exception e) {
            System.out.println("Unable to get Folder name: " + e.getMessage());
            throw new RuntimeException("Failed to locate Folder name field.", e);
        }
    }

    /**
     * Get virtual folder location (e.g., PRIVATE / PUBLIC).
     */
    public String getUIFolderLocation() {
        try {
        	//Provide actual xpath of the element from the DOM
            WebElement locationField = driver.findElement(By.xpath("//input[@aria-label='Location' or @id='location']"));
            return locationField.getAttribute("value").trim();
        } catch (Exception e) {
            System.out.println("Folder location not found: " + e.getMessage());
            throw new RuntimeException("Failed to locate Folder Location field.", e);
        }
    }

    /**
     * Get Citizen Tool Enabled toggle (true/false).
     */
    public boolean isUICitizenToolEnabled() {
        try {
        	//Provide actual xpath of the element from the DOM
            WebElement toggle = driver.findElement(
                    By.xpath("//input[@type='checkbox' and @aria-label='Citizen Tool Enabled']")
            );
            return toggle.isSelected() ||
                    "true".equalsIgnoreCase(toggle.getAttribute("aria-checked"));
        } catch (Exception e) {
            System.out.println("Citizen Tool toggle not found, returning false.");
            throw new RuntimeException("Failed to locate Citizen Tool checkbox.", e);
        }
    }
}
