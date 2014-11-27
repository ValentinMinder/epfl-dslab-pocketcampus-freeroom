
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(115000000, '2013-09-30 00:00:00', '2013-10-01 00:00:00', 1, NULL, 'http://pocketcampus.epfl.ch/images/congress_privacy_surveillance1.jpg', 'Congress on Privacy & Surveillance', 'EPFL Rolex Learning Center Forum', NULL, NULL, -1, NULL, NULL, -1, 'ic', NULL, NULL, 'EPFL Rolex Learning Center Forum', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(17000030, 'http://pocketcampus.epfl.ch/images/participants.png', 'Participants', NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon.', NULL, 115000000),
(17000010, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Schedule', NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 115000000),
(17000050, 'http://pocketcampus.epfl.ch/images/venue.png', 'Venue', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, 'pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId=115050001', 115000000),
(17000070, 'http://pocketcampus.epfl.ch/images/labs.png', 'Labs', NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'Coming soon.', NULL, 115000000),
(17000090, 'http://pocketcampus.epfl.ch/images/open_labs.png', 'Research Areas', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 115000000),
(17000130, 'http://pocketcampus.epfl.ch/images/favorites.png', 'Favorites', NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites', NULL, 115000000),
(17000060, 'http://pocketcampus.epfl.ch/images/affiliates.png', 'Sponsors', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 115000000);

