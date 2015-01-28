-- Create Table From users.dat
CREATE TABLE User
(
	UserId VARCHAR(100),     -- The Id of the user
	Rating INT, 		     -- The rating of the user
	Location VARCHAR(100),   -- User's location
	Latitude VARCHAR(20),    -- User's latitude
	Longitude VARCHAR(20),   -- User's longitude
	Country VARCHAR(100),    -- User's country 
	PRIMARY KEY(UserId)      -- The UserId should be unique
);

-- Create Table From items.dat
CREATE TABLE Item
(
	ItemId VARCHAR(20),        -- Item Id
	Name VARCHAR(200),         -- The Item name
	Currently DECIMAL(8, 2),   -- Current bid price
	Buy_Price DECIMAL(8, 2),   -- Asking buy price
	First_Bid DECIMAL(8, 2),   -- First bid price
	Number_Of_Bids INT,        -- Number of bids
	Description VARCHAR(4000), -- Item Description
	Seller VARCHAR(100),       -- The Seller User Id
	Location VARCHAR(100),     -- Item location
	Latitude VARCHAR(20),      -- Item latitude
	Longitude VARCHAR(20),     -- Item longitude
	Country VARCHAR(100), 	   -- Item country
	Started TIMESTAMP, 		   -- Auction start time
	Ends TIMESTAMP, 		   -- Auction end time
	PRIMARY KEY(ItemId), 	   -- The ItemId should be unique
	FOREIGN KEY (Seller) references User(UserId) -- We check to make sure the Seller id exists in the User table
);

-- Create Table From category.dat
CREATE TABLE Category
(
	CategoryName varchar(100), -- The name of the category
	ItemId VARCHAR(20),        -- The item id of the category
	FOREIGN KEY (ItemId) REFERENCES Item(ItemId) -- We check to make sure the Item id exists in the Item table
);

-- Create Bid From bid.dat
CREATE TABLE Bid
(
	UserId VARCHAR(100), 	-- The Id of the user
	ItemId VARCHAR(20),  	-- The Id of the item
	Time TIMESTAMP,      	-- The time the bid was made
	Amount DECIMAL(8, 2),   -- The amount the bid is for
	FOREIGN KEY (ItemId) REFERENCES Item(ItemId),
	FOREIGN KEY (UserId) REFERENCES User(UserId),
	PRIMARY KEY(UserId, ItemId, Time)
);