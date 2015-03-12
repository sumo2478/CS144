<!DOCTYPE html>
<html>
<head>
	<title>Buy Item</title>
</head>
<body>
	<% String confirmationUrl = "https://" + request.getServerName() + ":8443" + request.getContextPath() + "/confirm"; %>
		<%-- If no buy price then cannot buy --%>
	<%
		String buyPrice = (String) request.getAttribute("buyPrice");		
		if (buyPrice != "" && buyPrice != null) {
		%>
			<h2>Buy Item</h2>
			<h3>Name: <%= request.getAttribute("itemName") %></h3>
			<p>ID: <%= request.getAttribute("itemId") %></p>
			<p>Price: <%= request.getAttribute("buyPrice") %></p>

			<form action="<%= confirmationUrl %>" method="post">
				Credit Card Number: <input type="text" name="creditCardNumber">
				<input type="submit" value="Pay">
			</form>			
		<%
		}
		else {
			%><h1>Cannot purchase item because buy price not set</h1><%
		}
	%>

</body>
</html>