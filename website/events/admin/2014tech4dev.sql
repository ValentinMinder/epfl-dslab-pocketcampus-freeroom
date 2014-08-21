
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(118000000, '2014-06-04 00:00:00', '2014-06-06 00:00:00', 1, NULL, 'http://memento.epfl.ch/image/1852/256x256.jpg', '2014 UNESCO International Conference on Technologies for Development', 'STCC', NULL, NULL, -1, NULL, NULL, -1, 'epfl,enac,sti,ic,sv,cdm,cdh,inter,agenda-lcav', NULL, NULL, 'Cooperation and Development Center', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(21000010, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Schedule',                 NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 118000000),
(21000020, 'http://pocketcampus.epfl.ch/images/open_labs.png',       'Research Areas',           NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 118000000),
(21000030, 'http://pocketcampus.epfl.ch/images/labs.png',            'Labs',                     NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 'Coming soon', NULL, 118000000),
(21000040, 'http://pocketcampus.epfl.ch/images/participants.png',    'Participants',             NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon', NULL, 118000000),
(21000050, 'http://pocketcampus.epfl.ch/images/posters.png',         'Posters',                  NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon', NULL, 118000000),
(21000060, 'http://pocketcampus.epfl.ch/images/venue.png',           'Venue',                    NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 118000000),
(21000070, 'http://pocketcampus.epfl.ch/images/info.png',            'Useful Information',       NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon', NULL, 118000000),
(21000080, 'http://pocketcampus.epfl.ch/images/favorites.png',       'Favorites',                NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites', NULL, 118000000);


