CREATE TABLE IF NOT EXISTS `fr-roomslist` (
	`uid` char(255) NOT NULL,
	`doorCode` char(255) NOT NULL,
	`capacity` int(11),
	`type` char(255),
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

