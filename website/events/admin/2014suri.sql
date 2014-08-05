
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(119000000, '2014-06-10 00:00:00', '2014-06-13 00:00:00', 1, NULL, 'http://ditwww.epfl.ch/EPFLTV/Images/Channel/suri_logo.jpg', 'Workshop on Cyber Risk and Information Security', 'EPFL', NULL, NULL, -1, NULL, NULL, -1, 'ic', NULL, NULL, 'EPFL', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(22000010, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Schedule',                 NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 119000000),
(22000040, 'http://pocketcampus.epfl.ch/images/participants.png',    'Participants',             NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon', NULL, 119000000),
(22000060, 'http://pocketcampus.epfl.ch/images/venue.png',           'Venue',                    NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 119000000),
(22000080, 'http://pocketcampus.epfl.ch/images/favorites.png',       'Favorites',                NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites', NULL, 119000000);


