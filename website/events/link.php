<?php

if(empty($_GET["id"]))
	exit;

$browser = get_browser(null, true);

if(!$browser || $browser["ismobiledevice"]) {

	$s = "pocketcampus://events.plugin.pocketcampus.org/showEventItem?eventItemId={$_GET["id"]}";

	header("Location: $s");
	exit;

}



$id = $_GET["id"];



include_once "admin/functions.php";

$conn=connect_to_db("pocketcampus");
mysql_set_charset("utf8",$conn);
sql_query("set time_zone = 'Europe/Zurich';", $conn);

$events = sql_query("SELECT * FROM eventitems WHERE eventId='" . sql_real_escape_string($id) . "' ;", $conn);

$event = sql_fetch_array($events);

if(!$event)
	exit;

if($event["isProtected"])
	exit;

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>


<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><?php echo htmlentities($event["eventTitle"]);  ?> in PocketCampus</title>


</head>









<body>



<table style="background-color: #F1F1F1; border-spacing: 5px 5px; border: 1px solid #E0E0E0; width: 800px; margin:auto;">


<tr><td style="border: 0px; height: 20px; padding: 30px; background-color: #F1F1F1; font:1em Georgia,serif;" colspan="2">To access this event, please <a href="http://onelink.to/pu6ngz">install</a> PocketCampus and visit this same page on your mobile device.</td></tr>

<tr>
<td style="border: 0px; background-color: #F1F1F1; padding-top: 6px; vertical-align: top; width: 80px;">
<div><img style="width: 100%" src="<?php echo htmlentities($event["eventThumbnail"]);  ?>"></div>
</td>
<td><table style="background-color: #F1F1F1; border-spacing: 0px 5px; width: 100%;">

<tr><td style="background-color: #FFFFFF; border: 1px solid #E0E0E0; padding:10px;">
<div style="font:1.1em Georgia,serif; font-weight:bold;"><?php echo htmlentities($event["eventTitle"]);  ?></div>
<div style="font:1em Georgia,serif; text-align: left;"><?php echo htmlentities($event["secondLine"]);  ?></div>
</td></tr>
<tr><td style="background-color: #FFFFFF; border: 1px solid #E0E0E0; padding:10px;">
<div style="font:1em Georgia,serif; text-align: left;"><?php echo htmlentities($event["tempDetails"]);  ?></div>
</td></tr>

</table></td>
</tr>


</table>


</body>


</html>

