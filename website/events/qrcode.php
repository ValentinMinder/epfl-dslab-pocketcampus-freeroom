<?php

if(empty($_GET["id"]))
	exit;


$s = "pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId={$_GET["id"]}";
if(!empty($_GET["mf"]))
       	$s .= "&markFavorite={$_GET["mf"]}";
if(!empty($_GET["ut"]))
       	$s .= "&userTicket={$_GET["ut"]}";

header("Location: $s");

?>
