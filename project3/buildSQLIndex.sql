-- Create the Location Table
CREATE TABLE Location
(
	ItemId VARCHAR(100),          -- The Id of the item
	Coordinate GEOMETRY NOT NULL, -- The latitude and longitude of the item	
	PRIMARY KEY(ItemId)
) ENGINE=MyISAM;

-- Populate Table with ItemId, Latitude, and longitude
-- x = latitude y = longitude
INSERT INTO Location (ItemId, Coordinate) 
SELECT ItemId, Point(Latitude, Longitude)
FROM Item
WHERE Latitude != "" AND Longitude != "";

-- Create an index on the Coordinate field
CREATE SPATIAL INDEX Coordinate_Index ON Location(Coordinate);