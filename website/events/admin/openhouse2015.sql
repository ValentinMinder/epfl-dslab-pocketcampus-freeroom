

REPLACE INTO `eventitems` 
SET 
	`eventId` 		= 127000000, 
	`startDate` 	= '2015-03-20 00:00:00', 
	`endDate` 		= '2015-03-21 00:00:00', 
	`fullDay` 		= 1, 
	`eventPicture` 	= NULL, 
	`eventThumbnail` = 'http://pocketcampus.epfl.ch/images/binary_world.png', 
	`eventTitle` 	= 'EDIC Open House 2015', 
	`eventTitle_fr`	= NULL, 
	`eventPlace` 	= 'EPFL', 
	`eventSpeaker` 	= NULL, 
	`eventDetails` 	= NULL, 
	`eventDetails_fr` = NULL, 
	`parentPool` 	= -1, 
	`eventUri` 		= NULL, 
	`vcalUid` 		= NULL, 
	`eventCateg` 	= -1, 
	`broadcastInFeeds` = 'ic', 
	`locationHref` 	= NULL, 
	`detailsLink` 	= NULL, 
	`secondLine` 	= 'School of Computer and Communication Sciences', 
	`timeSnippet` 	= NULL, 
	`hideEventInfo` = 1, 
	`hideTitle` 	= NULL, 
	`hideThumbnail` = NULL, 
	`isProtected` 	= 1;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000010, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/schedule.png', 
	`poolTitle` 	= 'Schedule', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= NULL, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= NULL, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;




REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000020, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/open_labs.png', 
	`poolTitle` 	= 'Research Areas', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= 1, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= NULL, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000030, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/labs.png', 
	`poolTitle` 	= 'Labs', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= NULL, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = NULL, 
	`enableScan` 	= NULL, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000040, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/open_labs.png', 
	`poolTitle` 	= 'Open Lab Sessions', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= NULL, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= 1, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000050, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/participants.png', 
	`poolTitle` 	= 'Prospective PhD Students', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= NULL, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = NULL, 
	`enableScan` 	= 1, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000060, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/participants.png', 
	`poolTitle` 	= 'EPFL Participants', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= NULL, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= 1, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000070, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/posters.png', 
	`poolTitle` 	= 'Posters', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= NULL, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= 1, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000080, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/venue.png', 
	`poolTitle` 	= 'Venue', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= 1, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= NULL, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000090, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/info.png', 
	`poolTitle` 	= 'Useful Information', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= 1, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= NULL, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 27000099, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/favorites.png', 
	`poolTitle` 	= 'Favorites', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= NULL, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= NULL, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= 1, 
	`noResultText` 	= 'No favorites', 
	`overrideLink` 	= NULL, 
	`parentEvent` 	= 127000000;


