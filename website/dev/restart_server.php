<?php

include_once "vars.php";

function get_latest($team, $thing) {
	$files = glob("bin/pocketcampus-server-$team-*-*.$thing");
	sort($files);
	return basename(array_pop($files));
}



if ($_SERVER['REQUEST_METHOD'] === 'POST') {

echo "<pre>";

echo "Terminating old server\n";

system("ps aux | grep pocketcampus-server-$team | grep -v grep | tr '\t' ' ' | tr -s ' ' | cut -f 2 -d ' ' | xargs kill -TERM");

echo "Waiting for it to terminate gracefully\n";

sleep(5);

echo "Killing the old server just in case\n";

system("ps aux | grep pocketcampus-server-$team | grep -v grep | tr '\t' ' ' | tr -s ' ' | cut -f 2 -d ' ' | xargs kill -KILL");

echo "Starting the new server\n";

$jarname = get_latest($team, "jar");
$configname = get_latest($team, "config");
$jarname or die("jar not found");
$configname or die("config not found");
$d = date("YmdHis");
$u = uniqid('');
system("cd bin ; java -jar $jarname $configname  > pocketcampus-server-$team-$d-$u.log  2>&1 & echo Server successfully started with pid $!");

exit;

}

?>
<html>
<body>

<h2>Restart server</h2>


<form action="#" method="post">
<p>(takes ~5 seconds)</p>
<input type="hidden" name="file"><br>
<input type="submit" value="kill!">
</form>


</body>
</html>
