SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


CREATE TABLE IF NOT EXISTS `eventcategs` (
  `categKey` int(11) NOT NULL,
  `categValue` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `categValue_fr` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`categKey`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `eventemails` (
  `templateId` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `participantsPool` bigint(20) NOT NULL,
  `sendOnlyTo` text COLLATE utf8_unicode_ci,
  `emailTitle` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `emailBody` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`templateId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `eventitems` (
  `eventId` bigint(20) NOT NULL,
  `startDate` timestamp NULL DEFAULT NULL,
  `endDate` timestamp NULL DEFAULT NULL,
  `fullDay` tinyint(1) DEFAULT NULL,
  `eventPicture` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventThumbnail` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventTitle` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventTitle_fr` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventPlace` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventSpeaker` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `eventDetails` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `eventDetails_fr` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `parentPool` bigint(20) DEFAULT NULL,
  `eventUri` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `vcalUid` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `translation` bigint(20) DEFAULT NULL,
  `translation_fr` bigint(20) DEFAULT NULL,
  `eventCateg` int(11) DEFAULT NULL,
  `broadcastInFeeds` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `locationHref` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `detailsLink` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `secondLine` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `timeSnippet` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `hideEventInfo` tinyint(1) DEFAULT NULL,
  `hideTitle` tinyint(1) DEFAULT NULL,
  `hideThumbnail` tinyint(1) DEFAULT NULL,
  `isProtected` tinyint(1) DEFAULT NULL,
  `tempDetails` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `deleted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `eventperms` (
  `userToken` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `eventItemId` bigint(20) NOT NULL,
  `permLevel` int(11) NOT NULL,
  `remark` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  PRIMARY KEY (`userToken`,`eventItemId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `eventpools` (
  `poolId` bigint(20) NOT NULL,
  `poolPicture` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `poolTitle` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `poolPlace` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `poolDetails` text CHARACTER SET utf8 COLLATE utf8_unicode_ci,
  `disableStar` tinyint(1) DEFAULT NULL,
  `disableFilterByCateg` tinyint(1) DEFAULT NULL,
  `disableFilterByTags` tinyint(1) DEFAULT NULL,
  `enableScan` tinyint(1) DEFAULT NULL,
  `refreshOnBack` tinyint(1) DEFAULT NULL,
  `sendStarred` tinyint(1) DEFAULT NULL,
  `noResultText` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `overrideLink` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `parentEvent` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`poolId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `eventtags` (
  `feedKey` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `feedValue` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `feedValue_fr` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `isMemento` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`feedKey`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `eventusers` (
  `userId` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `mappedEvent` bigint(20) NOT NULL,
  `exchangeToken` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `addressingName` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `emailAddress` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `EXCHANGE_TOKEN` (`exchangeToken`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `eventpageviews` (
  `userTicket` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `nodeId` bigint(20) NOT NULL,
  `pageType` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `viewCount` int(11) NOT NULL,
  PRIMARY KEY (`userTicket`,`nodeId`,`pageType`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;