
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(121000000, '2014-10-21 00:00:00', '2014-10-25 00:00:00', 1, NULL, 'http://www.cs.utexas.edu/users/hunt/FMCAD/FMCAD14/fmcad_css_files/fmcad_logo.png', 'FMCAD 2014', NULL, NULL, NULL, -1, NULL, NULL, -1, 'ic', NULL, NULL, 'Lausanne, Switzerland', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(24000010, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Schedule',       NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 121000000),
(24000030, 'http://pocketcampus.epfl.ch/images/participants.png',    'Participants',   NULL, NULL, NULL, 1, 1, 1, NULL, NULL, 'Coming soon.', NULL,  121000000),
(24000050, 'http://pocketcampus.epfl.ch/images/affiliates.png',      'Sponsors',       NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  121000000),
(24000060, 'http://pocketcampus.epfl.ch/images/venue.png',           'Venue',          NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  121000000),
(24000090, 'http://pocketcampus.epfl.ch/images/favorites.png',       'Favorites',      NULL, NULL, NULL, 1, 1, NULL, NULL, 1, 'No favorites.', NULL, 121000000);


INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES
(24000011, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Monday - DIFTS',       NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 121000000),
(24000012, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Tuesday - Joint Tutorials',       NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 121000000),
(24000013, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Wednesday - Day 1',       NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 121000000),
(24000014, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Thursday - Day 2',       NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 121000000),
(24000015, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Friday - Day 3',       NULL, NULL, NULL, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL, 121000000);




