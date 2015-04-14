<?php

// Config params

$HTML5_APP_URL_PROTO = (empty($_SERVER["HTTPS"]) ? "http://" : "https://");
$HTML5_APP_URL_HOST = $_SERVER["HTTP_HOST"];

$PC_PROXY_CONFIG = array (
	"pc-server.php" => array("protto" => "http://", "url" => "dslabpc36.epfl.ch:9090"),
	"tequila.php" => array("protto" => "https://", "url" => "tequila.epfl.ch"),
	"moodle.php" => array("protto" => "http://", "url" => "moodle.epfl.ch"),
	"archibus.php" => array("protto" => "https://", "url" => "websrv.epfl.ch"),
	"facebook.php" => array("protto" => "http://", "url" => "graph.facebook.com"),
	"library-beast.php" => array("protto" => "http://", "url" => "beast-epfl.hosted.exlibrisgroup.com"),
	"library-rib.php" => array("protto" => "http://", "url" => "www.library.ethz.ch"),
);

?>
