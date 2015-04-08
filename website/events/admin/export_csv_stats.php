<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database




if(empty($_GET["rootEventItem"])) {
	die("No rootEventItem specified");
}
if(empty($_GET["type"])) {
	die("No type specified");
}

$ROOT_EVENT = ($_GET["rootEventItem"]);
$TYPE = ($_GET["type"]);








function export_csv_stats(){
	$t=func_get_args();
	global $TYPE;
	return call_user_func_array(__FUNCTION__."_$TYPE", $t);
}

if(!function_exists("export_csv_stats_$TYPE")){
	die("I don't understand this type");
}



//$TITLE = "Event";
//$eee = sql_fetch_array(sql_query("SELECT eventTitle FROM eventitems WHERE eventId='$ROOT_EVENT'", $conn));
//if($eee) $TITLE = $eee[0];




header("Content-Type: text/csv");
header("Content-Disposition: attachment; filename=stats.csv");
// Disable caching
header("Cache-Control: no-cache, no-store, must-revalidate"); // HTTP 1.1
header("Pragma: no-cache"); // HTTP 1.0
header("Expires: 0"); // Proxies

function outputCSV($data) {
    $output = fopen("php://output", "w");
    foreach ($data as $row) {
        fputcsv($output, $row); // here you can change delimiter/enclosure
    }
    fclose($output);
}

outputCSV(export_csv_stats($ROOT_EVENT, $conn));



?>
