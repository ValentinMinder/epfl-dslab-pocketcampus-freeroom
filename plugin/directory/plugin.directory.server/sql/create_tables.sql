
--
-- Table structure for table `directory_firstname`
--

CREATE TABLE IF NOT EXISTS `directory_firstname` (
  `firstname` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`firstname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `directory_lastname`
--

CREATE TABLE IF NOT EXISTS `directory_lastname` (
  `lastname` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`lastname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
