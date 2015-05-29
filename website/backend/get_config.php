<?php

$disable_isa_grades = 1;

if(!empty($_SERVER["HTTP_X_PC_AUTH_PCSESSID"]) && ($sess_id = json_decode($_SERVER["HTTP_X_PC_AUTH_PCSESSID"], true)) && !empty($sess_id["Tequila.profile"])) {
	$profile = file_get_contents("https://tequila.epfl.ch/cgi-bin/OAuth2IdP/userinfo?access_token=" . urlencode($sess_id["Tequila.profile"]));
	if($profile && ($decoded = json_decode($profile, true)) && !empty($decoded["Sciper"])) {

		$workbook = "18D07NjPnPgKva7FNPV0qZAIcWtth_oyYeMwGtsIcxIc";
		$sheet_id = "1674637367";
		$sheet_data = file_get_contents("https://docs.google.com/spreadsheets/d/$workbook/export?format=csv&id=$workbook&gid=$sheet_id");
		$sheet_data = str_getcsv($sheet_data, "\n");
		$headers = null;
		foreach($sheet_data as $row) {
			if($headers == null) {
				$headers = str_getcsv($row);
			} else {
				$r = array_combine($headers, str_getcsv($row));
				if($r["sciper"] == $decoded["Sciper"]) {
					$disable_isa_grades = 0;
					break;
				}
			}
		}

		system("echo \"{$decoded["Sciper"]}\" >> scipers");
	}
}

system("echo \"{$_SERVER["HTTP_USER_AGENT"]}\" >> user_agents");

$platform = "android";
$app_version = "1.0";

if(empty($_GET["platform"])) {
	header("X-Error: no platform specified, defaulting to " . $platform);
} else {
	$platform = $_GET["platform"];
}
if(empty($_GET["app_version"])) {
	header("X-Error: no app_version specified, defaulting to " . $app_version);
} else {
	$app_version = $_GET["app_version"];
}


function trim_array($arr) {
	/*while(count($arr) && !$arr[0]) {
		array_shift($arr);
	}*/
        while(count($arr) && !$arr[count($arr) - 1]) {
                array_pop($arr);
        }
	return $arr;
}

function comparble_version($version_number) {
	$version_number = explode(".", $version_number);
	$version_number = array_map("intval", $version_number);
	$version_number = trim_array($version_number);
	$version_number = array_map(function($e){return str_pad($e, 10, "0", STR_PAD_LEFT);}, $version_number);
	return implode(".", $version_number);
}


function echo_compatible($arr) {
	global $platform;
	$lower_platform = strtolower($platform);
	if($lower_platform == "ios" || $lower_platform == "win") {
		return json_encode($arr);
	}
	$out = "";
	foreach($arr as $k => $v) {
		if(is_array($v)) {
			$v = implode(",", $v);
		}
		$out .= "$k=$v\n";
	}
	return $out;
}

/*
if($_SERVER['REMOTE_ADDR'] == "128.179.145.151") {
	$resp = array();
	$resp["SERVER_PROTOCOL"] = "http";
	$resp["SERVER_PORT"] = "9090";
	$resp["SERVER_ADDRESS"] = "128.179.137.34";
	die(echo_compatible($resp));
}
*/

// BACKWARD COMPATIBILITY

if($platform == "ios" && comparble_version($app_version) < comparble_version("1.2")) {
	$resp = array();
        $resp["status"] = 200;
        $resp["server_protocol"] = "https";
        $resp["server_address"] = "pocketcampus.epfl.ch";
        $resp["server_port"] = "14611";
        $resp["server_uri"] = "v3r1";
	die(echo_compatible($resp));
}


// MAIN LOGIC

$resp = array();

$resp["SERVER_PROTOCOL"] = "https";
//$resp["SERVER_PROTOCOL"] = "http";
//$resp["GCM_SENDER_ID"] = "384589660983";
$resp["SERVER_PORT"] = "14611";
//$resp["SERVER_PORT"] = "4433";
//$resp["SERVER_PORT"] = "9090";
//$resp["GA_TRACKING_CODE"] = "UA-22135241-3"; //Old Google Analytics
//$resp["GA_TRACKING_CODE"] = "UA-22135241-5"; //New Google Universal Analytics
$resp["SERVER_ADDRESS"] = "pocketcampus.epfl.ch";
$resp["ENABLED_PLUGINS"] = array("Camipro", "Moodle", "Food", "Transport", "News", "Satellite", "Map", "Directory", "MyEdu", "Events", "Authentication", "PushNotif", "IsAcademia", "FreeRoom", "CloudPrint", "RecommendedApps", "Alumni");
//$resp["SERVER_URI"] = "v3r1";
$resp["DISABLE_ISA_GRADES"] = $disable_isa_grades;
$resp["FOOD_RATINGS_ENABLED"] = 1; //0 = disabled, 1 = enabled

// iOS6 does not support https
if($platform == "ios" && comparble_version($app_version) < comparble_version("2.0")) {
	$resp["SERVER_PROTOCOL"] = "http";
	$resp["SERVER_PORT"] = "14610";
}

echo echo_compatible($resp);

?>
