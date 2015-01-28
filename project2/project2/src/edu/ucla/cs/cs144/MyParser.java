/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
	// Processed Variables
	//static HashSet<Item> processedItems = new HashSet<Item>();
	//static HashSet<Bid> processedBids = new HashSet<Bid>();
	static ArrayList<Item> processedItems = new ArrayList<Item>();
	static ArrayList<Bid> processedBids = new ArrayList<Bid>();
	static HashMap<String, User> processedUsers = new HashMap<String, User>();
	
    
	static final int MAX_DESCRIPTION_LENGTH = 4000;
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        
        // Retrieve all items from the document
        Element[] items = getElementsByTagNameNR(doc.getDocumentElement(), "Item");
        
        try {
        	// Process the items from the data
        	processItems(items);
        }
        catch (Exception e) {
        	System.out.println("Error processing item");
        	e.printStackTrace();
        	System.exit(3);
        }
                      
        /**************************************************************/        
    }
    
    static void processItems(Element[] items) {    	
        // TODO: remove data in the current file?

    	// Process each item individually
    	for (int i = 0; i < items.length; i++) {
    		Element itemData = items[i];
    		Item newItem = new Item(itemData);
    		
    		// Add the item to the processed items list
    		processedItems.add(newItem);
    		
    		// Add the bids to the processed bids list
    		processedBids.addAll(newItem.bids);
    		
    		// Add the seller to the user hash set
    		User seller = newItem.seller;
    		addUserToHashMap(seller);
    		
    		// Add all the bidders to the user hash set
    		for (Bid bid : processedBids) {
    			addUserToHashMap(bid.user);
    		}    		
    	}    
    }
    
    static void writeObjectsToFile() {
    	// Write items to file
    	writeItemsToFile(processedItems, "items.dat");
    	
    	// Write users to file
    	writeUsersToFile(processedUsers.values(), "users.dat");
    	
    	// Write bids to file    	
    	writeBidsToFile(processedBids, "bids.dat");
    	
    	// Write locations with latitude and longitude to file
    	writeLocationsToFile(processedItems, processedUsers.values(), "locations.dat");
    	
    	// Write categories to file
    	writeCategoriesToFile(processedItems, "categories.dat");
    }
    
    static void writeItemsToFile(ArrayList<Item> items, String filename) {
    	try {
            FileWriter fileWriter = new FileWriter(filename, true); 
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        	
        	for (Item item : items) {
        		writeDataToFile(bufferedWriter, item.dataFileFormat());
        	}    
        	
        	bufferedWriter.close();
    	}
    	catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
            System.exit(3);
    	}
    }
    
    static void writeUsersToFile(Collection<User> users, String filename) {   	
    	try {
            FileWriter fileWriter = new FileWriter(filename, true); 
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        	
        	for (User user: users) {
        		writeDataToFile(bufferedWriter, user.dataFileFormat());
        	}   
        	
        	bufferedWriter.close();
    	}
    	catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
            System.exit(3);
    	}
    }
    
    static void writeBidsToFile(ArrayList<Bid> bids, String filename) {    	
    	try {
            FileWriter fileWriter = new FileWriter(filename, true); 
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        	
        	for (Bid bid: bids) {
        		writeDataToFile(bufferedWriter, bid.dataFileFormat());
        	}  
        	
        	bufferedWriter.close();
    	}
    	catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
            System.exit(3);
    	}
    }
    
    static void writeLocationsToFile(ArrayList<Item> items, Collection<User> users, String filename) {
    	try {
            FileWriter fileWriter = new FileWriter(filename, true); 
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        	
        	for (Item item: items) {
        		if (item.location.longitude != "" && item.location.latitude != "") {
        			writeDataToFile(bufferedWriter, item.location.dataFileFormat());
        		}
        	}
        	
        	for (User user: users) {
        		if (user.location.longitude != "" && user.location.latitude != "") {
        			writeDataToFile(bufferedWriter, user.location.dataFileFormat());
        		}
        	}
        	
        	bufferedWriter.close();
    	}
    	catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
            System.exit(3);
    	}
    }
    
    static void writeCategoriesToFile(ArrayList<Item> items, String filename) {    	   	
    	try {
            FileWriter fileWriter = new FileWriter(filename, true); 
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        	
        	for (Item item: items) {
        		ArrayList<String> categories = item.categories;
        		for (String category : categories) {
        			String categoryDataFileString = category + "," + item.itemId; 
        			writeDataToFile(bufferedWriter, categoryDataFileString);
        		}
        	}
        	
        	bufferedWriter.close();
    	}
    	catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
            System.exit(3);
    	}
    }
    
    static void addUserToHashMap(User newUser) {
    	String userId = newUser.userId;
    	
		// If the user is already in the hash map
		User storedUser = processedUsers.get(userId);
		if (storedUser != null) {
			// If the user data doesn't have location and the current user does then replace the old one with the new one
			if (storedUser.location.name.equals("") && !newUser.location.name.equals("")) {				
				processedUsers.put(userId, newUser);				
			}
		}    			
		// Otherwise add in the current user
		else {
			processedUsers.put(userId, newUser);
		}
    }

    /**
     * Writes a string to a particular file
     * @param fileName - the string of the file name
     * @param data - the data string to write to the file
     */
    static void writeDataToFile(BufferedWriter bufferedWriter, String data) {
        try {
            bufferedWriter.append(data);
            bufferedWriter.newLine();            
        }
        catch (IOException e) {
            System.out.println("Error writing to file");
            e.printStackTrace();
            System.exit(3);
        }
    }
    
    static String convertDateToDatabaseFormat(String dateToFormat) {
    	String newDate = "";
    	try {
    		SimpleDateFormat originalFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        	Date parsedDate = originalFormat.parse(dateToFormat);
        	
        	SimpleDateFormat newFormat = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS");
        	newDate = newFormat.format(parsedDate);        	        
    	}
    	catch (ParseException e) {
            System.out.println("Error parsing date");
            e.printStackTrace();
            System.exit(3);
    	}
    	
    	return newDate;
    }
	    
    static class dataObjects {
    	ArrayList<Item> processedItems;
    	ArrayList<Bid> processedBids;
    	HashSet<User> processedUsers;
    	
    	public dataObjects() {
        	this.processedItems = new ArrayList<Item>();
        	this.processedBids = new ArrayList<Bid>();
        	this.processedUsers = new HashSet<User>();
    	}
    }
    
    static class Item {
    	String itemId;
    	String name;
    	String currently;
    	String buyPrice;
    	String firstBid;
    	String numBids;
    	String description;    	
        Location location;
        String country;
    	String started;
    	String ended;
    	
    	ArrayList<String> categories;    	
    	User seller;
    	ArrayList<Bid> bids;
    	
    	public Item(Element itemData) {
    		this.categories = new ArrayList<String>();
    		this.bids = new ArrayList<Bid>();
    		
    		this.itemId = itemData.getAttribute("ItemID");
    		this.name = getElementTextByTagNameNR(itemData, "Name");
    		this.currently = getElementTextByTagNameNR(itemData, "Currently");    
    		this.buyPrice = getElementTextByTagNameNR(itemData, "Buy_Price");
    		this.firstBid = getElementTextByTagNameNR(itemData, "First_Bid");
    		this.numBids = getElementTextByTagNameNR(itemData, "Number_of_Bids");    		    		    		    		
            this.country = getElementTextByTagNameNR(itemData, "Country");
    		this.started = convertDateToDatabaseFormat(getElementTextByTagNameNR(itemData, "Started"));
    		this.ended = convertDateToDatabaseFormat(getElementTextByTagNameNR(itemData, "Ends"));
    		
    		// Retrieve description information
    		String descriptionText = getElementTextByTagNameNR(itemData, "Description");
    		if (descriptionText.length() > MAX_DESCRIPTION_LENGTH) {
    			descriptionText = descriptionText.substring(0, MAX_DESCRIPTION_LENGTH-1);
    		}
    		this.description = descriptionText;

    		// Retrieve the location information
    		Element locationData = getElementByTagNameNR(itemData, "Location");
    		this.location = new Location(locationData);
    		
    		// Retrieve the seller information
            Element userData = getElementByTagNameNR(itemData, "Seller");
            this.seller = new User(userData);
            
            // Retrieve the categories information
            Element[] categoryData = getElementsByTagNameNR(itemData, "Category");
            for (int i = 0; i < categoryData.length; i++) {
            	String category = categoryData[i].getTextContent();
            	this.categories.add(category);            	
            }
            
            // Retrieve the bidding information
            Element bidsData = getElementByTagNameNR(itemData, "Bids");
            Element[] bidDataList = getElementsByTagNameNR(bidsData, "Bid");
            for (int i = 0; i < bidDataList.length; i++) {
            	Element bidData = bidDataList[i];
            	Bid newBid = new Bid(bidData, this.itemId);
            	this.bids.add(newBid);            	
            }                       
    	}
    	
    	public String toString() {
    		return "ItemID: " + this.itemId + "\n" + 
    			   "Name: " + this.name + "\n" + 
    			   "Currently: " + this.currently + "\n" + 
    			   "Buy Price: " + this.buyPrice + "\n" + 
    			   "First Bid: " + this.firstBid + "\n" + 
    			   "Number of Bids: " + this.numBids + "\n" +
    			   "Description: " + this.description + "\n" +
                   "Location: " + this.location.name + "\n" +
                   "Country: " + this.country + "\n" +
    			   "Started: " + this.started + "\n" + 
    			   "Ended: " + this.ended + "\n";
    	}

        // Returns a string with the proper format for a dat file
        public String dataFileFormat() {
            return this.itemId + "," +
                   this.name + "," + 
                   this.currently + "," +
                   this.buyPrice + "," +
                   this.firstBid + "," +
                   this.numBids + "," +
                   this.description + "," +
                   this.location.name + "," +
                   this.country + "," +
                   this.started + "," +
                   this.ended;
        }
    }
    
    static class User {
    	String userId;
    	String rating;
    	Location location;
    	String country;

        public User(Element userData) {
           this.userId = userData.getAttribute("UserID");
           this.rating = userData.getAttribute("Rating");           
           this.country = getElementTextByTagNameNR(userData, "Country");
           
	   	   // Retrieve the location information           
	   	   Element locationData = getElementByTagNameNR(userData, "Location");	   	   
	   	   this.location = new Location(locationData);	   	
        }

        public String toString() {           
           return "User Id: " + this.userId + "\n" + 
                  "Rating: " + this.rating + "\n" +
                  "Location: " + this.location.name + "\n" + 
                  "Country: " + this.country;
        }

        public String dataFileFormat() {
        	String locationString;           
            if (this.location == null) {
            	System.out.println("Null");
         	   locationString = "";
            }
            else {         	   
         	   locationString = this.location.name;
            }
            
            return this.userId + "," +
                   this.rating + "," +
                   locationString + "," +
                   this.country;
        }
    }
    
    static class Bid {
    	User user;
    	String itemId;
    	String time;
    	String amount;    	
    	
    	public Bid(Element bidData, String itemId) {
    		this.itemId = itemId;
    		this.time = convertDateToDatabaseFormat(getElementTextByTagNameNR(bidData, "Time"));
    		this.amount = getElementTextByTagNameNR(bidData, "Amount");
    		
    		// Get user information from the bidder
    		Element bidderData = getElementByTagNameNR(bidData, "Bidder");
    		this.user = new User(bidderData);
    	}
    	
    	public String toString() {
    		return "User: " + this.user.toString() + "\n" +
    	           "ItemId: " + this.itemId + "\n" +
    				"Time: " + this.time + "\n" +
    	           "Amount: " + this.amount;
    	}
    	
    	public String dataFileFormat() {
    		return this.user.userId + "," +
    	           this.itemId + "," +
    				this.time + "," +
    	           this.amount;
    	}
    }
    
    static class Location {
    	String name;
    	String latitude;
    	String longitude;
    	
    	public Location(Element locationData) {    		
    		if (locationData == null) {
    			this.name = "";
    			this.latitude = "";
    			this.longitude = "";
    			return;
    		}
    		
    		this.name = locationData.getTextContent();    		
    		this.longitude = locationData.getAttribute("Longitude");
    		this.latitude = locationData.getAttribute("Latitude");
    	}
    	
    	public String toString() {
    		return "Name: " + this.name + "\n" + 
    			   "Longitude: " + this.longitude + "\n" +
    			   "Latitude: " + this.latitude;
    	}
    	
    	public String dataFileFormat() {
    		return this.name + "," +
    	           this.latitude + "," +
    			   this.longitude;
    	}
    }

    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
        
        writeObjectsToFile();
    }
}
