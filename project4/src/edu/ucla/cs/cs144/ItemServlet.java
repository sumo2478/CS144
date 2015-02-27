package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.xml.sax.InputSource;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.cs.cs144.AuctionSearchClient;
import edu.ucla.cs.cs144.Bid;

// Parsing classes
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

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	// Retrieve the document for the item given the id
		String itemId = request.getParameter("id");
		Document doc = getDocumentForId(itemId);
		if (doc == null) {
			request.setAttribute("error", "No item exists for that id");
			request.getRequestDispatcher("/item.jsp").forward(request, response);
			return;
		}

		Element itemData = doc.getDocumentElement();

		// Set the item ID of the response
		request.setAttribute("itemId", itemId);

		// Set the name of the item
		String name = getElementTextByTagNameNR(itemData, "Name");	
		request.setAttribute("name", name);			

		// Set the currently attribute
		String currently = getElementTextByTagNameNR(itemData, "Currently");
		request.setAttribute("currently", currently);

		// Set the first bid attribute
		String firstBid = getElementTextByTagNameNR(itemData, "First_Bid");
		request.setAttribute("firstBid", firstBid);

		// Set the buy price
		String buyPrice = getElementTextByTagNameNR(itemData, "Buy_Price");
		request.setAttribute("buyPrice", buyPrice);

		// Set the location
		Element locationData = getElementByTagNameNR(itemData, "Location");
		String location = locationData.getTextContent();
		String latitude = locationData.getAttribute("Latitude");
		String longitude = locationData.getAttribute("Longitude");

		request.setAttribute("location", location);
		request.setAttribute("latitude", latitude);
		request.setAttribute("longitude", longitude);

		// Set the country
		String country = getElementTextByTagNameNR(itemData, "Country");
		request.setAttribute("country", country);

		// Set the started
		String started = getElementTextByTagNameNR(itemData, "Started");
		request.setAttribute("started", started);

		// Set the ended
		String ends = getElementTextByTagNameNR(itemData, "Ends");
		request.setAttribute("ends", ends);

		// Set the description
		String description = getElementTextByTagNameNR(itemData, "Description");
		request.setAttribute("description", description);

		// Get the seller information
		Element seller = getElementByTagNameNR(itemData, "Seller");
		String sellerId = seller.getAttribute("UserID");
		String sellerRating = seller.getAttribute("Rating");
		request.setAttribute("sellerId", sellerId);
		request.setAttribute("sellerRating", sellerRating);

		// Get the category information
		Element[] categoryData = getElementsByTagNameNR(itemData, "Category");
		ArrayList<String> categories = new ArrayList<String>();
		for (Element category : categoryData) {
			categories.add(category.getTextContent());
		}
		request.setAttribute("categories", categories);

        // Get the bidder information
        ArrayList<Bid> bids = new ArrayList<Bid>();
        Element bidsData = getElementByTagNameNR(itemData, "Bids");
        Element[] bidDataList = getElementsByTagNameNR(bidsData, "Bid");
        for (int i = 0; i < bidDataList.length; i++) {
        	Element bidData = bidDataList[i];
        	Bid newBid = new Bid(bidData);
        	bids.add(newBid);      	
        }

        // Sort the Bids by chronological order        
	    Collections.sort(bids, new Comparator<Bid>() {
	        @Override public int compare(Bid b1, Bid b2) {
		        if (b1.dateObject.before(b2.dateObject)) {
		            return -1;
		        }
		        else if (b1.dateObject.after(b2.dateObject)) {
		            return 1;
		        }
		        else {
		            return 0;
		        }	            
	        }
	    });

        request.setAttribute("bids", bids);

		request.getRequestDispatcher("/item.jsp").forward(request, response);
    }

    private Document getDocumentForId(String itemId) {
    	// Configure the document parser    	
    	DocumentBuilderFactory factory = null;
    	DocumentBuilder builder = null;

        try {
	    	factory = DocumentBuilderFactory.newInstance();
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

        // Retrieve data from search client        
        String xmlData = AuctionSearchClient.getXMLDataForItemId(itemId);

        // Parse the xml data into usable form        
        Document doc = null;

        try {
        	if (!xmlData.equals("")) {
        		doc = builder.parse(new InputSource(new StringReader(xmlData)));                                   
        	}            
        }
        catch (IOException e) {
            e.printStackTrace();            
        }
        catch (SAXException e) {            
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();            
        }
        catch (Exception e) {
            System.out.println(" An Exception has occurred");
        }         

        return doc;   	
    }

    // Helper functions from MyParser.java from project2
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
}
