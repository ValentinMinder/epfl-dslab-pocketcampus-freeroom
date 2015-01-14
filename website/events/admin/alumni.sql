
INSERT INTO `eventitems` (`eventId`, `startDate`, `endDate`, `fullDay`, `eventPicture`, `eventThumbnail`, `eventTitle`, `eventPlace`, `eventSpeaker`, `eventDetails`, `parentPool`, `eventUri`, `vcalUid`, `eventCateg`, `broadcastInFeeds`, `locationHref`, `detailsLink`, `secondLine`, `timeSnippet`, `hideEventInfo`, `hideTitle`, `hideThumbnail`, `isProtected`) VALUES
(122000000, '2014-11-01 00:00:00', '2017-11-01 00:00:00', 1, NULL, 'http://pocketcampus.epfl.ch/images/epflalumni.png', 'EPFL Alumni', NULL, NULL, NULL, -1, NULL, NULL, -1, 'ic', NULL, NULL, 'Lausanne, Switzerland', NULL, 1, NULL, NULL, 1);

INSERT INTO `eventpools` (`poolId`, `poolPicture`, `poolTitle`, `poolPlace`, `poolDetails`, `disableStar`, `disableFilterByCateg`, `disableFilterByTags`, `enableScan`, `refreshOnBack`, `sendStarred`, `noResultText`, `overrideLink`, `parentEvent`) VALUES

(25000010, 'http://pocketcampus.epfl.ch/images/participants.png',    'Directory',      NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
(25000015, 'http://pocketcampus.epfl.ch/images/person.png',          'My profile',     NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
(25000020, 'http://pocketcampus.epfl.ch/images/dollar.png',          'Contribute',     NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),

(25000025, 'http://pocketcampus.epfl.ch/images/venue.png',           'Alumni near me', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
(25000030, 'http://pocketcampus.epfl.ch/images/icons/newspaper.png', 'EPFL news',      NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
(25000035, 'http://pocketcampus.epfl.ch/images/schedule.png',        'Events',         NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),

(25000040, 'http://pocketcampus.epfl.ch/images/labs.png',            'Chapters',       NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
(25000045, 'http://pocketcampus.epfl.ch/images/affiliates.png',      'EPFL alumni office',          NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000),
(25000050, 'http://pocketcampus.epfl.ch/images/info.png',            'About',          NULL, NULL, 1, 1, 1, NULL, NULL, NULL, 'Coming soon.', NULL,  122000000);





