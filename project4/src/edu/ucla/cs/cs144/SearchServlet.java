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
        // int numResultsToReturn = Integer.parseInt(request.getParameter("numResultsToReturn"));
        int numResultsToReturn = 20;

        AuctionSearchClient searchClient = new AuctionSearchClient();
        SearchResult[] results = searchClient.basicSearch(searchQuery, numResultsToSkip, numResultsToReturn);
        request.setAttribute("results", results);
        request.setAttribute("title", "hello");
        request.getRequestDispatcher("/search.jsp").forward(request, response);;
    }    
}
