-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 25, 2014 at 05:30 PM
-- Server version: 5.5.35
-- PHP Version: 5.3.10-1ubuntu3.9

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `pocketcampus`
--

-- --------------------------------------------------------

--
-- Table structure for table `roomslist`
--

CREATE TABLE IF NOT EXISTS `roomslist` (
  `rid` int(11) NOT NULL AUTO_INCREMENT,
  `building` varchar(255) NOT NULL,
  `room_number` int(11) NOT NULL,
  `type` varchar(255) NOT NULL,
  `capacity` int(11) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- --------------------------------------------------------

--
-- Table structure for table `roomsoccupancy`
--

CREATE TABLE IF NOT EXISTS `roomsoccupancy` (
  `rid` int(11) NOT NULL DEFAULT '-1',
  `timestampStart` bigint(20) NOT NULL,
  `timestampEnd` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

