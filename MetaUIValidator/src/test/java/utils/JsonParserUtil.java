package utils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
   This class reads and extracts data from JSON for:
   1. Entities
   2. Enumerations
   3. Flows
   4. FileFolder
 */
public class JsonParserUtil {
	
	   //Represents a field inside an Entity (SampleStructure)
	   public static class Field {
	        public String name;
	        public String scalarType;
	        public boolean pk;
	    }
	    //Represents an Entity (like "SampleStructure")//
	    public static class Entity {
	        public String entityName;
	        public List<Field> fields = new ArrayList<>();
	    }
        //Represents a single literal value inside an Enumeration 
	    public static class EnumLiteral {
	        public String constant;
	    }
        //Represents an Enumeration
	    public static class Enumeration {
	        public String name;
	        public List<EnumLiteral> literals = new ArrayList<>();
	    }
        //Represents a single input (name and type) parameter for a Flow
	    public static class FlowInput {
	        public String name;
	        public String scalarType;
	    }
        //Represents a Flow (like "ServerFlow")
	    public static class Flow {
	        public String name;
	        public List<FlowInput> inputs = new ArrayList<>();
	    }
	    //Represents a FileFolder (like "VF1").
	    public static class FileFolder {
	        public String name;
	        public Boolean citizenToolEnabled;
	        public String location;
	    }
	    //ENTITIES PARSING SECTION
	    /*
	     * Reads the "entities" section of the JSON.
	     * Extracts entity name, fields, their types, and PK flag.
	     */
        public static List<Entity> parseEntities(String jsonPath) {
        List<Entity> entities = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            FileReader jpath=new FileReader(jsonPath);
            Object obj = parser.parse(jpath);

            //Go into entities in JSON : result->resources->entities
            JSONObject root = (JSONObject) obj;
            JSONObject result = (JSONObject) root.get("result");
            JSONObject resources = (JSONObject) result.get("resources");
            JSONArray entityArray = (JSONArray) resources.get("entities");

            //Loop through each entity
            for (Object eObj : entityArray) {
                JSONObject entityJson = (JSONObject) eObj;
                //Create an object for entity class
                Entity entity = new Entity();
                
                //Get the entity name
                entity.entityName = (String) entityJson.get("name");
                System.out.println("\n Found Entity:" + entity.entityName);

                //Get "fields" array inside the entities in JSON
                JSONArray fieldsArray = (JSONArray) entityJson.get("fields");
                
                // Loop through fields and extract details(name and type)
                for (Object fObj : fieldsArray) {
                    JSONObject fieldJson = (JSONObject) fObj;
                    Field field = new Field();
                    //Get the field name
                    field.name = (String) fieldJson.get("name");

                    // Get the field type
                    JSONObject typeObj = (JSONObject) fieldJson.get("type");
                    if (typeObj != null) {
                        field.scalarType = (String) typeObj.get("scalarType");
                    } else {
                        field.scalarType = "UNKNOWN";
                    }

                    // Get the Primary key flag
                    Object pkValue = fieldJson.get("pk");
                    field.pk = pkValue!= null && (Boolean) pkValue;

                    /*System.out.println("FieldName:" + field.name +
                                       "| Data Type: " + field.scalarType +
                                       "| PK: " + field.pk);*/

                    entity.fields.add(field);
                }

                entities.add(entity);
            }

        } catch (Exception e) {
            System.out.println("Error parsing Entities: " + e.getMessage());
        }

        return entities;
    }
        
        //ENUMERATIONS PARSING SECTION
        /**
         * Reads the "enumerations" section of the JSON.
         * Extracts enumeration name and its literal values.
         */    
 public static List<Enumeration> parseEnumerations(String jsonPath) {
     List<Enumeration> enumerations = new ArrayList<>();
     try {
         JSONParser parser = new JSONParser();
         FileReader jpath = new FileReader(jsonPath);
         Object obj = parser.parse(jpath);

         //Go into enumerations: result -> resources -> enumerations
         JSONObject root = (JSONObject) obj;
         JSONObject result = (JSONObject) root.get("result");
         JSONObject resources = (JSONObject) result.get("resources");
         JSONArray enumArray = (JSONArray) resources.get("enumerations");
         
         // Loop through each enum object
         if (enumArray != null) {
             for (Object eObj : enumArray) {
                 JSONObject enumJson = (JSONObject) eObj;
                 //Create an object for Enumeration class
                 Enumeration enm = new Enumeration();
                 enm.name = (String) enumJson.get("name");
                 System.out.println("\n Found Enumeration: "+enm.name);
                 
                 // Extract literal values
                 JSONArray literalsArray = (JSONArray) enumJson.get("literals");
                 if (literalsArray != null) {
                     for (Object lObj : literalsArray) {
                         JSONObject litJson = (JSONObject) lObj;
                         EnumLiteral lit = new EnumLiteral();
                         lit.constant = (String) litJson.get("constant");
                         System.out.println("Literal: "+ lit.constant);
                         enm.literals.add(lit);
                     }
                 }
                 enumerations.add(enm);
             }
         }

     } catch (Exception e) {
         System.out.println("Error parsing Enumerations: " + e.getMessage());
     }
     return enumerations;
 }

 // FLOWS PARSING SECTION
 /**
  * Reads the "flows" section of the JSON.
  * Extracts flow name and its input parameters (name + type).
  */
 public static List<Flow> parseFlows(String jsonPath) {
     List<Flow> flows = new ArrayList<>();
     try {
         JSONParser parser = new JSONParser();
         FileReader jpath = new FileReader(jsonPath);
         Object obj = parser.parse(jpath);

      // Go into flows: result -> resources -> flows
         JSONObject root = (JSONObject) obj;
         JSONObject result = (JSONObject) root.get("result");
         JSONObject resources = (JSONObject) result.get("resources");
         JSONArray flowArray = (JSONArray) resources.get("flows");

         //Get the flow name
         if (flowArray != null) {
             for (Object fObj : flowArray) {
                 JSONObject flowJson = (JSONObject) fObj;
                 Flow flow = new Flow();
                 flow.name = (String) flowJson.get("name");
                 System.out.println("\n Found Flow: "+ flow.name);
                 
                 // Extract input parameters (name and scalar type)
                 JSONArray inputArray = (JSONArray) flowJson.get("input");
                 if (inputArray != null) {
                     for (Object inputObj : inputArray) {
                         JSONObject inputJson = (JSONObject) inputObj;
                         FlowInput fi = new FlowInput();
                         fi.name = (String) inputJson.get("name");

                         JSONObject typeObj = (JSONObject) inputJson.get("type");
                         fi.scalarType = typeObj != null ? (String) typeObj.get("scalarType") : "UNKNOWN";

                         System.out.println("Input: " + fi.name + " | Type: " + fi.scalarType);
                         flow.inputs.add(fi);
                     }
                 }
                 flows.add(flow);
             }
         }

     } catch (Exception e) {
         System.out.println("Error parsing Flows: " + e.getMessage());
     }
     return flows;
 }

 //FILEFOLDERS (VIRTUAL FOLDERS) PARSING SECTION
 /**
  * Reads the "fileFolders" section of the JSON.
  * Extracts folder name, citizen tool enabled flag, and location.
  */
 public static List<FileFolder> parseFileFolders(String jsonPath) {
     List<FileFolder> folders = new ArrayList<>();
     try {
         JSONParser parser = new JSONParser();
         FileReader jpath = new FileReader(jsonPath);
         Object obj = parser.parse(jpath);

         // Go into: result -> resources -> fileFolders
         JSONObject root = (JSONObject) obj;
         JSONObject result = (JSONObject) root.get("result");
         JSONObject resources = (JSONObject) result.get("resources");
         JSONArray folderArray = (JSONArray) resources.get("fileFolders");

         if (folderArray != null) {
             for (Object fObj : folderArray) {
                 JSONObject folderJson = (JSONObject) fObj;
                 FileFolder folder = new FileFolder();
                 
                 // Extract folder details(name, citizen tool enabled flag, and location)
                 folder.name = (String) folderJson.get("name");
                 folder.citizenToolEnabled = (Boolean) folderJson.get("isCitizenToolEnabled");
                 folder.location = (String) folderJson.get("location");

                 System.out.println("\n Found FileFolder: "+ folder.name);
                 System.out.println("Citizen Tool Enabled:"+ folder.citizenToolEnabled);
                 System.out.println("Location:"+ folder.location);

                 folders.add(folder);
             }
         }

     } catch (Exception e) {
         System.out.println("Error parsing FileFolders: " + e.getMessage());
         
     }
     return folders;
 }
 //Test run
 public static void main(String[] args) {
     String jsonFilePath = System.getProperty("user.dir")
             + "\\src\\test\\resources\\Jsonfiles\\QAChallengeJSON.json";

     // Parse everything
     List<Entity> entities = parseEntities(jsonFilePath);
     parseEnumerations(jsonFilePath);
     parseFlows(jsonFilePath);
     parseFileFolders(jsonFilePath);
		// Final summary
     System.out.println("\n Finished parsing all sections successfully");
		}
}
