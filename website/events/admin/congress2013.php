<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database

$EVENT_TITLE = "Congress on Privacy and Surveillance";

$SECTIONS = array();

$SECTIONS[] = array("POOL" => "17000010", "TYPE" => "schedule", "NAME" => "schedule");
$SECTIONS[] = array("POOL" => "17000030", "TYPE" => "participants", "NAME" => "participants");
$SECTIONS[] = array("POOL" => "17000070", "TYPE" => "labs", "NAME" => "labs");
$SECTIONS[] = array("POOL" => "17000090", "TYPE" => "research", "NAME" => "research areas");
//$SECTIONS[] = array("POOL" => "17000110", "TYPE" => "info", "NAME" => "useful info");
$SECTIONS[] = array("POOL" => "17000050", "TYPE" => "venue", "NAME" => "venue");
$SECTIONS[] = array("POOL" => "17000060", "TYPE" => "affiliates", "NAME" => "sponsors");

$ID_MAP = array();

$ID_MAP["GOOGLE_SPREADSHEET"] = "0Ape12d7hkDUpdHA2Vzg4Z1VBRTI4azhySXhPQVFFX3c";
$ID_MAP["17000010"] = "3";
$ID_MAP["17000030"] = "10";
$ID_MAP["17000070"] = "4";
$ID_MAP["17000090"] = "16";
$ID_MAP["17000110"] = "20";
$ID_MAP["17000050"] = "5";
$ID_MAP["17000060"] = "28";

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
