<%@page import="java.text.*" %>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%@page import="edu.ucla.cs.cs144.Bid" %>

<!DOCTYPE html>
<html>
<head>
	<title>Item Results</title>
</head>
<body>
	<h2>Search For Item:</h2>
	<form action="/eBay/item">
		ID: <input type="text" name="id">
		<input type="submit" value="Search">
	</form>

	<h2>Item Result:</h2>
	<h3>ID: <%= request.getAttribute("itemId") %></h3>
	<h3>Name: <%= request.getAttribute("name") %></h3>
	<p>Currently: <%= request.getAttribute("currently") %></p>
	<p>First Bid: <%= request.getAttribute("firstBid") %></p>

	<%-- Set the Buy Price--%>
	<%
		String buyPrice = (String) request.getAttribute("buyPrice");		
		if (buyPrice != "") {
		%>
			<p>Buy Price: <%= buyPrice %></p>
		<%
		}
	%>

	<%-- Set the Location --%>
	<p>Location: <%= request.getAttribute("location") %></p>
	<% 
		String latitude = (String) request.getAttribute("latitude");
		String longitude = (String) request.getAttribute("longitude");
		if (latitude != "" && longitude != "") {
		%>
			<p>Latitude: <%= latitude %> Longitude <%= longitude %></p>	
		<%
		}
	%>	
	<p>Country: <%= request.getAttribute("country") %></p>
	<p>Started: <%= request.getAttribute("started") %></p>
	<p>Ends: <%= request.getAttribute("ends") %></p>
	<p>Description: <%= request.getAttribute("description") %></p>
	<p>Seller ID: <%= request.getAttribute("sellerId") %></p>
	<p>Seller Rating: <%= request.getAttribute("sellerRating") %></p>

	<%-- Set the Categories --%>
	<%
		String categoryString = "Categories: ";
		ArrayList<String> categories = (ArrayList<String>) request.getAttribute("categories");
		for (String category : categories) {
			categoryString = categoryString + category + " | ";
		}		
		%><p><%= categoryString %></p><%
	%>

	<%-- Set the Bidders --%>
	<h3>Bids: </h3>
	<table border="1" style="width:100%">
	<tr>
		<th>Bidder ID</th>
		<th>Bidder Rating</th>
		<th>Time</th>
		<th>Amount</th>
		<th>Location</th>
		<th>Country</th>		
	</tr>
	<%
		ArrayList<Bid> bids = (ArrayList<Bid>) request.getAttribute("bids");
		for (Bid bid : bids) {
		%>	
			<tr>
				<td><%= bid.bidderUserId%></td>
				<td><%= bid.bidderRating%></td>
				<td><%= bid.time%></td>
				<td><%= bid.amount%></td>
				<td><%= bid.location%></td>
				<td><%= bid.country%></td>				
			</tr>
		<%
		}
	%>
	</table>
</body>
</html>