CREATE TABLE IF NOT EXISTS `pc_pushnotif` (
  `plugin` varchar(16) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `userid` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `platform` varchar(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `pushtoken` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`plugin`,`userid`,`platform`,`pushtoken`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;