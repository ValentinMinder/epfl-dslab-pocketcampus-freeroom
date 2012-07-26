<?php

require_once "common.php";

header('Content-Type: text/cache-manifest');

echo "CACHE MANIFEST\n";
echo "\n";
echo "CACHE:\n";
echo getUrl("moodle-a.php") . "\n";
echo getUrl("moodle.php") . "\n";

echo "\n";
echo "FALLBACK:\n";
echo "{$HTML5_APP_URL_PROTO}{$HTML5_APP_URL_HOST}" . dirname($_SERVER["SCRIPT_NAME"]) . "/sorry.html\n";



?>