<?php

session_start();

if(!empty($_GET["t"])) {
	$_SESSION["t"] = $_GET["t"];
	header("Location: ?");
	exit;
}

if(empty($_SESSION["t"])) {
	header('HTTP/1.0 403 Forbidden');
	exit;
}






$config = parse_ini_file("/var/www/vhosts/pocketcampus/private/pocketcampus-deploy.config");
$config or die("CANNOT FIND OR OPEN CONFIG FILE");



if(isset($config[$_SESSION["t"]])) {
	$team = $config[$_SESSION["t"]];
} else {
	exit;
}



?>
