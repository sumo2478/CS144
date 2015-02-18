package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {	
	
	private IndexSearcher searcher = null;
	private QueryParser parser = null;	
	
	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	public AuctionSearch() {
		try {
			searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("/var/lib/lucene/index1/"))));
			parser = new QueryParser("content", new StandardAnalyzer());			
		}
		catch (IOException e) {
			System.out.println("Failed to initialize Auction Search");
			System.out.println(e);
			System.exit(3);
		}
	}
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
		SearchResult[] results = null;		
		
		try {
			ScoreDoc[] hits = queryIndexForItems(query, numResultsToSkip, numResultsToReturn);
			
			for (int i = 0; i < hits.length; i++) {
				if (i < numResultsToSkip) {
					continue;
				}
				
				ScoreDoc hit = hits[i];
				Document doc = searcher.doc(hit.doc);
				String itemId = doc.get("ItemId");
				String name = doc.get("Name");
				
				SearchResult currentSearchResult = new SearchResult(itemId, name);
				searchResults.add(currentSearchResult);			
			}
						
			results = new SearchResult[searchResults.size()];
			results = searchResults.toArray(results);					
		}
		catch (IOException e) {
			System.out.println("Failed to perform basic search");
			System.out.println(e);
			System.exit(3);
		}
				
		return results;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
		SearchResult[] results = null;
		
		// We set the number of results to return to 99999999 in order to query all matches with the given keyword
		ScoreDoc[] hitsFromKeywordMatch = queryIndexForItems(query, 0, 99999999);
		HashSet<String> itemIdsInRegion = queryItemInRegion(region);		
		
		// Need to add the two to find the total number of results we should go through
		int maxNumberOfItemsToLookThrough = numResultsToSkip + numResultsToReturn;
		
		try {
			for (int i = 0; i < hitsFromKeywordMatch.length; i++) {				
				ScoreDoc hit = hitsFromKeywordMatch[i];
				Document doc = searcher.doc(hit.doc);
				String itemId = doc.get("ItemId");
				String name = doc.get("Name");
				
				if (itemIdsInRegion.contains(itemId)) {
					SearchResult currentSearchResult = new SearchResult(itemId, name);
					searchResults.add(currentSearchResult);
				}		
			}	
		}
		catch (IOException e) {
			System.out.println("Failed to complete spatial search");
			System.out.println(e);
			System.exit(3);
		}
		
		ArrayList<SearchResult> offsettedResults = new ArrayList<SearchResult>();
		for (int i = 0; i < searchResults.size(); i++) {
			if (i < numResultsToSkip) {
				continue;
			}
			else if (i > maxNumberOfItemsToLookThrough) {				
				break;
			}
			
			offsettedResults.add(searchResults.get(i));
		}
		
		
		results = new SearchResult[offsettedResults.size()];
		results = offsettedResults.toArray(results);
		
		return results;
	}

	public String getXMLDataForItemId(String itemId) {
		String xmlFormattedItem = "";
		
		try {
			Connection conn = DbManager.getConnection(true);
			
			// Retrieve the item data
			PreparedStatement queryForItemData = conn.prepareStatement(
					"SELECT * FROM Item WHERE ItemId = ?"
			);					
			queryForItemData.setString(1, itemId);
			ResultSet itemData = queryForItemData.executeQuery();
			
			// If item does not exist then just return the empty string
			if (!itemData.next()) {
				return xmlFormattedItem;
			}
			
			// Set the initial Item tag
			xmlFormattedItem = "<Item ItemID=\"" + itemId + "\">\n";
			
			// Set the item name tag
			String itemName = xmlFormatString(itemData.getString("Name"));
			xmlFormattedItem = xmlFormattedItem + "\t<Name>" + itemName + "</Name>\n"; 
			
			// Set the category data
			xmlFormattedItem = xmlFormattedItem + categoryXMLString(conn, itemId);					
			
			// Set the currently number
			String currently = formatToCurrency(itemData.getFloat("Currently"));
			xmlFormattedItem = xmlFormattedItem + "\t<Currently>" + currently + "</Currently>\n";			
			
			// Set the first bid
			String firstBid = formatToCurrency(itemData.getFloat("First_Bid"));			
			xmlFormattedItem = xmlFormattedItem + "\t<First_Bid>" + firstBid + "</First_Bid>\n";			
			
			// Set the number of bids
			String numberOfBids = Integer.toString(itemData.getInt("Number_Of_Bids"));
			xmlFormattedItem = xmlFormattedItem + "\t<Number_of_Bids>" + numberOfBids + "</Number_of_Bids>\n";
			
			// Set the bid data
			xmlFormattedItem = xmlFormattedItem + bidXMLString(conn, itemId);
			if (itemData.getInt("Number_Of_Bids") <= 0) {
				xmlFormattedItem = xmlFormattedItem + "\t<Bids />\n";
			}
			else {
				xmlFormattedItem = xmlFormattedItem + "\t</Bids>\n";
			}			
			
			// Set the Location data
			String location = xmlFormatString(itemData.getString("Location"));
			String latitude = itemData.getString("Latitude");
			String longitude = itemData.getString("Longitude");
			if (!latitude.equals("") && !longitude.equals("")) {
				xmlFormattedItem = xmlFormattedItem + "\t<Location Latitude=\"" + latitude + "\" Longitude=\"" + longitude + "\">" + location + "</Location>\n";
			}
			else {
				xmlFormattedItem = xmlFormattedItem + "\t<Location>" + location + "</Location>\n";
			}
			
			// Set the country data
			String country = xmlFormatString(itemData.getString("Country"));
			xmlFormattedItem = xmlFormattedItem + "\t<Country>" + country + "</Country>\n";
			
			// Set the started data
			String started = formatTimeString(itemData.getTimestamp("Started").toString());
			xmlFormattedItem = xmlFormattedItem + "\t<Started>" + started + "</Started>\n";
			
			// Set the ended data
			String ends = formatTimeString(itemData.getTimestamp("Ends").toString());
			xmlFormattedItem = xmlFormattedItem + "\t<Ends>" + ends + "</Ends>\n";
			
			// Set the seller data
			String sellerId = itemData.getString("Seller");
			ResultSet sellerData = getDataForUser(conn, sellerId);
			if (sellerData.next()) {
				String rating = sellerData.getString("Rating");
				String formattedUserId = xmlFormatString(sellerId);
				
				xmlFormattedItem = xmlFormattedItem + "\t<Seller Rating=\"" + rating + "\" UserID=\"" + sellerId + "\" />\n";
			}
			
			// Set the description data
			String description = xmlFormatString(itemData.getString("Description"));
			xmlFormattedItem = xmlFormattedItem + "\t<Description>" + description + "</Description>\n";
			
			xmlFormattedItem = xmlFormattedItem + "</Item>";
			
			conn.close();
		}
		catch (SQLException ex) {
			System.out.println(ex);
		}
		
		return xmlFormattedItem;
	}
	
	public String echo(String message) {
		return message;
	}
	
	private String formatToCurrency(float amount) {
		return String.format("$%.2f", amount);
	}
	
	private String bidXMLString(Connection conn, String itemId) throws SQLException {
		String bidString = "";
		
		PreparedStatement queryForBidData = conn.prepareStatement(
				"SELECT * FROM Bid WHERE ItemId = ?"
		);
		queryForBidData.setString(1, itemId);
		ResultSet bidData = queryForBidData.executeQuery();
		
		while (bidData.next()) {
			// If the bid string is empty then add in the initial <Bids> tag
			if (bidString.equals("")) {
				bidString = "\t<Bids>\n";
			}
			
			bidString = bidString + "\t\t<Bid>\n";
			
			// Add in the bidder information			
			String bidderId = bidData.getString("UserId");
			ResultSet bidderData = getDataForUser(conn, bidderId);
			if (bidderData.next()) {
				String rating = bidderData.getString("Rating");
				String formattedBidderId = xmlFormatString(bidderId);
				String location = xmlFormatString(bidderData.getString("Location"));
				String country = xmlFormatString(bidderData.getString("Country"));
				
				if (location.equals("") && country.equals("")) {
					bidString = bidString + "\t\t\t<Bidder Rating=\"" + rating + "\" UserID=\"" + formattedBidderId + "\" />\n";
				}
				else {	
					bidString = bidString + "\t\t\t<Bidder Rating=\"" + rating + "\" UserID=\"" + formattedBidderId + "\">\n";
					bidString = bidString + "\t\t\t\t<Location>" + location + "</Location>\n";
					bidString = bidString + "\t\t\t\t<Country>" + country + "</Country>\n";
					bidString = bidString + "\t\t\t</Bidder>\n";
				}										
			}			
			
			// Add in the time information
			String time = formatTimeString(bidData.getTimestamp("Time").toString());
			bidString = bidString + "\t\t\t<Time>" + time + "</Time>\n";
			
			// Add in the amount information
			String amount = formatToCurrency(bidData.getFloat("Amount"));
			bidString = bidString + "\t\t\t<Amount>" + amount + "</Amount>\n";
			
			bidString = bidString + "\t\t</Bid>\n";
			
		}
		
		return bidString;
	}
	
	private String formatTimeString(String timeToFormat) {
    	String newDate = "";
    	
    	try { 
			SimpleDateFormat newFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
			SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
			Date parsedDate = originalFormat.parse(timeToFormat);
			newDate = newFormat.format(parsedDate);
    	}
    	catch (Exception e) {
            System.out.println("Error parsing date");
            e.printStackTrace();
            System.exit(3);
    	}
    	
    	return newDate;
	}
	
	private ResultSet getDataForUser(Connection conn, String userId) throws SQLException {
		PreparedStatement queryForUserData = conn.prepareStatement(
				"SELECT * FROM User WHERE UserId = ?"
		);		
		queryForUserData.setString(1, userId);
		return queryForUserData.executeQuery();
	}
	
	private String categoryXMLString(Connection conn, String itemId) throws SQLException {
		String categoryString = "";
		
		PreparedStatement queryForCategoryData = conn.prepareStatement(
				"SELECT * FROM Category WHERE ItemId = ?"
		);
		queryForCategoryData.setString(1, itemId);
		ResultSet categoryData = queryForCategoryData.executeQuery();
		
		while (categoryData.next()) {
			String categoryName = xmlFormatString(categoryData.getString("CategoryName"));
			categoryString = categoryString + "\t<Category>" + categoryName + "</Category>\n";
		}
		
		return categoryString;
	}
	
	private String xmlFormatString(String stringToFormat) {
		stringToFormat = stringToFormat.replaceAll("<", "&lt;");		
		stringToFormat = stringToFormat.replaceAll(">", "&gt;");
		stringToFormat = stringToFormat.replaceAll("&", "&amp;");
		
		return stringToFormat;
	}
	
	private HashSet<String> queryItemInRegion(SearchRegion region) {
        // Create a connection to the database to retrieve Items from MySQL
		HashSet<String> itemIdResultSet = null;                

		try {
			Connection conn = DbManager.getConnection(true);				
			
			String point1 = region.getLx() + " " + region.getLy();
			String point2 = region.getRx() + " " + region.getLy();
			String point3 = region.getRx() + " " + region.getRy();
			String point4 = region.getLx() + " " + region.getRy();
			
			String queryString = "SELECT ItemId FROM Location WHERE MBRContains(GeomFromText('Polygon((" + point1 + "," + point2 + "," + point3 + "," + point4 + "," + point1 + "))'), Coordinate)";
			PreparedStatement preparedStatement = conn.prepareStatement(
					queryString
				);
			
			ResultSet results = preparedStatement.executeQuery();
			itemIdResultSet = new HashSet<String>();
			while (results.next()) {
				itemIdResultSet.add(results.getString("ItemId"));
			}			
			
			conn.close();
		}
		catch (SQLException ex) {
		    System.out.println(ex);
		}
		
		return itemIdResultSet;
	}
		
	private ScoreDoc[] queryIndexForItems(String query, int numResultsToSkip, int numResultsToReturn) {
		/**
		 * Returns all items matching query with size of numResultsToSkip + numResultsToReturn
		 * @param query - The query to execute
		 * @param numResultsToSkip - The number of results to skip
		 * @param numResultsToReturn - The number of results to return
		 * @return ScoreDoc array containing all the entries that match with the query 
		 */
		ScoreDoc[] results = null;
		
		// The number of results we query for should be the offset + the number of results we want to obtain
		int numResultsToObtain = numResultsToReturn + numResultsToSkip;
		
		try {
			// Initialize index searcher and query parser		
			Query queryObject = parser.parse(query);			
			TopDocs topDocs = searcher.search(queryObject, numResultsToObtain);
			results = topDocs.scoreDocs;			
		}
		catch (IOException | ParseException e) {
			System.out.println("Failed to perform basic search");
			System.out.println(e);
			System.exit(3);
		}
				
		return results;
	}

}
