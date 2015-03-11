<!DOCTYPE html>
<html>
<head>
	<title>Confirmation Page</title>
</head>
<body>
	<h2>Purchase Confirmation</h2>	
	<h3>Name: <%= request.getAttribute("itemName") %></h3>
	<p>ID: <%= request.getAttribute("itemId") %></p>
	<p>Price: <%= request.getAttribute("buyPrice") %></p>
	<p>Credit Card: <%= request.getAttribute("creditCardNumber") %></p>
	<p>Timestamp: <%= request.getAttribute("currentTimestamp") %></p>
</body>
</html>