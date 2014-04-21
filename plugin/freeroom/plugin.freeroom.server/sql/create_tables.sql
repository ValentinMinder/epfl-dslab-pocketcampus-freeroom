-- Hold the list of rooms and all the details needed about a room.
CREATE TABLE IF NOT EXISTS `fr-roomslist` (
	`uid` char(255) NOT NULL,
	`doorCode` char(255) NOT NULL,
	`doorCodeWithoutSpace` char(255) NOT NULL,
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
	PRIMARY KEY (`uid`)
) CHARSET=latin1;

-- This table holds all types of occupancy, either a room or user occupancy. 
-- For practical reasons we assume timestampStart of a user occupancy to be a full hour (e.g 10h00), 
-- even if it overlaps some other room occupancy, this is why we put the type as a member of the primary key.
-- The overlaps of room/user occupancy are processed for a request, but not repercuted in the database.
CREATE TABLE IF NOT EXISTS `fr-occupancy` (
	`uid` char(255) NOT NULL,
	`timestampStart` bigint(20) NOT NULL,
	`timestampEnd` bigint(20) NOT NULL,
	`type` char(255) NOT NULL,
	`count` int(11) DEFAULT 0,
	PRIMARY KEY (`uid`, `timestampStart`, `type`),
	CONSTRAINT FOREIGN KEY (`uid`) REFERENCES `fr-roomslist`(`uid`) ON DELETE CASCADE	
) CHARSET=latin1;

-- This table is used to avoid multiple submission by a single client of user occupancies 
CREATE TABLE IF NOT EXISTS `fr-checkOccupancy` (
	`uid` char(255) NOT NULL,
	`timestampStart` bigint(20) NOT NULL,
	`hash` char(255) NOT NULL,
	PRIMARY KEY (`uid`, `timestampStart`, `hash`),
	CONSTRAINT FOREIGN KEY (`uid`) REFERENCES `fr-roomslist` (`uid`) ON DELETE CASCADE
) CHARSET=latin1;