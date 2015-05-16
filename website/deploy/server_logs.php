<?php

include_once "vars.php";

echo "<pre>";

echo "ls\n";

foreach(glob("bin/pocketcampus-server-$team-*") as $file) {
	echo "<a href=\"$file\">" . basename($file) . "</a>\n";
}

?>
