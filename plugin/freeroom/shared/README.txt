FreeRoom - SHARED README
Authors: Valentin Minder / Julien Weber

FreeRoom application use THRIFT interface to allow server and client talk to each other. Shared folder also provides a few commun utils, used mainly for time-related objects and check the invariant about the time-check (invariants defining if a time selection is valid or not.

/!\ OTHER CLIENTS MUST CHECK THEIR REQUESTS INVARIANT ON THEIR LANGUAGE BEFORE SENDING IT TO THE SERVER, OTHERWISE THEY WILL RECEIVE BAD REQUESTS RESPONSE FROM THE SERVER. 

*** THRIFT definitions ***

** Commun objects **

* FRRoom *
FRRoom defines a room, with it’s official name and EPFL UID. Other information may be added, like the alias, the surface, the capacity, the building, …
- UID: this is the unique EPFL ID. A room should NEVER be identified by it’s name (it may change, spaces may be added/removed) but only by its UID.
- NAME: Official EPFL Name, of the form “Building [Zone][Floor] RoomNumber”, where [] are optional.
For example, PH D3 395 corresponds to the building “PH”, zone “D”, floor “3” and room number “395”. Spaces position are NEVER garantueed.
- ALIAS: Please note that if the ALIAS is set, it should ALWAYS be used in place of the official name! IS-Academia and most other EPFL services always use alias, BUT a search must also return results based ont eh official name.
- BUILDING: Please note the building should NEVER be derived from the name, EVEN IF the building name is always at the beginning. As spaces position are never guaranteed, the third letter might represent the zone (PHD is PH building, zone D) or the building name (INF, it’s INF building).
- Capacity, surface and UID are additional informations that might be displayed to users upon request/on detailed views.

*FRPeriod*
Defines a period with start and end point in milliseconds (UNIX timestamp, number of seconds from Jan 1 1970, x1000).
Recurrent is always set to false, as this implementation has been canceled.

*Occupancy*
Defines the occupancy of a given room, during a given period.
Stores a list of actual occupation, and meta-data about this list: if it’s free at least once, if it’s occupied at least once, the total period covered, and worst case ratio of free ActualOccupation.

*ActualOccupation*
Defines a unique occupation (occupancy), for a single period, if it’s free or not. If it’s free, the ratio is the percentage of capacity that is used by people who said there are there. DON’T use the number of people, always the RATIO !

** I/O objects **

*General request*
They always have a user group set. The default in 10, and all values between 1 and 20 give valuable results.

*General replies*
Replies always contains an integer status, following the HTTP code convention. Used values are 200 for OK, 400 for bad request (client-side error) and 500 for internal error (server-side error). If the result is 200 (OK), then the reply contains an actual usable reply.

*FRRequest*
Check the occupancy of room for a given period.
You may precise if you want only free rooms, or all the rooms you selected.
If the set of rooms is empty/null, checks for all the room.
WARNING: you can check all the room ONLY if you want only free room. The server will reply with a bad request otherwise!

*FRReply*
A map of Occupancy by building, one for each room selected/free.

*Autocomplete Request*
The first letter of room name, alias, building or UID. There is a minimum (thrift def, currently 2) of letters to provide!

*Autocomplete*
A map of list of FRRoom, an entry key for each building.
Limited in size on server-side (thrift def, currently 50)

*others*
Follows the same logic! :)

*** Commun methods/utils ***
*** TIME INVARIANTS ***
Both clients and server must check and enforces these invariants (o/w server sends a bad request)
To be valid, a period of time must be:
- the end after the start
- same year
- same month
- same day of week
- having a minimal time elapsed (thrift def, currently 5 minutes)
- not more than 24h
- if weekend are not accepted: only week days
- if evenings are not accepted: not after the limit hour in evening, not before the beginning hour in the morning (thrift definition for hours, currently 8am-19pm)
- limited in time in future (thrift def, currently 4 weeks)
- limited in time in past (thrift def, currently 1 week)