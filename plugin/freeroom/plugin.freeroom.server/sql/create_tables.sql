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
	`type` char(255) DEFAULT NULL,
	PRIMARY KEY (`uid`)
) CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `fr-roomsoccupancy` (
	`oid` int(11) NOT NULL AUTO_INCREMENT,
	`timestampStart` bigint(20) NOT NULL,
	`timestampEnd` bigint(20) NOT NULL,
	`uid` char(255) NOT NULL,
	PRIMARY KEY (`oid`, `uid`),
	CONSTRAINT FOREIGN KEY (`uid`) REFERENCES `fr-roomslist`(`uid`) ON DELETE CASCADE
) CHARSET=latin1;
