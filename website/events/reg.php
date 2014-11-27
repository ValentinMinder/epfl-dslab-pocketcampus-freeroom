<?php

if(empty($_GET["t"]))
	exit;



$browser = get_browser(null, true);

function isMobile($brz) {
	return ($brz["platform"] === "Android" || $brz["platform"] === "iOS" || $brz["platform"] === "WinPhone8.1" || $brz["platform"] === "WinPhone8");
}


$ticket = rawurlencode($_GET["t"]);

if(empty($_GET["m"])) {

	// This is the src of the QR code picture. We send this by email, and we want it to display properly on both Desktops and Mobiles.
	header("Location: http://chart.apis.google.com/chart?cht=qr&chs=400x400&chl=pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId=-1%26userTicket=$ticket");

}else {

	if(!isMobile($browser)) {
		echo "Please open this link from a mobile device running PocketCampus.";
	} else {
		header("Location: pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId=-1&userTicket=$ticket");
	}

}


?>
