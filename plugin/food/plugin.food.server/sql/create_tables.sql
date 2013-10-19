
--
-- Table structure for table `dailyratings`
--

CREATE TABLE IF NOT EXISTS `dailyratings` (
  `DeviceId` varchar(30) NOT NULL,
  `stamp_created` date NOT NULL DEFAULT '0000-00-00',
  `Rating` float DEFAULT NULL,
  `MealId` mediumtext NOT NULL,
  PRIMARY KEY (`DeviceId`,`stamp_created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `campusmenus`
--

CREATE TABLE IF NOT EXISTS `campusmenus` (
  `Title` varchar(30) NOT NULL,
  `Description` varchar(150) NOT NULL,
  `Restaurant` varchar(30) NOT NULL,
  `TotalRating` float NOT NULL,
  `NumberOfVotes` float NOT NULL,
  `MealId` bigint(20) NOT NULL,
  `stamp_created` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`Title`,`Restaurant`,`stamp_created`,`MealId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
