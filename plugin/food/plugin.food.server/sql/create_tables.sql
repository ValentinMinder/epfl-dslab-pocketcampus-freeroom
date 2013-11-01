--
-- Table structure for table `dailyratings` (OLD)
--

CREATE TABLE IF NOT EXISTS `dailyratings` (
  `DeviceId` varchar(30) NOT NULL,
  `stamp_created` date NOT NULL DEFAULT '0000-00-00',
  `Rating` float DEFAULT NULL,
  `MealId` mediumtext NOT NULL,
  PRIMARY KEY (`DeviceId`,`stamp_created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `campusmenus` (OLD)
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


--
-- Table structure for table 'meals'
--

CREATE TABLE IF NOT EXISTS `meals` (
  `Id` bigint(20) NOT NULL,
  `Name` text NOT NULL,
  `RestaurantId` bigint(20) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table 'restaurants'
--

CREATE TABLE IF NOT EXISTS `restaurants` (
  `Id` bigint(20) NOT NULL,
  `Name` text NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table 'mealratings'
--

CREATE TABLE IF NOT EXISTS `mealratings` (
  `MealId` bigint(20) NOT NULL,
  `Rating` float NOT NULL,
  `Date` date NOT NULL,
  PRIMARY KEY (`MealId`, `Rating`, `Date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;