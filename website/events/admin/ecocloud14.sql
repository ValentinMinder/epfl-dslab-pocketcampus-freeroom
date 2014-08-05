
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(120000000, '2014-06-05 00:00:00', '2014-06-07 00:00:00', 1, NULL, 'http://pocketcampus.epfl.ch/events/ecocloud2013/images/ecocloud.logo.png', 'EcoCloud Annual Event 2014', NULL, NULL, NULL, -1, NULL, NULL, -1, 'ic', NULL, NULL, 'Lausanne Palace & Spa', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(23000010, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Schedule', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, 120000000),
(23000070, 'http://pocketcampus.epfl.ch/images/posters.png', 'Posters', NULL, NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, 120000000),
(23000071, 'http://pocketcampus.epfl.ch/images/participants.png', 'Participants', NULL, NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, 120000000),
(23000080, 'http://pocketcampus.epfl.ch/images/labs.png', 'Labs', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 120000000),
(23000081, 'http://pocketcampus.epfl.ch/images/affiliates.png', 'Affiliates', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 120000000),
(23000090, 'http://pocketcampus.epfl.ch/images/venue.png', 'Venue', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, 'pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId=120090001', 120000000),
(23000091, 'http://pocketcampus.epfl.ch/images/favorites.png', 'Favorites', NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites', NULL, 120000000);

