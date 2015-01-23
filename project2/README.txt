1. Relations
------------
Item(ItemId [Primary Key], 
     Name, 
     Currently, 
     Buy_Price, 
     First_Bid, 
     Number_of_bids,
     Description,
     UserId, // User ID of the seller
     Started,
     Ends)

User(UserId, 
     Rating, 
     Location, 
     Country)

Location(Name,
         Latitude,
         Longitude)       

Category(Name, 
         ItemId)

Bid(UserId, // User ID of the buyer
    ItemId, 
    Time, 
    Amount)   

2. There are no           


