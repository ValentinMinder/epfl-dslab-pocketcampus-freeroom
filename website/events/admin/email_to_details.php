<?php

require_once("openinviter_base.php");
require_once("functions.php");
require_once("php_headers.php"); // will set $conn to database





function fetch_details_by_email($ee) {


	$oib = new OpenInviter_Base();
	$oib->timeout = 30;
	$oib->init();

	//html_entity_decode
	$res = $oib->get("http://people.epfl.ch/" . $ee, true, false, true, false, array("Accept-Language" => "en-US,en;q=0.5"));
	//$page = file_get_contents("http://people.epfl.ch/" . $_POST["parti_sciper"]);
	$email = $oib->getElementString($res, "msgto(", ")");
	$email = str_replace("'", "", $email);
	$email = str_replace(",", "@", $email);
	$sciper = $oib->getElementString($res, "vCard?id=", "&");
	$photo = $oib->getElementString($res, "getPhoto?id=", "&");
	if($photo) $photo = "http://people.epfl.ch/cgi-bin/people/getPhoto?id=$photo";
	$name = $oib->getElementString($res, "class=\"presentation\"", "</h4>");
	$name = str_replace("<h4>", "", $name);
	$name = str_replace(">", "", $name);
	$name = str_replace("&nbsp;", " ", $name);
	$name = trim(html_entity_decode($name));
	
	$labs = explode("class=\"unit-name\"", $res);
	array_shift($labs);
	$lab = "";
	foreach($labs as $ll) {
		if(strpos($oib->getElementString($ll, ">", "<"), "Group") !== FALSE) {
			$lab = $oib->getElementString($ll, "ubrowse.action?acro=", "\"");
			break;
		}
	}
	foreach($labs as $ll) {
		if(strpos($oib->getElementString($ll, ">", "<"), "Laboratory") !== FALSE) {
			$lab = $oib->getElementString($ll, "ubrowse.action?acro=", "\"");
			break;
		}
	}
	//$lab = $oib->getElementString($res, "class=\"unit-name\"", "</td>");
	//$lab = $oib->getElementString($lab, "ubrowse.action?acro=", "\"");
	
	/*echo "<pre>";
	echo "name: $name\n";
	echo "email: $email\n";
	if($photo) echo "photo: $photo\n";
	
	//echo htmlentities();
	//print_r($_POST);
	exit;*/
	
	return "$email\t$sciper\t$lab\t$photo";
	//insert_user($name, $email, $photo, $categ, $conn);


}




if($_SERVER["REQUEST_METHOD"] == "POST"){
	
	
	if(!empty($_POST["emails"])) {
		$emails = explode("\n", trim($_POST["emails"]));
		$details = array();
		foreach($emails as $e) {
			$ee = explode("@", trim($e));
			if(count($ee) != 2 || $ee[1] != "epfl.ch") continue;
			$details[] = $ee[0];
		}
		$details = array_map("fetch_details_by_email", $details);
		$details = implode("\n", $details);
	}
	
}



?><html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Email to details</title>

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
			<h1>Email to details</h1>
			<br>
			<hr>
			<h4>Emails</h4>
			<form enctype="multipart/form-data" action="#" method="POST">
				<textarea id="emails" name="emails" rows="20" cols="150"><?php if(!empty($_POST["emails"])) echo htmlentities($_POST["emails"]); ?></textarea><br>
				<input type="submit" value="fetch details" /><br>
			</form>
			<textarea id="details" name="details" rows="20" cols="150"><?php if(!empty($details)) echo htmlentities($details); ?></textarea><br>
		</div>
	</div>
	

</div>


</body>


</html>
