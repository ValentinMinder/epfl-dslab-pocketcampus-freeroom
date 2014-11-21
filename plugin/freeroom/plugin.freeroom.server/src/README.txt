Some notes about the server.


---------- About inserting occupancies ---------

The server assumes data is stored in a certain way in the database, therefore for inserting occupancies (room or user) there is a method you should call. 
That is  public int insertOccupancyDetailedReply(FRPeriod period,
			OCCUPANCY_TYPE type, String uid, String hash, String userMessage);
			
The method 	public boolean insertOccupancy(FRPeriod period, OCCUPANCY_TYPE type,
			String uid, String hash, String userMessage); do exactly the same, the difference is the return value.
			
	
Theses methods manages data differently :

For room occupancies : no special treatment is done we store each occupancy whatever the length is as long as it does not overlap a previous ROOM occupancy already stored. It that case we do nothing.

For user occupancies : We consider each user occupancy to be one hour at least, if more the server cut it into chunks of one hour each. It does not worry about overlap between room and user occupancies as this is taken care later when processing a request for the user.
User occupancies should also have seconds and milliseconds set to 0 to avoid problem when retrieving. 

--------- About getOccupancy ---------

When processing a request for the user, the server gets all the occupancies (room or user) from the database concerned for the time period given.
Then we use a class called OccupancySorted that take as input (there is a method to add) ActualOccupation (class for occupancies) with no particular order for a given room.
When you're done adding ActualOccupation you can call getOccupancy and it will create for you an Occupancy, sorted by timestamps, periods modified accordingly in case of overlap and blank period filled with user occupancies cut by chunks of one hour (with ratio of people in there = 0.0).
One instance of OccupancySorted can manage one room, no more.

-------- About updating data --------

The server provides a class (PeriodicallyUpdate) which is a Runnable. It updates all rooms occupancies but assumes the table `fr-roomslist` is already filled with the sql located in sql/.

If you want to update the details of rooms (`fr-roomslist`) you can do this using the public method in FetchOccupancyDataJSON called public void fetchAndInsertRoomsList(long from, long to);
It will load the page for rooms occupancies and take all the rooms displayed, our recommandation is to use it with a duration of all semester to be sure all the rooms are presents.

