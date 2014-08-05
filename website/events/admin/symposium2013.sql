

INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(116000000, '2013-11-20 00:00:00', '2013-11-23 00:00:00', 1, NULL, 'https://pocketcampus.epfl.ch/events/cragirgc2013/pics/LogoSymposiumSquareMargin.png', 'CRAG - IRGC Symposium 2013', 'EPFL, Lausanne, Switzerland', NULL, NULL, -1, NULL, NULL, -1, 'epfl,cdm,crag,sfi-epfl', NULL, NULL, 'EPFL, Lausanne, Switzerland', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(18000030, 'http://pocketcampus.epfl.ch/images/participants.png', 'Participants', NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon.', NULL, 116000000),
(18000050, 'http://pocketcampus.epfl.ch/images/favorites.png', 'Favorites', NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites', NULL, 116000000),
(18000011, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Plenary session 1', '20.11.2013 - BC 420', NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 116000000),
(18000012, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Workshop 1', '21.11.2013 - BI A0 448', NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 116000000),
(18000013, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Workshop 2', '21.11.2013 - BC 420', NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 116000000),
(18000014, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Workshop 3', '21.11.2013 - BC 03', NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 116000000),
(18000015, 'http://pocketcampus.epfl.ch/images/schedule.png', 'Plenary session 2', '22.11.2013 - BC 420', NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 116000000),
(18000040, 'http://pocketcampus.epfl.ch/images/venue.png', 'Venue', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, NULL, 116000000);



