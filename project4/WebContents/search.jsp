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
			<p>ID: <%= item.getItemId() %> Name: <%= item.getName() %></p>
	    <%
		}
	%>
</body>
</html>