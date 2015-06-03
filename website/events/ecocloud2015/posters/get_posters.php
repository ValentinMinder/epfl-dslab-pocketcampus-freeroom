<?php

header("content-type: text/plain");

foreach(glob("tmp/*_*.pdf") as $p) {
	echo "$p\n";
}

?>
