<?php

if(empty($_GET["id"]))
	exit;


$s = "pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId={$_GET["id"]}";
if(!empty($_GET["mf"]))
	$s .= "%26markFavorite={$_GET["mf"]}";
if(!empty($_GET["ut"]))
	$s .= "%26userTicket={$_GET["ut"]}";

$chs = "400x400";
if(!empty($_GET["s"]))
	$chs = $_GET["s"];

$f = file_get_contents("http://chart.apis.google.com/chart?cht=qr&chs=$chs&chl=$s");

header('Content-Length: '.strlen($f));
header('Content-Type: image/png');
print $f;
?>
