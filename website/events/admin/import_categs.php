<?php

require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database







if($_SERVER["REQUEST_METHOD"] == "POST"){
	
	echo "<pre>";
	
	if(!empty($_POST["categs"])) {
		$categs = explode("\n", trim($_POST["categs"]));
		$details = array();
		foreach($categs as $e) {
			$ee = explode("\t", trim($e));
			if(intval($ee[0])) {
				//echo "{$ee[0]}\n";
				//continue;
				$succ = sql_query("REPLACE INTO eventcategs (categKey, categValue) VALUES (" . intval($ee[0]) . ", '" . sql_real_escape_string(trim($ee[1])) . "');", $conn);
				echo "insert " . intval($ee[0]) . ":   succ=$succ affected_rows=" . mysql_affected_rows($conn) . "\n";
			}
			//if(count($ee) != 2 || $ee[1] != "epfl.ch") continue;
			//$details[] = $ee[0];
		}
		//$details = array_map("fetch_details_by_email", $details);
		//$details = implode("\n", $details);
	}
	exit;
}



?><html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Import categs</title>

</head>






<body style="text-align:center">


<div id=main_div>

	<!--
	<div style='margin-top:50px;'>
		<b>EcoCloud Annual Event 2013</b>
	</div>
	-->

	
	<div style='height:600px;width:600px;display:table;margin:auto;'>
		<div style='display:table-cell;vertical-align:middle;'>
			<h1>Import categs</h1>
			<br>
			<hr>
			<h4>categs</h4>
			<form enctype="multipart/form-data" action="#" method="POST">
				<textarea id="categs" name="categs" rows="20" cols="150"><?php if(!empty($_POST["categs"])) echo htmlentities($_POST["categs"]); ?></textarea><br>
				<input type="submit" value="import categs" /><br>
			</form>
		</div>
	</div>
	

</div>


</body>


</html>
