<?php


$browser = get_browser(null, true);

//echo "<pre>";
//print_r($browser);




if($browser["platform"] === "Android") {
	header("Location: https://play.google.com/store/apps/details?id=org.pocketcampus");
} elseif($browser["platform"] === "iOS") {
	header("Location: https://itunes.apple.com/en/app/epfl/id549799535");
} elseif($browser["platform"] === "WinPhone8.1" || $browser["platform"] === "WinPhone8") {
	header("Location: http://windowsphone.com/s?appId=28f8300e-8a84-4e3e-8d68-9a07c5b2a83a");
} else {
	header("Location: https://pocketcampus.epfl.ch/");
}



exit;

