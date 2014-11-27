-- Hold the list of rooms and all the details needed about a room.
-- groupAccess is used to differentiate the visibility of the rooms for some peoples (e.g student, staff..)
-- The lowest groupAccess possible is 1, usually for students, staff can have groupAccess of 20
-- It is possible to defines as many groups as there are integer between 1 and 20, 
-- the bigger the integer, the more rooms the group has access to.
-- enabled is used to temporarily disable a room, (e.g during a special event, it there are no occupancy for the room
-- it will be displayed as entirely free, enabled can be used to prevent such things)
CREATE TABLE IF NOT EXISTS `fr-roomslist` (
	`uid` char(32) NOT NULL,
	`doorCode` char(255) NOT NULL,
	`doorCodeWithoutSpace` char(255) NOT NULL,
	`alias` char(255),
	`capacity` int(11),
	`site_label` char(255),
	`surface` double,
	`building_name` char(255),
	`zone` char(255),
	`unitlabel` char(255),
	`site_id` int(11),
	`floor` int(11),
	`unitname` char(255),
	`site_name` char(255),
	`unitid` int(11),
	`building_label` char(255),
	`cf` char(255),
	`adminuse` char(255),
	`EWAid` char(255) DEFAULT NULL,
	`typeFR` char(255) DEFAULT NULL,
	`typeEN` char(255) DEFAULT NULL,
	`dincat` char(255) DEFAULT NULL,
	`enabled` boolean DEFAULT TRUE,
	`groupAccess` int(11) DEFAULT 1,
	PRIMARY KEY (`uid`)
) CHARSET=utf8;

-- This table holds rooms occupancies
CREATE TABLE IF NOT EXISTS `fr-occupancy` (
	`uid` char(32) NOT NULL,
	`timestampStart` bigint(20) NOT NULL,
	`timestampEnd` bigint(20) NOT NULL,
	`type` char(64) NOT NULL,
	`count` int(11) DEFAULT 0,
	PRIMARY KEY (`uid`, `timestampStart`, `type`),
	CONSTRAINT FOREIGN KEY (`uid`) REFERENCES `fr-roomslist`(`uid`) ON DELETE CASCADE	
) CHARSET=utf8;

-- This table is used to avoid multiple submission by a single client of user occupancies 
-- and to store messages from users
CREATE TABLE IF NOT EXISTS `fr-checkOccupancy` (
	`uid` char(32) NOT NULL,
	`timestampStart` bigint(20) NOT NULL,
	`timestampEnd` bigint(20) NOT NULL,
	`hash` char(64) NOT NULL,
	`message` varchar(255) DEFAULT NULL,
	PRIMARY KEY (`timestampStart`, `hash`),
	CONSTRAINT FOREIGN KEY (`uid`) REFERENCES `fr-roomslist` (`uid`) ON DELETE CASCADE
) CHARSET=utf8;

-- This table holds users occupancies
CREATE TABLE IF NOT EXISTS `fr-occupancy-users` (
	`uid` char(255) NOT NULL,
	`timestampStart` bigint(20) NOT NULL,
	`timestampEnd` bigint(20) NOT NULL,
	`type` char(255) NOT NULL,
	`count` int(11) DEFAULT 0,
	PRIMARY KEY (`uid`, `timestampStart`, `type`),
	CONSTRAINT FOREIGN KEY (`uid`) REFERENCES `fr-roomslist`(`uid`) ON DELETE CASCADE	
) CHARSET=utf8;

