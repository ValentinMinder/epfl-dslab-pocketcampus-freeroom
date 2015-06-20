#!/usr/bin/php
<?php
if (PHP_SAPI !== 'cli') {
	die("Sorry, this can only be run from cli\n");
}
if (gethostname() !== "kissrv118.epfl.ch" && gethostname() !== "kissrv119.epfl.ch" && gethostname() !== "kissrv120") {
	die("This script can only run on kissrv118, kissrv119, or kissrv120\n");
}
chdir(dirname(__FILE__) . "/../");
system("git pull");
system("cp -r website/* /var/www/vhosts/pocketcampus/htdocs/");
?>
