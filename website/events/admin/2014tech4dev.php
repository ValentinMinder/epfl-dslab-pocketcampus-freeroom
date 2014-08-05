<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database

$EVENT_TITLE = "UNESCO 2014";

$SECTIONS = array();

$SECTIONS[] = array("POOL" => "21000010", "TYPE" => "schedule", "NAME" => "schedule");
//$SECTIONS[] = array("POOL" => "21000020", "TYPE" => "research", "NAME" => "research areas");
//$SECTIONS[] = array("POOL" => "21000030", "TYPE" => "labs", "NAME" => "labs");
//$SECTIONS[] = array("POOL" => "21000040", "TYPE" => "visits", "NAME" => "open labs");
$SECTIONS[] = array("POOL" => "21000040", "TYPE" => "participants", "NAME" => "participants");
//$SECTIONS[] = array("POOL" => "21000060", "TYPE" => "participants", "NAME" => "epfl participants");
//$SECTIONS[] = array("POOL" => "21000070", "TYPE" => "posters", "NAME" => "posters");
//$SECTIONS[] = array("POOL" => "21000080", "TYPE" => "venue", "NAME" => "venue");
//$SECTIONS[] = array("POOL" => "21000090", "TYPE" => "info", "NAME" => "useful info");

$ID_MAP = array();

$ID_MAP["GOOGLE_SPREADSHEET"] = "1d2HfAnFgOjzuyC6rDPFaX7A7H89CwKuHM-FT_IsQj0s";
$ID_MAP["21000010"] = "890169652";
//$ID_MAP["21000020"] = "927549881";
//$ID_MAP["21000030"] = "583490921";
//$ID_MAP["21000040"] = "329555645";
//$ID_MAP["21000050"] = "1984864236";
$ID_MAP["21000040"] = "1842160259";
//$ID_MAP["21000070"] = "825413559";
//$ID_MAP["21000080"] = "210404313";
//$ID_MAP["21000090"] = "837099128";

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
