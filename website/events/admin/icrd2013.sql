
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(114000000, '2013-06-13 00:00:00', '2013-06-13 00:00:00', 1, NULL, 'http://pocketcampus.epfl.ch/events/icresearchday2013/images/logo.jpg', 'IC Research Day 2013', NULL, NULL, NULL, -1, NULL, NULL, -1, 'ic', NULL, NULL, 'School of Computer and Communication Sciences', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(16000001, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Schedule', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 114000000),
(16000003, 'http://pocketcampus.epfl.ch/images/posters.png', 'Posters', NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon.', NULL, 114000000),
(16000004, 'http://pocketcampus.epfl.ch/images/participants.png', 'Participants', NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon.', NULL, 114000000),
(16000005, 'http://pocketcampus.epfl.ch/images/labs.png', 'Labs', NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'Coming soon.', NULL, 114000000),
(16000009, 'http://pocketcampus.epfl.ch/images/open_labs.png', 'Research Areas', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 114000000),
(16000007, 'http://pocketcampus.epfl.ch/images/info.png', 'Useful Information', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 114000000),
(16000010, 'http://pocketcampus.epfl.ch/images/favorites.png', 'Favorites', NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites', NULL, 114000000);


