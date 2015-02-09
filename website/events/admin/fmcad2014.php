<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database

$EVENT_TITLE = "FMCAD 2014";

$SECTIONS = array();

//$SECTIONS[] = array("POOL" => "24000011", "TYPE" => "schedule", "NAME" => "day1");
$SECTIONS[] = array("POOL" => "24000012", "TYPE" => "schedule", "NAME" => "day2");
$SECTIONS[] = array("POOL" => "24000013", "TYPE" => "schedule", "NAME" => "day3");
$SECTIONS[] = array("POOL" => "24000014", "TYPE" => "schedule", "NAME" => "day4");
$SECTIONS[] = array("POOL" => "24000015", "TYPE" => "schedule", "NAME" => "day5");
//$SECTIONS[] = array("POOL" => "24000070", "TYPE" => "posters", "NAME" => "posters");
$SECTIONS[] = array("POOL" => "24000030", "TYPE" => "participants", "NAME" => "Participants");
//$SECTIONS[] = array("POOL" => "24000080", "TYPE" => "labs", "NAME" => "labs");
$SECTIONS[] = array("POOL" => "24000050", "TYPE" => "affiliates", "NAME" => "Sponsors");
$SECTIONS[] = array("POOL" => "24000060", "TYPE" => "venue", "NAME" => "Venue");

$ID_MAP = array();

$ID_MAP["GOOGLE_SPREADSHEET"] = "1aLiBNOnpEO1BijWiSajpyqJwp-7Cn_VRoahpB08aO1c";
//$ID_MAP["24000011"] = "1702782389";
$ID_MAP["24000012"] = "2055272421";
$ID_MAP["24000013"] = "712820405";
$ID_MAP["24000014"] = "1121231943";
$ID_MAP["24000015"] = "1306349983";
$ID_MAP["24000030"] = "1842160259";
$ID_MAP["24000050"] = "1173699225";
$ID_MAP["24000060"] = "210404313";

?><html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Manage <?php echo $EVENT_TITLE; ?></title>

<script>
function send_post(url, params) {
	var http = new XMLHttpRequest();
	http.open("POST", url, true);
	http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	http.onreadystatechange = function() {if(http.readyState == 4 && http.status == 200) {alert(http.responseText);}};
	http.send(params);
}
function update_section(pool, type, workbook_elem, sheet_elem) {
	workbook = document.getElementById(workbook_elem).value;
	sheet = document.getElementById(sheet_elem).value;
	send_post('manage.php?parentPool=' + pool + '&type=' + type, 'deleteFirst=1&silent=1&workbookId=' + workbook + '&sheetId=' + sheet);
}
</script>

</head>






<body style="text-align:center">


<div id=main_div>


	
	<div style='height:600px;width:800px;display:table;margin:auto;'>
		<div style='display:table-cell;vertical-align:middle;'>
			<h1>Manage <?php echo $EVENT_TITLE; ?></h1>
			<br>
			<hr>
			<h4>Sections</h4>
			<ul>
			<?php
			foreach($SECTIONS as $s){ 
				echo "<li><a href=\"manage.php?parentPool={$s["POOL"]}&type={$s["TYPE"]}\">manage {$s["NAME"]}</a></li>\n";
			}
			?>
			</ul>
			<hr>
			<h4>Update from Google Spreadsheet</h4>
			<p>key=<input id="workbookId" style="width:400px;" value="<?php echo $ID_MAP["GOOGLE_SPREADSHEET"]; ?>"></p>
			<ul>
	<?php
	foreach($SECTIONS as $s){
		echo "<li>{$s["NAME"]}: gid=<input id=\"pool_{$s["POOL"]}\" value=\"{$ID_MAP[$s["POOL"]]}\"><button onclick=\"update_section('{$s["POOL"]}', '{$s["TYPE"]}', 'workbookId', 'pool_{$s["POOL"]}')\">update</button></li>\n";
	}
	?>
			</ul>	
			<hr>
		</div>
	</div>
	

</div>


</body>


</html>
