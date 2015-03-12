package edu.ucla.cs.cs144;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ConfirmationServlet extends HttpServlet implements Servlet {
       
    public ConfirmationServlet() {}  

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {        
        HttpSession session = request.getSession(true);
        String itemId = (String) session.getAttribute("itemId");
        String itemName = (String) session.getAttribute("itemName");
        String buyPrice = (String) session.getAttribute("buyPrice");
        String creditCardNumber = request.getParameter("creditCardNumber");

		request.setAttribute("itemId", itemId);
		request.setAttribute("itemName", itemName);
		request.setAttribute("buyPrice", buyPrice);		        
        request.setAttribute("creditCardNumber", creditCardNumber);

        // Get the current timestamp
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String currentTimestamp = dateFormat.format(date);
        request.setAttribute("currentTimestamp", currentTimestamp);
                
        request.getRequestDispatcher("/confirmation.jsp").forward(request, response);
    }    
}