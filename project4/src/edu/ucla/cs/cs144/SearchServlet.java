package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.cs.cs144.AuctionSearchClient;
import edu.ucla.cs.cs144.SearchResult;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {        
        String searchQuery = request.getParameter("q");
        int numResultsToSkip = Integer.parseInt(request.getParameter("numResultsToSkip"));
        int numResultsToReturn = 20;

        AuctionSearchClient searchClient = new AuctionSearchClient();
        SearchResult[] results = searchClient.basicSearch(searchQuery, numResultsToSkip, numResultsToReturn);
        request.setAttribute("results", results);        
        request.setAttribute("query", searchQuery);

        SearchResult[] nextResults = searchClient.basicSearch(searchQuery, numResultsToSkip + numResultsToReturn, numResultsToReturn);            

        // Set the results to skip               
		String prevResultsToSkipString = "";
		int prevResultsToSkip = numResultsToSkip - numResultsToReturn;
		if (prevResultsToSkip >= 0) {
			prevResultsToSkipString += prevResultsToSkip;
		}		

		String nextResultsToSkipString = "";
		int nextResultsToSkip = numResultsToSkip + numResultsToReturn;
		if (nextResults.length > 0) {
			nextResultsToSkipString += nextResultsToSkip;			
		}		
		        
        request.setAttribute("prevResultsToSkip", prevResultsToSkipString);
        request.setAttribute("nextResultsToSkip", nextResultsToSkipString);        
                
        request.getRequestDispatcher("/search.jsp").forward(request, response);
    }    
}
