<?php

require_once "common.php";

header('Content-Type: text/cache-manifest');

echo "CACHE MANIFEST\n";
echo "# v10\n";
echo "\n";
echo "CACHE:\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/pocketcampus.html\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/jquery.mobile-1.1.1.min.css\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/jquery-1.7.1.min.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/jquery.mobile-1.1.1.min.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/thrift.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/authentication_types.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/AuthenticationService.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/moodle_types.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/MoodleService.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/pc.css\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/pc.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/moodle.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/authentication.js\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/images/ajax-loader.gif\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/images/icons-18-white.png\n";


echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/directory.png\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/food.png\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/news.png\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/map.png\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/transport.png\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/camipro.png\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/moodle.png\n";


echo "\n";
echo "NETWORK:\n";
echo "*\n";


?>




