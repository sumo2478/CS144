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
		// TODO: Your code here!
		return "";
	}
	
	public String echo(String message) {
		return message;
	}
	
	private HashSet<String> queryItemInRegion(SearchRegion region) {
        // Create a connection to the database to retrieve Items from MySQL
		HashSet<String> itemIdResultSet = null;
        Connection conn = null;        

		try {
		    conn = DbManager.getConnection(true);				
			
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
