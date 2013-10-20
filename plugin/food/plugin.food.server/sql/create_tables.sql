
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
-- Table structure for table 'Meals'
--

CREATE TABLE IF NOT EXISTS Meals (
  MealId bigint NOT NULL,
  RestaurantId bigint NOT NULL,
  PRIMARY KEY (MealId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table 'MealRatings'
--

CREATE TABLE IF NOT EXISTS MealRatings (
  MealId bigint NOT NULL,
  RatingTotal float NOT NULL,
  RatingCount int UNSIGNED NOT NULL,
  PRIMARY KEY (MealId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;