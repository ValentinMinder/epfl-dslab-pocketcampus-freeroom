<?php

if(empty($_GET["id"]))
	exit;


$s = "pocketcampus://events.plugin.pocketcampus.org/showEventPool?eventPoolId={$_GET["id"]}";
if(!empty($_GET["mf"]))
       	$s .= "&markFavorite={$_GET["mf"]}";
if(!empty($_GET["ut"]))
       	$s .= "&userTicket={$_GET["ut"]}";

//header("Location: $s");

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>


<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="refresh" content="0; url=<?php echo $s; ?>">
<title>PocketCampus</title>


</head>









<body>



<table style="background-color: #F1F1F1; border-spacing: 5px 5px; border: 1px solid #E0E0E0; width: 800px; margin:auto;">


<tr><td style="border: 0px; height: 20px; padding: 30px; background-color: #F1F1F1; font:1em Georgia,serif;" colspan="2">
Please <a href="http://onelink.to/pu6ngz">install PocketCampus</a> first, then come back to this link.
</td></tr>



</table>


</body>


</html>