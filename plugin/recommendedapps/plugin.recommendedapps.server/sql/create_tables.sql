
CREATE TABLE `RecommendedApps` (
  `AppId` int(11) NOT NULL,
  `AppStore` int(11) NOT NULL,
  `AppStoreQuery` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `AppOpenURLPattern` text COLLATE utf8_unicode_ci,
  `AppLogoURL` text COLLATE utf8_unicode_ci,
  `AppName_EN` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `AppName_FR` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `AppDescription_EN` text COLLATE utf8_unicode_ci,
  `AppDescription_FR` text COLLATE utf8_unicode_ci,
  `AppCategory` int(11) NOT NULL,
  PRIMARY KEY (`AppId`,`AppStore`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `RecommendedAppsCategories` (
  `CategoryId` int(11) NOT NULL,
  `CategoryLogoURL` text COLLATE utf8_unicode_ci,
  `CategoryName_EN` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `CategoryName_FR` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  `CategoryDescription_EN` text COLLATE utf8_unicode_ci NOT NULL,
  `CategoryDescription_FR` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`CategoryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
