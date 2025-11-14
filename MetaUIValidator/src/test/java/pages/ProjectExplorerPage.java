package pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/*
 * This class helps navigate through the Project Explorer on the left side of the UI
 *
 * Supports:
 *   - Structures (e.g., SampleStructure)
 *   - Enumerations (e.g., EnumSample)
 *   - Flows (e.g., ServerFlow)
 *   - Virtual Folders (e.g., VF1)
 *
 * Example hierarchy:
 *   Data -> Models -> SampleModel -> Structures -> SampleStructure
 */
public class ProjectExplorerPage {

    WebDriver driver;

    public ProjectExplorerPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Generic method to click a single node in the Project Explorer.
     * @param nodeName The visible text of the node.
     */
    public void clickNode(String nodeName) {
        try {
            String xpath = "//span[normalize-space(text())='" + nodeName + "']";
            WebElement element = driver.findElement(By.xpath(xpath));
            element.click();
            Thread.sleep(800); // short wait for UI to expand
            System.out.println("Clicked: " + nodeName);
        } catch (Exception e) {
            System.out.println("Could not click node: " + nodeName + " | " + e.getMessage());
            throw new RuntimeException("Failed to click node: " + nodeName, e);
        }
    }

    /**
     * Navigate through a list of node names one by one.
     */
    public void navigatePath(List<String> path) {
        for (String step : path) {
            clickNode(step);
        }
    }

    /**
     * Open SampleStructure/EnumSample
     */
    public void openStructEnum(String modelFolder, String modelName) {
    	//List of inner text of elements to be clicked is passed to navigatePath method
        navigatePath(List.of("Data", "Models","SampleModel",modelFolder,modelName));
        System.out.println("Opened SampleModel:" + modelName);
    }

    /**
     * Opens a ServerFlow
     */
    public void openFlow() {
    	//List of inner text of elements to be clicked is passed to navigatePath method
        navigatePath(List.of("Business Logic","Server Flows","Global Flows","ServerFlow"));
        System.out.println("Opened Flow: ServerFlow");
    }

    /**
     * Opens a Virtual Folder (VF1)
     */
    public void openVirtualFolder() {
    	//List of inner text of elements to be clicked is passed to navigatePath method
        navigatePath(List.of("Resources","Virtual Folders","VF1"));
        System.out.println("Opened Virtual Folder: VF1");
    }
}
