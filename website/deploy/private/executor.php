<?php

if(empty($argv[2])) {
	die("You didn't say please\n");
}

chdir(dirname(__FILE__));

while($l = readline("")) {
	if($l == "exit")
		break;
	echo "EXECUTING COMMAND: $l\n"; 
	system($l);
}

?>
