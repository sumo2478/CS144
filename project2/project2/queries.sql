-- Find the number of users
SELECT COUNT(*) 
FROM User;

-- Find the number of items in "New York"
SELECT COUNT(*) 
FROM Item 
WHERE BINARY location = "New York";

-- Find the number of auctions belonging to exactly four categories
SELECT COUNT(*) 
FROM (SELECT * 
	  FROM Category 
	  GROUP BY ItemId
	  HAVING COUNT(CategoryName) = 4) AS C;

-- Find the ID of current (unsold) auction(s) with the highest bid
SELECT ItemId 
FROM Item
WHERE Currently = (SELECT MAX(Currently)
                   FROM Item
                   WHERE Ends > "2001-12-20 00:00:01" AND Number_of_Bids > 0) AND Number_of_Bids > 0;

-- Find the number of sellers whose rating is higher than 1000
SELECT COUNT(*)
FROM User
WHERE UserId in (SELECT seller 
                 FROM Item) AND rating > 1000;

-- Find the number of users who are both sellers and bidders               
SELECT COUNT(DISTINCT Item.seller)
FROM Item, Bid
WHERE Item.seller = Bid.UserId;

-- Find the number of categories that include at least one item with a bid of more than $100
SELECT COUNT(*)
FROM (SELECT DISTINCT CategoryName
      FROM Category
      WHERE ItemId IN (SELECT DISTINCT ItemID
      	               FROM Bid
      	               WHERE Amount > 100)) as C;