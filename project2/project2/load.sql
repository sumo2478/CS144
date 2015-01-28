-- Load Users into Database
LOAD DATA LOCAL INFILE 'User.dat' 
INTO TABLE User 
FIELDS OPTIONALLY ENCLOSED BY '"'
TERMINATED BY '|*|';

-- Load Items into Database
LOAD DATA LOCAL INFILE 'Item.dat' 
INTO TABLE Item 
FIELDS OPTIONALLY ENCLOSED BY '"'
TERMINATED BY '|*|';