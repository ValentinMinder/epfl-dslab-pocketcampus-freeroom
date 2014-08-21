<?php

if(empty($argv[3])) {
	die("Make sure executor is not running and run: php launcher.php I MADE SURE\n");
}


if(""  !=  exec("ps aux | grep \"php executor.php say please\" | grep -v grep")  ) {
	die("FATAL: seems like executor is already running. kill it first\n");
}


chdir(dirname(__FILE__));

//system("truncate commands --size 0");
system("rm -f commands logs");
system("touch commands logs");
system("chown pocketcampus:apache commands logs");
system("chmod 620 commands");
system("chmod 640 logs");

system("tail -f commands 2>/dev/null | php executor.php say please >> logs 2>&1 &");

echo "started executor\n";

?>
