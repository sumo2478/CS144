<%@page import="java.text.*" %>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%@page import="edu.ucla.cs.cs144.Bid" %>

<!DOCTYPE html>
<html>
<head>
	<title>Item Results</title>
	<style type="text/css">
		#map_canvas { height:100% }
	</style>
</head>

<body onload="initialize()">
	<h2>Search For Item:</h2>
	<form action="/eBay/item">
		ID: <input type="text" name="id">
		<input type="submit" value="Search">
	</form>

	<%
		if (request.getAttribute("error") != null) {
		%>
			<h2>No item matched the given id</h2>
		<%
		}
		else {
		%>
			<h2>Item Result:</h2>
			<h3>Name: <%= request.getAttribute("name") %></h3>
			<p>ID: <%= request.getAttribute("itemId") %></p>			
			<p>Currently: <%= request.getAttribute("currently") %></p>
			<p>First Bid: <%= request.getAttribute("firstBid") %></p>

			<%-- Set the Buy Price--%>
			<%
				String buyPrice = (String) request.getAttribute("buyPrice");		
				if (buyPrice != "") {
				%>
					<p>Buy Price: <%= buyPrice %></p>   <form action="/eBay/buy"><input type="submit" value="Pay Now"></form> 
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
					<p>Latitude: <%= latitude %> Longitude: <%= longitude %></p>	
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
			<br>
			<br>

			<div id="map_canvas" style="width:100%; height:500px"></div>
			<br>
		</body>
		<%
		}
	%>

<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<script type="text/javascript">
	function initialize() {
		var latitude = "<%= request.getAttribute("latitude") %>";
		var longitude = "<%= request.getAttribute("longitude") %>";
		var latlng = null;

		if (latitude != "" && longitude != "") {
			latlng = new google.maps.LatLng(latitude, longitude);	

			var myOptions = {
				zoom:14,
				center: latlng,
				mapTypeId: google.maps.MapTypeId.ROADMAP
			};
			var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

			var marker = new google.maps.Marker({
		    	position: latlng,
		    	map: map,
		    	title:"<%= request.getAttribute("name") %>"
			});
			marker.setMap(map);
		}
		else {
			var geocoder = new google.maps.Geocoder();
			var address = "<%= request.getAttribute("location") %>";
			var country = "<%= request.getAttribute("country") %>";
			address = address + ", " + country;					

			geocoder.geocode({'address': address}, function (results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					var myOptions = {
						zoom:14,
						mapTypeId: google.maps.MapTypeId.ROADMAP
					};
					var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
			        map.setCenter(results[0].geometry.location);
			        
			        var marker = new google.maps.Marker({
			            map: map,
			            position: results[0].geometry.location
			        });			        
			    } 
			});
		}
	}
</script>

</html>