<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database




if(empty($_GET["parentPool"])) {
	die("No parentPool specified");
}
if(empty($_GET["type"])) {
	die("No type specified");
}

$PARENT_POOL = sql_real_escape_string($_GET["parentPool"]);
$TYPE = ($_GET["type"]);








function export_event_items(){
	$t=func_get_args();
	global $TYPE;
	return call_user_func_array(__FUNCTION__."_$TYPE", $t);
}

if(!function_exists("export_event_items_$TYPE")){
	die("I don't understand this type");
}




$POOL_TITLE = "Pool";
$poool = sql_fetch_array(sql_query("SELECT poolTitle FROM eventpools WHERE poolId='$PARENT_POOL'", $conn));
if($poool) $POOL_TITLE = $poool[0];


?><html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Export <?php echo $POOL_TITLE; ?></title>

</head>






<body style="text-align:center">


<div id=main_div>


<?php

	echo export_event_items($PARENT_POOL, $conn);

?>


</div>


</body>


</html>
