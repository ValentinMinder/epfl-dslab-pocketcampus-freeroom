


REPLACE INTO `eventitems` 
SET 
	`eventId` 		= 128000000, 
	`startDate` 	= '2015-06-22 00:00:00', 
	`endDate` 		= '2015-06-24 00:00:00', 
	`fullDay` 		= 1, 
	`eventPicture` 	= NULL, 
	`eventThumbnail` = 'http://pocketcampus.epfl.ch/events/ecocloud2013/images/ecocloud.logo.png', 
	`eventTitle` 	= 'EcoCloud Annual Event 2015', 
	`eventTitle_fr`	= NULL, 
	`eventPlace` 	= NULL, 
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
	`secondLine` 	= 'Lausanne Palace & Spa', 
	`timeSnippet` 	= NULL, 
	`hideEventInfo` = 1, 
	`hideTitle` 	= NULL, 
	`hideThumbnail` = NULL, 
	`isProtected` 	= 1;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 28000010, 
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
	`parentEvent` 	= 128000000;



REPLACE INTO `eventpools` 
SET
	`poolId` 		= 28000020, 
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
	`parentEvent` 	= 128000000;





REPLACE INTO `eventpools` 
SET
	`poolId` 		= 28000030, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/participants.png', 
	`poolTitle` 	= 'Participants', 
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
	`parentEvent` 	= 128000000;





REPLACE INTO `eventpools` 
SET
	`poolId` 		= 28000040, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/labs.png', 
	`poolTitle` 	= 'Labs', 
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
	`parentEvent` 	= 128000000;




REPLACE INTO `eventpools` 
SET
	`poolId` 		= 28000050, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/affiliates.png', 
	`poolTitle` 	= 'Affiliates', 
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
	`parentEvent` 	= 128000000;




REPLACE INTO `eventpools` 
SET
	`poolId` 		= 28000060, 
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
	`overrideLink` 	= 'pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId=128060001', 
	`parentEvent` 	= 128000000;




REPLACE INTO `eventpools` 
SET
	`poolId` 		= 28000090, 
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
	`parentEvent` 	= 128000000;


