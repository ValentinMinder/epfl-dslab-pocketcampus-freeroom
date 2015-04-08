CREATE TABLE IF NOT EXISTS `meals` (
  `Id` bigint(20) NOT NULL,
  `Name` text NOT NULL,
  `Description` text NOT NULL,
  `RestaurantId` bigint(20) NOT NULL,
  `TimeIndependentId` bigint(20) NOT NULL,
  `Time` enum("LUNCH", "DINNER") NOT NULL,
  `Date` date NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- This table is for logging purposes only, it is not read from in code
CREATE TABLE IF NOT EXISTS `restaurants` (
  `Id` bigint(20) NOT NULL,
  `Name` text NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `mealratings` (
  `DeviceId` varchar(50) NOT NULL,
  `MealId` bigint(20) NOT NULL,
  `Rating` float NOT NULL,
  `Timestamp` TIMESTAMP NULL DEFAULT NOW(),
  PRIMARY KEY (`DeviceId`, `MealId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX `idx_meals_TimeIndependentId`  ON `meals` (TimeIndependentId)

CREATE INDEX `idx_meals_Time_Date`  ON `meals` (Time, Date)
