<%@page import="edu.ucla.cs.cs144.SearchResult" %>

<!DOCTYPE html>
<html>
<head>
	<title>Search Results</title>
</head>
<body>
	<h1>Search Results: </h1>
	<%
		SearchResult[] results = (SearchResult[])request.getAttribute("results");
		for (SearchResult item : results) {
		%>
			<a href="/eBay/item?id=<%= item.getItemId()%>">ID: <%= item.getItemId() %> Name: <%= item.getName() %></a><br>
	    <%
		}		
	%>
	
	<br>
	<br>

	<%
		String nextResultsToSkip = (String) request.getAttribute("nextResultsToSkip");
		String prevResultsToSkip = (String) request.getAttribute("prevResultsToSkip");
		String query = (String) request.getAttribute("query");

		if (prevResultsToSkip != "") {
		%>
			<a href="/eBay/search?q=<%= query %>&numResultsToSkip=<%= prevResultsToSkip %>">Previous</a>
		<%
		}

		if (nextResultsToSkip != "") {
		%>
			<a href="/eBay/search?q=<%= query %>&numResultsToSkip=<%= nextResultsToSkip %>">Next</a>
		<%
		}			
	%>

</body>
</html>