<?php

include_once "vars.php";

echo "<pre>";

echo "ps aux\n";

system("ps aux | grep pocketcampus-server-$team | grep -v grep ");


?>
