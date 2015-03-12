package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BuyServlet extends HttpServlet implements Servlet {
       
    public BuyServlet() {}  

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {        
        HttpSession session = request.getSession(true);
        String itemId = (String) session.getAttribute("itemId");
        String itemName = (String) session.getAttribute("itemName");
        String buyPrice = (String) session.getAttribute("buyPrice");		        

		request.setAttribute("itemId", itemId);
		request.setAttribute("itemName", itemName);
		request.setAttribute("buyPrice", buyPrice);		        
                
        request.getRequestDispatcher("/buy.jsp").forward(request, response);
    }    
}
