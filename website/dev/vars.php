<?php

session_start();

if(!empty($_GET["t"]))
	$_SESSION["t"] = $_GET["t"];

if(empty($_SESSION["t"]))
	exit;




$config = parse_ini_file("/var/www/vhosts/pocketcampus/private/pocketcampus-deploy.config");
$config or die("CANNOT FIND OR OPEN CONFIG FILE");



if(isset($config[$_SESSION["t"]]))
	$team = $config[$_SESSION["t"]];
else
	exit;




?>
