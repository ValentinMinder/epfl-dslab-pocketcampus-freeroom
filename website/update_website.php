#!/usr/bin/php
<?php
if (PHP_SAPI === 'cli') {
	chdir(dirname(__FILE__) . "/../");
	system("git pull");
	system("cp -r website/* /var/www/vhosts/pocketcampus/htdocs/");
} else {
	die("Sorry, this can only be run from cli");
}
?>
