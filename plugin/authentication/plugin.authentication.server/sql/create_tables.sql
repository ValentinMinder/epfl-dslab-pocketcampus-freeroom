

CREATE TABLE `pocketcampus`.`authsessions` (
	`sessionid` VARCHAR(50) NOT NULL, 
	`expiry` BIGINT NOT NULL, 
	`timeout` BIGINT NOT NULL, 
	`clienthost` TEXT NULL, 
	`office` TEXT NULL, 
	`phone` TEXT NULL, 
	`status` TEXT NULL, 
	`firstname` TEXT NULL, 
	`where` TEXT NULL, 
	`requesthost` TEXT NULL, 
	`version` TEXT NULL, 
	`unit` TEXT NULL, 
	`sciper` TEXT NULL, 
	`title` TEXT NULL, 
	`gaspar` TEXT NULL, 
	`email` TEXT NULL, 
	`category` TEXT NULL, 
	`lastname` TEXT NULL, 
	`authorig` TEXT NULL, 
	`unixid` TEXT NULL, 
	`groupid` TEXT NULL, 
	`authstrength` TEXT NULL, 
	PRIMARY KEY (`sessionid`)
) ENGINE = MyISAM  DEFAULT CHARSET=utf8;

 
