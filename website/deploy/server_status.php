<?php

include_once "vars.php";

echo "<pre>";

echo "ps aux\n";

//system("ps aux | grep pocketcampus-server-$team | grep -v grep ");
//system("./status $team");

$u = uniqid('');
file_put_contents("private/commands", "ps aux | grep pocketcampus-server-$team- | grep -v grep ; echo $u \n", FILE_APPEND | LOCK_EX);
$watchdog = 0;
do {
	sleep(1);
	$watchdog++;
	if($watchdog > 20) die("FATAL: timed out. is executor running?\n");
} while("" == exec("grep ^$u private/logs"));
system("grep \"echo $u\" -A 100 private/logs | grep -B 100 ^$u | grep -v $u");

?>
