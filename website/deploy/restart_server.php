<?php

include_once "vars.php";

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

	$u = uniqid('');
	$just_stop = (empty($_GET["just_stop"]) ? "" : "just_stop");
	file_put_contents("private/commands", "php start_server.php $team $just_stop ; echo $u \n", FILE_APPEND | LOCK_EX);
	$watchdog = 0;
	do {
		sleep(1);
		$watchdog++;
		if($watchdog > 20) die("FATAL: timed out. is executor running?\n");
	} while("" == exec("grep ^$u private/logs"));
	echo "<pre>\n";
	system("grep \"echo $u\" -A 100 private/logs | grep -B 100 ^$u | grep -v $u");
	exit;

}




?>
<html>
<body>

<h2>Restart server</h2>


<form action="#" method="post">
<p>(this button submits a request to (re)start your server; the old instance, if any, will be killed)</p>
<input type="hidden" name="file"><br>
<input type="submit" value="(re)start">
</form>

<form action="?just_stop=1" method="post">
<p>(to just stop the currently running server, use the following button)</p>
<input type="hidden" name="file"><br>
<input type="submit" value="stop">
</form>


</body>
</html>
