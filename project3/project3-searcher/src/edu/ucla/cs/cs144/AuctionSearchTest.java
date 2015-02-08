package edu.ucla.cs.cs144;

import java.util.Calendar;
import java.util.Date;

import edu.ucla.cs.cs144.AuctionSearch;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearchTest {
	public static void main(String[] args1)
	{
		AuctionSearch as = new AuctionSearch();

		/*String message = "Test message";
		String reply = as.echo(message);
		System.out.println("Reply: " + reply);
		
		String supermanQuery = "superman";
		SearchResult[] queryResults = as.basicSearch(supermanQuery, 0, 20);
		System.out.println("Basic Seacrh Query: " + supermanQuery);
		System.out.println("Received " + queryResults.length + " results");
		for(SearchResult result : queryResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}
		
		SearchResult[] resultsWithOffset = as.basicSearch(supermanQuery, 10, 10);
		System.out.println("Basic Seacrh Query: " + supermanQuery);
		System.out.println("Received " + resultsWithOffset.length + " results");
		for(SearchResult result : resultsWithOffset) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}*/		
		
		/*SearchRegion region =
		    new SearchRegion(33.774, -118.63, 34.201, -117.38); 
		SearchResult[] spatialResults = as.spatialSearch("camera", region, 0, 20);
		System.out.println("Spatial Seacrh");
		System.out.println("Received " + spatialResults.length + " results");
		for(SearchResult result : spatialResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}
		*/
		String itemId = "1045700537";
		String item = as.getXMLDataForItemId(itemId);
		System.out.println("XML data for ItemId: " + itemId);
		System.out.println(item);

		// Add your own test here
		
		String query = "superman";
		SearchResult[] basicResults = as.basicSearch(query, 0, 100);		
		assert (basicResults.length == 68): "Superman query failed got: " + basicResults.length + " should be: 68";
		
		String query2 = "kitchenware";
		SearchResult[] basicResults2 = as.basicSearch(query2, 0, 2000);		
		assert (basicResults2.length == 1462): "kitchenware query failed got: " + basicResults2.length + " should be: 1462";
		
		String query3 = "star trek";
		SearchResult[] basicResults3 = as.basicSearch(query3, 0, 2000);
		assert (basicResults3.length == 770): "star trek query failed got: " + basicResults3.length + " should be: 770";
		
		String query4 = "star trek";
		SearchResult[] basicResult4 = as.basicSearch(query4, 800, 20);
		assert (basicResult4.length == 0): "If offset is greater than total number of elements the result should be 0";
		
		String query5 = "star trek";
		SearchResult[] basicResult5 = as.basicSearch(query5, 500, 800);
		assert (basicResult5.length == 270): "If offset + number of elements to return is greater than total elements it should return the remaining number";
		
		SearchRegion region =
	    new SearchRegion(33.774, -118.63, 34.201, -117.38); 
		SearchResult[] spatialResults = as.spatialSearch("camera", region, 0, 20);		
		assert (spatialResults.length == 17): "spatial search query failed got: " + spatialResults.length + " should be: 17";
		/*System.out.println("Regular");
		for (SearchResult result: spatialResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}*/
		
		SearchRegion region2 =
	    new SearchRegion(33.774, -118.63, 34.201, -117.38); 
		SearchResult[] spatialResults2 = as.spatialSearch("camera", region2, 5, 12);		
        assert (spatialResults2.length == 12): "spatial search query failed got: " + spatialResults2.length + " should be: 12";
        /*System.out.println("Offsetted");
        for (SearchResult result: spatialResults2) {
        	System.out.println(result.getItemId() + ": " + result.getName());
        }*/
				
		
		//SELECT ItemId FROM Location WHERE MBRContains(GeomFromText('Polygon((33.774 -118.63, 34.201 -118.63, 34.201 -117.38, 33.774 -117.38,33.774 -118.63))'), Coordinate);
		
		System.out.println("Tests completed successfully!");
	}
}
