package edu.ucla.cs.cs144;

import java.net.*;
import java.io.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet {
	
	private static String CHAR_ENCODING = "UTF-8";
	private static String CONTENT_TYPE = "text/xml";

    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String url = "http://google.com/complete/search?output=toolbar&q=";
        String query = request.getParameter("q");	
        String queryUrl = url + URLEncoder.encode(query, CHAR_ENCODING);

        URLConnection connection = new URL(queryUrl).openConnection();
        connection.setRequestProperty("Accept-Charset", CHAR_ENCODING);

        InputStream responseData = connection.getInputStream();

        response.setContentType(CONTENT_TYPE);

        PrintWriter out = response.getWriter();

        BufferedReader reader = new BufferedReader(new InputStreamReader(responseData));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        out.println(stringBuilder.toString());        
        
        reader.close();
        out.close();
    }
}
