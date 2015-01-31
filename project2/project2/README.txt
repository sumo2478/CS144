1. Relations
------------
Item(ItemId, 
     Name, 
     Currently, 
     Buy_Price, 
     First_Bid, 
     Number_of_bids,
     Description,
     Seller, // User ID of the seller
     Location,
     Latitude,
     Longitude,
     Country,
     Started,
     Ends,
     PRIMARY KEY(ItemId)
     )

User(UserId, 
     Rating, 
     Location, 
     Country,
     PRIMARY KEY(UserId))   

Category(CategoryName, 
         ItemId,
         PRIMARY KEY (Name, ItemId)
         )

Bid(UserId, // User ID of the buyer
    ItemId, 
    Time, 
    Amount
    PRIMARY KEY(UserId, ItemId, Time)
    )   

2. 

ItemId -> Name, Currently, Buy_Price, First_Bid, Number_of_bids, Description, Seller, Location, Latitude, Longitude, Country, Started, Ends
UserId -> Rating, Location, Country
UserId, ItemId, Time -> Amount

3. All relations are in BCNF

4. All relations are in 4NF

