
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(13000000, '2013-05-31 00:00:00', '2013-05-31 00:00:00', 1, NULL, 'http://pocketcampus.epfl.ch/events/ecocloud2013/images/ecocloud.logo.png', 'EcoCloud Annual Event 2013', NULL, NULL, NULL, -1, NULL, NULL, -1, 'ic', NULL, NULL, 'Hôtel de la paix', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(15000001, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Schedule', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, NULL, NULL, 13000000),
(15000004, 'http://pocketcampus.epfl.ch/images/posters.png', 'Posters', NULL, NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, 13000000),
(15000005, 'http://pocketcampus.epfl.ch/images/participants.png', 'Participants', NULL, NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, 13000000),
(15000006, 'http://pocketcampus.epfl.ch/images/labs.png', 'Labs', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 13000000),
(15000007, 'http://pocketcampus.epfl.ch/images/affiliates.png', 'Affiliates', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 13000000),
(15000008, 'http://pocketcampus.epfl.ch/images/venue.png', 'Venue', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, 'pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId=13008001', 13000000),
(15000009, 'http://pocketcampus.epfl.ch/images/favorites.png', 'Favorites', NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites', NULL, 13000000);

