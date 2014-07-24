<?php

session_start();

if(!empty($_GET["t"]))
	$_SESSION["t"] = $_GET["t"];

if(empty($_SESSION["t"]))
	exit;




$config = parse_ini_file("/var/www/vhosts/pocketcampus/private/pocketcampus-access.config");
$config or die("CANNOT FIND OR OPEN CONFIG FILE");



if($_SESSION["t"] == "{$config["SEMPROJ_FREEROOM_ACCESS_TOKEN"]}")
	$team = "freeroom";
elseif($_SESSION["t"] == "{$config["SEMPROJ_MICROPHONE_ACCESS_TOKEN"]}")
	$team = "microphone";
elseif($_SESSION["t"] == "{$config["SEMPROJ_BLEBEACONS_ACCESS_TOKEN"]}")
	$team = "blebeacons";
elseif($_SESSION["t"] == "{$config["SEMPROJ_ROOMRATINGS_ACCESS_TOKEN"]}")
	$team = "roomrating";
else
	exit;


?>
