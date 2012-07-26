<?php

function getUrl($script_name) {
	return "http://128.178.77.233" . dirname($_SERVER["SCRIPT_NAME"]) . "/$script_name" . (empty($_SERVER["PATH_INFO"]) ? "" : $_SERVER["PATH_INFO"]) . (empty($_SERVER["QUERY_STRING"]) ? "" : ("?" . $_SERVER["QUERY_STRING"]));
}

header('Content-Type: text/cache-manifest');

echo "CACHE MANIFEST\n";
echo "\n";
echo "CACHE:\n";
echo getUrl("moodle-a.php") . "\n";
echo getUrl("moodle.php") . "\n";

echo "\n";
echo "FALLBACK:\n";
echo "http://128.178.77.233" . dirname($_SERVER["SCRIPT_NAME"]) . "/sorry.html\n";



?>