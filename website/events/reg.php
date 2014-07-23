<?php

if(empty($_GET["t"]))
	exit;

if(empty($_GET["m"])) {

	header("Location: http://chart.apis.google.com/chart?cht=qr&chs=400x400&chl=pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId=-1%26userTicket={$_GET["t"]}");

}else {


	header("Location: pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId=-1&userTicket={$_GET["t"]}");
}


?>
