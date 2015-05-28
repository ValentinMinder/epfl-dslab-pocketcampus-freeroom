<?php

session_start();

header('Content-Type: text/html; charset=utf-8');

require_once("tequila.php");

$oClient = new TequilaClient();

//$ret = array();
//$ret["status"] = 500;


//echo "<pre>";
//print_r($_SESSION);

//exit;

if(!empty($_GET["logout"])) {
	unset($_SESSION["user"]);
	unset($_SESSION["tequila_key"]);
	echo "<p>Logged out</p>\n";
	echo "<p><a href=\"?\">Login again</a></p>";
	exit;
}

if(!empty($_SESSION["tequila_key"])) {
	$key = $_SESSION["tequila_key"];
	unset($_SESSION["tequila_key"]);
	$attributes = $oClient->fetchAttributes($key);
	if($attributes) {
		$_SESSION["user"] = $attributes;
//	} else {
//		die("fucker");
	}
}


if(empty($_SESSION["user"])) {
//	echo "<pre>\n";
//	print_r($_SERVER);
//}

//exit;

//if(!empty($_GET["key"])) {

//	$attributes = $oClient->fetchAttributes($_GET["key"]);
//	if($attributes) {
//		$ret["status"] = 200;
//		$ret["attributes"] = $attributes;
//	}

//} elseif(!empty($_GET["app_url"]) && !empty($_GET["app_name"])) {

	$oClient->SetApplicationName("Pocket Campus1");
	$oClient->SetApplicationURL("https://{$_SERVER["HTTP_HOST"]}{$_SERVER["REQUEST_URI"]}");
	$oClient->SetWantedAttributes(array('uniqueid','name','firstname','unit', 'unitid', 'where', 'group', 'email', 'title'));
	$oClient->SetAllowsFilter("categorie=Shibboleth|categorie=epfl-guests");
	$oClient->createRequest();

	$_SESSION["tequila_key"] = $oClient->GetKey();
	header("Location: " . $oClient->getAuthenticationUrl());
	exit;


//	$oClient->createRequest();
//	$key = $oClient->GetKey();
//	if($key) {
//		$ret["status"] = 200;
//		$ret["key"] = $key;
//		$ret["redirect"] = $oClient->getAuthenticationUrl();
//	}

}

//echo "<pre>\n";
//print_r($_SESSION);

if(!empty($_POST["optin"])) {
	// do stuff




	//set POST variables
	$url = 'https://docs.google.com/forms/d/17NIShczi2AVb7cJVHnCj0YjYyNqkicD0StMQYoNRWu0/formResponse';
	$fields = array(
		'entry.2064026302=' . urlencode("{$_SESSION["user"]["uniqueid"]}"),
		'entry.1940291346=' . urlencode(json_encode($_SESSION["user"])),
	);

	//url-ify the data for the POST
	$fields_string = implode('&',$fields);

	//open connection
	$ch = curl_init();

	//set the url, number of POST vars, POST data
	curl_setopt($ch,CURLOPT_URL, $url);
	curl_setopt($ch,CURLOPT_POST, count($fields));
	curl_setopt($ch,CURLOPT_POSTFIELDS, $fields_string);
	curl_setopt($ch,CURLOPT_RETURNTRANSFER, true);

	//execute post
	$result = curl_exec($ch);

	//close connection
	curl_close($ch);

	if(strpos($result, "class=\"ss-confirmation\"") === false) {
		die("Something went wrong");
	}


	header("Location: ?");
	exit;
}







$workbook = "18D07NjPnPgKva7FNPV0qZAIcWtth_oyYeMwGtsIcxIc";
$sheet_id = "1674637367";
$sheet_data = file_get_contents("https://docs.google.com/spreadsheets/d/$workbook/export?format=csv&id=$workbook&gid=$sheet_id");
$sheet_data = str_getcsv($sheet_data, "\n");
$headers = null;
$has_signed_up = false;
foreach($sheet_data as $row) {
        if($headers == null) {
                $headers = str_getcsv($row);
        } else {
                $r = array_combine($headers, str_getcsv($row));
                if($r["sciper"] == $_SESSION["user"]["uniqueid"]) {
                        $has_signed_up = true;
                        break;
                }
        }
}



$title = trim(shell_exec("ldapsearch -h ldap.epfl.ch -b o=epfl,c=ch -x uniqueIdentifier={$_SESSION["user"]["uniqueid"]} | grep personalTitle | head -n 1 | cut -d ' ' -f 2"));

echo "<h1>Inscription pour tester la fonctionalité notes d'IS-Academia (beta)</h1>\n";

echo "<p>" . ($title == "Monsieur" ? "Cher" : ($title == "Madame" ? "Chère" : "Bonjour")) . " {$_SESSION["user"]["firstname"]},</p>\n";

echo "<p>Si tu connais l'app PocketCampus, tu sais probablement qu'il t'est possible d'accéder à ton horaire de cours dans la section IS-Academia.</p>\n";

echo "<p>Nous souhaitons passer à la prochaine étape et te permettre de voir tes notes !</p>\n";

echo "<p>Nous cherchons des testeurs pour la version Beta sur Android !</p>\n";

echo "<p>Tu n'auras pas besoin d'installer de nouvelle applications.</p>\n";

echo "<p>Si tu es intéressé, tu n'as qu'à t'inscrire à l'aide du boutton ci-dessous, et la fonctionalité sera activée automatiquement.</p>\n";

echo "<p>Merci d'avance!</p>\n";

echo "<p>L'équipe PocketCampus</p>\n";

if($has_signed_up) {
	echo "<p style=\"color:#090;\">Tu es déjà inscris, merci!</p>\n";
} else {
	echo "<form method=\"post\" action=\"?\"> <input type=\"hidden\" name=\"optin\" value=\"1\"> <input type=\"submit\" value=\"S'inscrire\"> </form>\n";
}





//echo "<pre>\n";
//print_r($_POST);

//echo json_encode($ret);

?>
