<%@page import="edu.ucla.cs.cs144.SearchResult" %>

<!DOCTYPE html>
<html>
<head>
	<title>Search Results</title>
    <script type="text/javascript" src="autosuggest.js"></script>
    <script type="text/javascript" src="suggestions.js"></script>
    <link rel="stylesheet" type="text/css" href="autosuggest.css" />        
    <script type="text/javascript">
        window.onload = function () {
            var oTextbox = new AutoSuggestControl(document.getElementById("searchTextBox"), new StateSuggestions());        
        }
    </script>
</head>
<body>
	<h2>Keyword Search</h2>
	<form autocomplete="off">
		Name: <input type="text" name="q" id="searchTextBox">
		<input type="hidden" name="numResultsToSkip" value="0">		
		<input type="submit" value="Search">
	</form>
	<br>
	<br>
	<br>
	<h2>Search Results: </h2>
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