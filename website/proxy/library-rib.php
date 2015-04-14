<?php

$config = parse_ini_file("/var/www/vhosts/pocketcampus/private/pocketcampus-access.config");
$config or die("CANNOT FIND OR OPEN CONFIG FILE");


if(empty($_REQUEST["{$config["LIBRARY_RIB_PROXY_ACCESS"]}"]))
	exit;

include "proxy.php";


?>
