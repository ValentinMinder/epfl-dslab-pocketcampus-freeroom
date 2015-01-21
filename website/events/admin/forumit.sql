
INSERT INTO `eventitems` 
SET 
	`eventId` 		= 126000000, 
	`startDate` 	= '2015-02-03 14:00:00', 
	`endDate` 		= '2015-02-03 16:30:00', 
	`fullDay` 		= NULL, 
	`eventPicture` 	= NULL, 
	`eventThumbnail` = 'http://vpsi.epfl.ch/files/content/sites/vpsi/files/visuel_VPSI.jpg', 
	`eventTitle` 	= 'Information Systems Forum', 
	`eventTitle_fr`	= 'Forum des Systèmes d’Information', 
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
	`secondLine` 	= 'SwissTech Convention Center', 
	`timeSnippet` 	= NULL, 
	`hideEventInfo` = 1, 
	`hideTitle` 	= NULL, 
	`hideThumbnail` = NULL, 
	`isProtected` 	= 1;



INSERT INTO `eventpools` 
SET
	`poolId` 		= 26000010, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/schedule.png', 
	`poolTitle` 	= 'Schedule', 
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
	`parentEvent` 	= 126000000;



INSERT INTO `eventpools` 
SET
	`poolId` 		= 26000020, 
	`poolPicture` 	= 'http://pocketcampus.epfl.ch/images/info.png', 
	`poolTitle` 	= 'Ask a question', 
	`poolPlace` 	= NULL, 
	`poolDetails` 	= NULL, 
	`disableStar` 	= 1, 
	`disableFilterByCateg` = 1, 
	`disableFilterByTags` = 1, 
	`enableScan` 	= NULL, 
	`refreshOnBack` = NULL, 
	`sendStarred` 	= NULL, 
	`noResultText` 	= 'Coming soon.', 
	`overrideLink` 	= 'http://test-pocketcampus.epfl.ch/backend/comment/question.php', 
	`parentEvent` 	= 126000000;





#(25000010, 'http://pocketcampus.epfl.ch/images/participants.png',    'Directory',      NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
#(25000015, 'http://pocketcampus.epfl.ch/images/person.png',          'My profile',     NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
#(25000020, 'http://pocketcampus.epfl.ch/images/dollar.png',          'Contribute',     NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),

#(25000025, 'http://pocketcampus.epfl.ch/images/venue.png',           'Alumni near me', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
#(25000030, 'http://pocketcampus.epfl.ch/images/icons/newspaper.png', 'EPFL news',      NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
#(25000035, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Events',         NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),

#(25000040, 'http://pocketcampus.epfl.ch/images/labs.png',            'Chapters',       NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
#(25000045, 'http://pocketcampus.epfl.ch/images/affiliates.png',      'EPFL alumni office',          NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
#(25000050, 'http://pocketcampus.epfl.ch/images/info.png',            'About',          NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000);





