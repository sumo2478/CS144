package edu.ucla.cs.cs144;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class Bid {
	public String bidderUserId;
	public String bidderRating;
	public String location;
	public String country;
	public String time;
	public String amount;

    public Bid(Element bidData) {	
    	this.time = getElementTextByTagNameNR(bidData, "Time");
    	this.amount = getElementTextByTagNameNR(bidData, "Amount");

    	// Get the user information from the bidder
    	Element bidderData = getElementByTagNameNR(bidData, "Bidder");
    	this.bidderUserId = bidderData.getAttribute("UserID");
    	this.bidderRating = bidderData.getAttribute("Rating");
    	this.country = getElementTextByTagNameNR(bidderData, "Country");

    	// Retrieve the location information
    	Element locationData = getElementByTagNameNR(bidderData, "Location");
    	this.location = locationData.getTextContent();
	}

	// Helper Parsing Functions

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
}