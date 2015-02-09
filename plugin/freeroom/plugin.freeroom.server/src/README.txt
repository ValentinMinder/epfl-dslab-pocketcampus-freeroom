Some notes about the server.


---------- About inserting occupancies ---------

Occupancies stored in the database should have the seconds and milliseconds of the timestamps rounded to 0.

Users occupancies and rooms occupancies are stored in two differents table, see create-tables.sql

Rooms occupancies are inserted as we receive it (we do not check if some occupancies overlap, if so, the reply to the user handles such cases).
Users occupancies start period should be rounded to a full hour.

When a user request some informations about rooms, it basically do :
	1) retrieves all data during the given period of time (rooms + users occupancies)
	2) pass them to an instance of the class OccupancySorted which get all the data and then process it. It deals with many things, including overlaps, blank periods of time.

-------- About updating data --------

The server auto-updates itself daily if necessary. When updating it deletes all the rooms occupancies from the given day. 

AutoUpdate class help the server decides when it needs to be updated.
PeriodicallyUpdate is a Thread performing the whole update (cleaning, adding the new data). It uses FetchOccupancyDataJSON to fetch, extract and insert data.
	Note that by calling FetchOccupancyDataJSON.fetchAndInsert(long, long) it will start a transaction on the table for rooms occupancies. The commit is performed in PeriodicallyUpdate once it returns.
FetchRoomsDetails is used to build the list of rooms and their attributes. The class RebuildDB can be used to rebuild the list of rooms.


