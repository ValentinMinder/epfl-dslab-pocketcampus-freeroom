<?php
//echo "<pre>";
//echo ini_get("memory_limit") . "\n";
//ini_set('memory_limit', '512M');
//echo ini_get("memory_limit") . "\n";

//system("echo alo maman bobo > fifo");
//system("mkfifo fifo");
//posix_mkfifo("fifo2", 0777);

//file_put_contents("mkfifo.sh", "#!/bin/sh\n\nmkfifo fifo\n\n");


//system("echo \"#!/bin/sh\" > mkfifo.sh");
//system("echo mkfifo fifo >> mkfifo.sh");
//system("cp runscript runscript2");
//system("chmod +s runscript2");

//system("cat fifo");
//system("echo \"echo 1\" >> log");

file_put_contents("commands", "echo 1123\n", FILE_APPEND | LOCK_EX);

echo 3;
exit;
system("./runscript");

?>
