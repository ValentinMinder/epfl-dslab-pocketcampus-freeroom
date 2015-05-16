<?php

if(empty($argv[1])) {
	die("Invocation error: team not specified\n");
}

chdir(dirname(__FILE__));


$team = $argv[1];


function get_latest($team, $thing) {
	$files = glob("../bin/pocketcampus-server-$team-*-*.$thing");
	sort($files);
	return basename(array_pop($files));
}


$jarname = get_latest($team, "jar");
$configname = get_latest($team, "config");
$jarname or die("jar not found\n");
$configname or die("config not found\n");
date_default_timezone_set("Europe/Zurich");
$d = date("YmdHis");
$u = uniqid('');

//die("we should restart the server for $team\n");

$sig = "TERM";
$try = 0;

while(""  !=  ($proc = exec("ps aux | grep pocketcampus-server-$team- | grep -v grep | tr '\t' ' ' | tr -s ' ' | cut -f 2 -d ' '"))  ) {

	echo "sending signal $sig to process $proc\n";
	system("kill -$sig $proc");

	$try++;
	sleep(1);

	if($try > 5) {
		$sig = "KILL";
	}
	if($try > 10) {
		die("FATAL: the process is not dying\n");
	}

}


echo "starting new server\n";


system("cd ../bin ; java -jar $jarname $configname  > pocketcampus-server-$team-$d-$u.log  2>&1 & echo started with pid $!");
//system("cd bin ; ./restart $jarname $configname pocketcampus-server-$team-$d-$u.log");


?>
