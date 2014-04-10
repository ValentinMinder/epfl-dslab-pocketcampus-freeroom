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
  `TimeIndependentId` bigint(20) NOT NULL,
  `Time` enum("LUNCH", "DINNER") NOT NULL,
  `Date` date NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table 'mealratings'
--

CREATE TABLE IF NOT EXISTS `mealratings` (
  `DeviceId` varchar(50) NOT NULL,
  `MealTimeIndependentId` bigint(20) NOT NULL,
  `Rating` float NOT NULL,
  PRIMARY KEY (`DeviceId`, `MealTimeIndependentId`, `Rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;