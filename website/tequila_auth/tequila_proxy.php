<?php

require_once("tequila.php");

$oClient = new TequilaClient();

$ret = array();
$ret["status"] = 500;

if(!empty($_GET["key"])) {

	$attributes = $oClient->fetchAttributes($_GET["key"]);
	if($attributes) {
		$ret["status"] = 200;
		$ret["attributes"] = $attributes;
	}

} elseif(!empty($_GET["app_url"]) && !empty($_GET["app_name"])) {

	$oClient->SetApplicationName($_GET["app_name"]);
	$oClient->SetApplicationURL($_GET["app_url"]);
	$oClient->SetWantedAttributes(array('uniqueid','name','firstname','unit', 'unitid', 'where', 'group', 'email', 'title'));
	$oClient->SetAllowsFilter("categorie=Shibboleth|categorie=epfl-guests");

	$oClient->createRequest();
	$key = $oClient->GetKey();
	if($key) {
		$ret["status"] = 200;
		$ret["key"] = $key;
		$ret["redirect"] = $oClient->getAuthenticationUrl();
	}

}

echo json_encode($ret);

?>
