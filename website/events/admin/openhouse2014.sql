
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(117000000, '2014-03-21 00:00:00', '2014-03-22 00:00:00', 1, NULL, 'http://pocketcampus.epfl.ch/images/binary_world.png', 'EDIC Open House 2014', 'EPFL', NULL, NULL, -1, NULL, NULL, -1, 'ic', NULL, NULL, 'School of Computer and Communication Sciences', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(19000010, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Schedule',                 NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000020, 'http://pocketcampus.epfl.ch/images/open_labs.png',       'Research Areas',           NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000030, 'http://pocketcampus.epfl.ch/images/labs.png',            'Labs',                     NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000040, 'http://pocketcampus.epfl.ch/images/open_labs.png',       'Open Lab Sessions',        NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000050, 'http://pocketcampus.epfl.ch/images/participants.png',    'Prospective PhD Students', NULL, NULL, NULL, 1, NULL, 1, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000060, 'http://pocketcampus.epfl.ch/images/participants.png',    'EPFL Participants',        NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000070, 'http://pocketcampus.epfl.ch/images/posters.png',         'Posters',                  NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000080, 'http://pocketcampus.epfl.ch/images/venue.png',           'Venue',                    NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000090, 'http://pocketcampus.epfl.ch/images/info.png',            'Useful Information',       NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 117000000),
(19000100, 'http://pocketcampus.epfl.ch/images/favorites.png',       'Favorites',                NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites', NULL, 117000000);


