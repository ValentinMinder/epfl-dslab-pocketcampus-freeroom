<?php

// Config params

$HTML5_APP_URL_PROTO = (empty($_SERVER["HTTPS"]) ? "http://" : "https://");
$HTML5_APP_URL_HOST = $_SERVER["HTTP_HOST"];

$PC_PROXY_CONFIG = array (
	"pc-server.php" => array("protto" => "http://", "url" => "dslabpc36.epfl.ch:9090"),
	"tequila.php" => array("protto" => "https://", "url" => "tequila.epfl.ch"),
	"moodle.php" => array("protto" => "http://", "url" => "moodle.epfl.ch"),
);

// Utility functinos

function getUrl($script_name) {
	global $HTML5_APP_URL_PROTO;
	global $HTML5_APP_URL_HOST;
	return "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/$script_name" . (empty($_SERVER["PATH_INFO"]) ? "" : $_SERVER["PATH_INFO"]) . (empty($_SERVER["QUERY_STRING"]) ? "" : ("?" . $_SERVER["QUERY_STRING"]));
}

?>