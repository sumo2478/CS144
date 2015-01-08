CREATE TABLE Actors
(
	Name VARCHAR(40), -- Actor Name
	Movie VARCHAR(80), -- Movie Name
	Year INTEGER,
	Role VARCHAR(40)
);

-- Load Actors into Database
LOAD DATA LOCAL INFILE '~/data/actors.csv'
INTO TABLE Actors
FIELDS OPTIONALLY ENCLOSED BY '"'
TERMINATED BY ',';

SELECT Name FROM Actors WHERE Movie = 'Die Another Day';

DROP TABLE Actors;