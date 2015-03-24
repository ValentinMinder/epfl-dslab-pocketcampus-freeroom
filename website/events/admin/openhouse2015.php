<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database

$EVENT_TITLE = "Open House &bull; 2015";
$EVENT_ID = "127000000";

$SECTIONS = array();

$SECTIONS[] = array("POOL" => "27000010", "TYPE" => "schedule", "NAME" => "schedule");
$SECTIONS[] = array("POOL" => "27000020", "TYPE" => "research", "NAME" => "research areas");
$SECTIONS[] = array("POOL" => "27000030", "TYPE" => "labs", "NAME" => "labs");
$SECTIONS[] = array("POOL" => "27000040", "TYPE" => "visits", "NAME" => "open labs");
$SECTIONS[] = array("POOL" => "27000050", "TYPE" => "participants", "NAME" => "prospective phd students");
$SECTIONS[] = array("POOL" => "27000060", "TYPE" => "participants", "NAME" => "epfl participants");
$SECTIONS[] = array("POOL" => "27000070", "TYPE" => "posters", "NAME" => "posters",                    "EXPORT" => true);
//$SECTIONS[] = array("POOL" => "27000080", "TYPE" => "venue", "NAME" => "venue");
//$SECTIONS[] = array("POOL" => "27000090", "TYPE" => "info", "NAME" => "useful info");

$ID_MAP = array();

$ID_MAP["GOOGLE_SPREADSHEET"] = "1VwdhEIRr5YPYmVDFMtYDc5UyyPbH409Wd_ZiTw9Y3LI";
$ID_MAP["27000010"] = "890169652";
$ID_MAP["27000020"] = "927549881";
$ID_MAP["27000030"] = "583490921";
$ID_MAP["27000040"] = "329555645";
$ID_MAP["27000050"] = "1984864236";
$ID_MAP["27000060"] = "1842160259";
$ID_MAP["27000070"] = "825413559";
//$ID_MAP["19000080"] = "210404313";
//$ID_MAP["19000090"] = "837099128";

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
				echo "<li>";
				echo "<a href=\"manage.php?parentPool={$s["POOL"]}&type={$s["TYPE"]}\">manage {$s["NAME"]}</a>";
				if($s["EXPORT"]) echo " [ <a href=\"export.php?parentPool={$s["POOL"]}&type={$s["TYPE"]}\">export</a> ]";
				echo "</li>\n";
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
			<h4>Export stats as CSV</h4>
			<ul>
				<li><a href="export_csv_stats.php?type=pageviews_l3&rootEventItem=<?php echo $EVENT_ID; ?>">pageviews</a></li>
				<li><a href="export_csv_stats.php?type=users&rootEventItem=<?php echo $EVENT_ID; ?>">users</a></li>
			</ul>
			<hr>
		</div>
	</div>
	

</div>


</body>


</html>
