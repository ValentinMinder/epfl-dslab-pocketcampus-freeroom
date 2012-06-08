<?php


function curl_get($url, $cookie) {
	$ch = curl_init($url);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_COOKIE, $cookie);
	$data = curl_exec($ch);
	curl_close($ch);
	return $data;
}

function db_connect($c) {
	if($c && @mysql_ping($c))
		return $c;
	if($c)
		@mysql_close($c);
	$conn = mysql_connect("pocketcampus.epfl.ch","pocketcampus","pHEcNhrKAZMS5Hdp");
	$conn or die("failed to connect to db");
	mysql_set_charset('utf8', $conn);
	mysql_select_db("pocketcampus", $conn) or die("failed to find db\n");
	return $conn;
}

$conn = 0;
date_default_timezone_set("UTC");

while(1) {
	$count = 0;
	$conn = db_connect($conn);
	$t = mysql_query("select * from auth_tokens;", $conn);
	while($qr = mysql_fetch_array($t)) {
		if($qr['moodle_cookie']) {
			curl_get("http://moodle.epfl.ch/my/index.php", $qr['moodle_cookie']);
		}
		if($qr['camipro_cookie']) {
			curl_get("https://cmp2www.epfl.ch/client/sertrans", $qr['camipro_cookie']);
		}
		$count++;
	}
	echo date("D M j G:i:s T Y") . " Refreshed cookies for $count users\n";
	sleep(3600);
}

?>
